package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.*
import com.example.myapplication.data.LocalAuthManager
import com.example.myapplication.data.MockData
import com.example.myapplication.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

val Context.configDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "config_store")

class LocalConfigRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : ConfigRepository {

    private val rolesKey = stringPreferencesKey("roles")
    private val musclesKey = stringPreferencesKey("muscles")
    private val typesKey = stringPreferencesKey("types")

    private val _roles = MutableStateFlow<List<RoleConfig>>(emptyList())
    val roles: StateFlow<List<RoleConfig>> = _roles.asStateFlow()

    private val _muscles = MutableStateFlow<List<MuscleGroupConfig>>(emptyList())
    val muscles: StateFlow<List<MuscleGroupConfig>> = _muscles.asStateFlow()

    private val _types = MutableStateFlow<List<ExerciseTypeConfig>>(emptyList())
    val types: StateFlow<List<ExerciseTypeConfig>> = _types.asStateFlow()

    init {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadFromStore()
            if (_roles.value.isEmpty() && _muscles.value.isEmpty() && _types.value.isEmpty()) {
                seedMockData()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.configDataStore.data.first()
        _roles.value = parseRoles(prefs[rolesKey])
        _muscles.value = parseMuscles(prefs[musclesKey])
        _types.value = parseTypes(prefs[typesKey])
    }

    private suspend fun saveRoles() {
        context.configDataStore.edit { prefs ->
            prefs[rolesKey] = JSONArray(_roles.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private suspend fun saveMuscles() {
        context.configDataStore.edit { prefs ->
            prefs[musclesKey] = JSONArray(_muscles.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private suspend fun saveTypes() {
        context.configDataStore.edit { prefs ->
            prefs[typesKey] = JSONArray(_types.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private fun parseRoles(json: String?): List<RoleConfig> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                RoleConfig(
                    id = obj.optString("id", ""),
                    name = obj.optString("name", ""),
                    permissions = (obj.optJSONArray("permissions") ?: JSONArray()).let { arr ->
                        (0 until arr.length()).map { j -> arr.optString(j) }
                    },
                    description = obj.optString("description", "")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseMuscles(json: String?): List<MuscleGroupConfig> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                MuscleGroupConfig(
                    id = obj.optString("id", ""),
                    name = obj.optString("name", ""),
                    emoji = obj.optString("emoji", ""),
                    description = obj.optString("description", "")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseTypes(json: String?): List<ExerciseTypeConfig> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                ExerciseTypeConfig(
                    id = obj.optString("id", ""),
                    name = obj.optString("name", ""),
                    emoji = obj.optString("emoji", ""),
                    description = obj.optString("description", "")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getRoles(): Result<List<RoleConfig>> = try {
        Result.success(_roles.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getMuscleGroups(): Result<List<MuscleGroupConfig>> = try {
        Result.success(_muscles.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getExerciseTypes(): Result<List<ExerciseTypeConfig>> = try {
        Result.success(_types.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockData() {
        _roles.value = MockData.roles
        _muscles.value = MockData.muscleGroupsConfig
        _types.value = MockData.exerciseTypesConfig
        saveRoles()
        saveMuscles()
        saveTypes()
    }
}
