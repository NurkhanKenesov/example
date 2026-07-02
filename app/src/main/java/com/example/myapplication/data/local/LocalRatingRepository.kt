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

val Context.ratingDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "rating_store")

class LocalRatingRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : RatingRepository {

    private val leaderboardKey = stringPreferencesKey("leaderboard")

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    init {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadFromStore()
            if (_leaderboard.value.isEmpty()) {
                seedMockData()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.ratingDataStore.data.first()
        _leaderboard.value = parseLeaderboard(prefs[leaderboardKey])
    }

    private suspend fun saveToStore() {
        context.ratingDataStore.edit { prefs ->
            prefs[leaderboardKey] = JSONArray(_leaderboard.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private fun parseLeaderboard(json: String?): List<LeaderboardEntry> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                LeaderboardEntry(
                    rank = obj.optInt("rank", 0),
                    studentId = obj.optString("studentId", ""),
                    name = obj.optString("name", ""),
                    score = obj.optString("score", ""),
                    avatarUrl = obj.optString("avatarUrl").takeIf { it.isNotEmpty() },
                    trend = com.example.myapplication.data.models.TrendDirection.valueOf(obj.optString("trend", com.example.myapplication.data.models.TrendDirection.STABLE.name))
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getLeaderboard(period: RatingPeriod): Result<List<LeaderboardEntry>> = try {
        Result.success(_leaderboard.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getUserRank(userId: String): Result<LeaderboardEntry?> = try {
        Result.success(_leaderboard.value.find { it.studentId == userId })
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockData() {
        _leaderboard.value = MockData.leaderboard
        saveToStore()
    }
}
