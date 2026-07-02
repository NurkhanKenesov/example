package com.example.myapplication

import android.util.Log

object GeminiRepository {
    private const val TAG = "GeminiRepository"

    suspend fun sendMessage(
        message: String
    ): Result<String> = Result.failure(Exception("ИИ-генерация недоступна в офлайн-режиме"))

    suspend fun generateWorkoutPlan(
        profile: UserProfile
    ): Result<String> = Result.failure(Exception("ИИ-генерация недоступна в офлайн-режиме"))
}
