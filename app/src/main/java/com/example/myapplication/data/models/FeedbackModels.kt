package com.example.myapplication.data.models

import org.json.JSONObject

data class ExerciseFeedback(
    val id: String = "",
    val exerciseId: String = "",
    val userId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "exerciseId" to exerciseId,
        "userId" to userId,
        "rating" to rating,
        "comment" to comment,
        "timestamp" to timestamp
    )
}

data class PlanFeedback(
    val id: String = "",
    val planId: String = "",
    val userId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "planId" to planId,
        "userId" to userId,
        "rating" to rating,
        "comment" to comment,
        "timestamp" to timestamp
    )
}
