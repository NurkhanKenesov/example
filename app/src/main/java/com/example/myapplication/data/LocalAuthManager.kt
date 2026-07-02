package com.example.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_store")

data class LocalUser(
    val uid: String,
    val email: String,
    val passwordHash: String,
    val role: String = "Student"
)

class LocalAuthManager(private val context: Context) {

    private val usersKey = stringPreferencesKey("local_users")
    private val currentUidKey = stringPreferencesKey("current_uid")

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun generateUid(email: String): String = sha256(email).take(28)

    private suspend fun getUsers(): List<LocalUser> {
        val prefs = context.authDataStore.data.first()
        val json = prefs[usersKey] ?: return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                LocalUser(
                    uid = obj.getString("uid"),
                    email = obj.getString("email"),
                    passwordHash = obj.getString("passwordHash"),
                    role = obj.optString("role", "Student")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private suspend fun saveUsers(users: List<LocalUser>) {
        val array = JSONArray()
        users.forEach { user ->
            array.put(JSONObject().apply {
                put("uid", user.uid)
                put("email", user.email)
                put("passwordHash", user.passwordHash)
                put("role", user.role)
            })
        }
        context.authDataStore.edit { it[usersKey] = array.toString() }
    }

    suspend fun register(email: String, password: String, role: String = "Student"): Result<LocalUser> {
        val users = getUsers()
        if (users.any { it.email.equals(email.trim(), ignoreCase = true) }) {
            return Result.failure(Exception("Этот email уже зарегистрирован"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Пароль слишком короткий (минимум 6 символов)"))
        }
        val newUser = LocalUser(
            uid = generateUid(email.trim().lowercase()),
            email = email.trim().lowercase(),
            passwordHash = sha256(password),
            role = role
        )
        saveUsers(users + newUser)
        setCurrentUser(newUser.uid)
        return Result.success(newUser)
    }

    suspend fun login(email: String, password: String): Result<LocalUser> {
        val users = getUsers()
        val user = users.find { it.email.equals(email.trim(), ignoreCase = true) }
            ?: return Result.failure(Exception("Пользователь не найден"))
        if (user.passwordHash != sha256(password)) {
            return Result.failure(Exception("Неверный пароль"))
        }
        setCurrentUser(user.uid)
        return Result.success(user)
    }

    suspend fun logout() {
        context.authDataStore.edit { it.remove(currentUidKey) }
    }

    suspend fun getCurrentUser(): LocalUser? {
        val prefs = context.authDataStore.data.first()
        val uid = prefs[currentUidKey] ?: return null
        return getUsers().find { it.uid == uid }
    }

    fun isLoggedIn(): Boolean = false

    suspend fun isLoggedInSuspend(): Boolean = getCurrentUser() != null

    suspend fun updateUserRole(uid: String, role: String) {
        val users = getUsers().map {
            if (it.uid == uid) it.copy(role = role) else it
        }
        saveUsers(users)
    }

    private suspend fun setCurrentUser(uid: String) {
        context.authDataStore.edit { it[currentUidKey] = uid }
    }
}
