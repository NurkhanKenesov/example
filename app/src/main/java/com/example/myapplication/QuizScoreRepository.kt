package com.example.myapplication

import java.util.Date

data class QuizScore(
    val id: String = "",
    val quizId: String = "health_basics",
    val score: Int = 0,
    val totalQuestions: Int = 10,
    val timestamp: Date? = null
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): QuizScore = QuizScore(
            id = id,
            quizId = map["quizId"] as? String ?: "health_basics",
            score = (map["score"] as? Long)?.toInt() ?: 0,
            totalQuestions = (map["totalQuestions"] as? Long)?.toInt() ?: 10,
            timestamp = map["timestamp"] as? Date
        )
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "quizId" to quizId,
        "score" to score,
        "totalQuestions" to totalQuestions,
        "timestamp" to timestamp
    )
}

interface QuizScoreRepository {
    suspend fun saveQuizScore(score: Int, totalQuestions: Int): Result<Unit>
    suspend fun getQuizScores(): Result<List<QuizScore>>
}
