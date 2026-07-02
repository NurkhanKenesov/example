package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.*
import com.example.myapplication.data.LocalAuthManager
import com.example.myapplication.data.MockData
import com.example.myapplication.data.models.PlanFeedback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

val Context.planFeedbackDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "plan_feedback_store")

class LocalPlanFeedbackRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : PlanFeedbackRepository {

    private val feedbackKey = stringPreferencesKey("feedback")

    private val _feedback = MutableStateFlow<List<PlanFeedback>>(emptyList())
    val feedback: StateFlow<List<PlanFeedback>> = _feedback.asStateFlow()

    init {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadFromStore()
            if (_feedback.value.isEmpty()) {
                seedMockData()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.planFeedbackDataStore.data.first()
        _feedback.value = parseFeedback(prefs[feedbackKey])
    }

    private suspend fun saveToStore() {
        context.planFeedbackDataStore.edit { prefs ->
            prefs[feedbackKey] = JSONArray(_feedback.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private fun parseFeedback(json: String?): List<PlanFeedback> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                PlanFeedback(
                    id = obj.optString("id", ""),
                    planId = obj.optString("planId", ""),
                    userId = obj.optString("userId", ""),
                    rating = obj.optInt("rating", 0),
                    comment = obj.optString("comment", ""),
                    timestamp = obj.optLong("timestamp", 0L)
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun saveFeedback(feedback: PlanFeedback): Result<Unit> = try {
        val current = _feedback.value.toMutableList()
        val newFeedback = if (feedback.id.isBlank()) feedback.copy(id = System.currentTimeMillis().toString()) else feedback
        current.add(newFeedback)
        _feedback.value = current
        saveToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getFeedback(planId: String): Result<List<PlanFeedback>> = try {
        Result.success(_feedback.value.filter { it.planId == planId })
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockData() {
        _feedback.value = MockData.planFeedbacks
        saveToStore()
    }
}
