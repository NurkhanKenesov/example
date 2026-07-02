package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.*
import com.example.myapplication.data.LocalAuthManager
import com.example.myapplication.data.MockData
import com.example.myapplication.data.models.Achievement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

val Context.achievementDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "achievement_store")

class LocalAchievementRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : AchievementRepository {

    private val achievementsKey = stringPreferencesKey("achievements")

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    init {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadFromStore()
            if (_achievements.value.isEmpty()) {
                seedMockData()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.achievementDataStore.data.first()
        _achievements.value = parseAchievements(prefs[achievementsKey])
    }

    private suspend fun saveToStore() {
        context.achievementDataStore.edit { prefs ->
            prefs[achievementsKey] = JSONArray(_achievements.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private fun parseAchievements(json: String?): List<Achievement> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Achievement(
                    id = obj.optString("id", ""),
                    emoji = obj.optString("emoji", ""),
                    name = obj.optString("name", ""),
                    description = obj.optString("description", ""),
                    unlocked = obj.optBoolean("unlocked", true)
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getAchievements(): Result<List<Achievement>> = try {
        Result.success(_achievements.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun unlockAchievement(achievementId: String): Result<Unit> = try {
        val current = _achievements.value.map {
            if (it.id == achievementId) it.copy(unlocked = true) else it
        }
        _achievements.value = current
        saveToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockData() {
        _achievements.value = MockData.achievements
        saveToStore()
    }
}
