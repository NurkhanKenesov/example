package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.*
import com.example.myapplication.data.LocalAuthManager
import com.example.myapplication.data.MockData
import com.example.myapplication.data.models.DailyActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

val Context.activityDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "activity_store")

class LocalActivityRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : ActivityRepository {

    private val activityKey = stringPreferencesKey("activities")

    private val _activities = MutableStateFlow<List<DailyActivity>>(emptyList())
    val activities: StateFlow<List<DailyActivity>> = _activities.asStateFlow()

    init {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadFromStore()
            if (_activities.value.isEmpty()) {
                seedMockData()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.activityDataStore.data.first()
        _activities.value = parseActivities(prefs[activityKey])
    }

    private suspend fun saveToStore() {
        context.activityDataStore.edit { prefs ->
            prefs[activityKey] = JSONArray(_activities.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private fun parseActivities(json: String?): List<DailyActivity> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                DailyActivity(
                    id = obj.optString("id", ""),
                    userId = obj.optString("userId", ""),
                    date = obj.optString("date", ""),
                    steps = obj.optInt("steps", 0),
                    calories = obj.optInt("calories", 0),
                    activeMinutes = obj.optInt("activeMinutes", 0),
                    distanceKm = obj.optDouble("distanceKm", 0.0).toFloat()
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun saveActivity(activity: DailyActivity): Result<Unit> = try {
        val current = _activities.value.toMutableList()
        val newActivity = if (activity.id.isBlank()) activity.copy(id = System.currentTimeMillis().toString()) else activity
        current.add(newActivity)
        _activities.value = current
        saveToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getHistory(userId: String, days: Int): Result<List<DailyActivity>> = try {
        val cutoff = System.currentTimeMillis() - days * 86_400_000L
        Result.success(_activities.value.filter { it.userId == userId && it.date.isNotEmpty() }.take(days))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getToday(userId: String): Result<DailyActivity?> = try {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        Result.success(_activities.value.find { it.userId == userId && it.date == today })
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockData() {
        _activities.value = MockData.dailyActivities
        saveToStore()
    }
}
