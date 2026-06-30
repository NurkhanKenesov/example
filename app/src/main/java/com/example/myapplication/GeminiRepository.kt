package com.example.myapplication

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiRepository {
    private const val TAG = "GeminiRepository"

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    suspend fun sendMessage(
        message: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = model.generateContent(message)
            Result.success(response.text ?: "")
        } catch (e: Exception) {
            Log.e(TAG, "sendMessage error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun generateWorkoutPlan(
        profile: UserProfile
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "generateWorkoutPlan - profile: ${profile.name}")
            val prompt = """
                Составь план тренировки на неделю для ученика: возраст ${profile.age}, пол ${profile.gender.displayName}, 
                рост ${profile.heightCm}см, вес ${profile.weightKg}кг, ИМТ ${profile.bmi}, медгруппа ${profile.medicalGroup.displayName}. 
                Учитывай медицинские ограничения. Отвечай по-русски.
            """.trimIndent()

            val response = model.generateContent(prompt)
            Result.success(response.text ?: "")
        } catch (e: Exception) {
            Log.e(TAG, "generateWorkoutPlan error: ${e.message}", e)
            Result.failure(e)
        }
    }
}