package com.example.myapplication.data.models

import org.json.JSONObject

data class LeaderboardEntry(
    val rank: Int = 0,
    val studentId: String = "",
    val name: String = "",
    val score: String = "",
    val avatarUrl: String? = null,
    val trend: TrendDirection = TrendDirection.STABLE
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "rank" to rank,
        "studentId" to studentId,
        "name" to name,
        "score" to score,
        "avatarUrl" to avatarUrl,
        "trend" to trend.name
    )
}

enum class TrendDirection(val displayName: String) {
    UP("▲"),
    DOWN("▼"),
    STABLE("●")
}

enum class RatingPeriod(val displayName: String) {
    WEEK("Неделя"),
    MONTH("Месяц"),
    ALL_TIME("Все время")
}
