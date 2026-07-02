package com.example.myapplication.data.models

import org.json.JSONObject

data class DailyActivity(
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val steps: Int = 0,
    val calories: Int = 0,
    val activeMinutes: Int = 0,
    val distanceKm: Float = 0f
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "date" to date,
        "steps" to steps,
        "calories" to calories,
        "activeMinutes" to activeMinutes,
        "distanceKm" to distanceKm
    )
}
