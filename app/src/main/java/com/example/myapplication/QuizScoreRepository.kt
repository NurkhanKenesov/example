package com.example.myapplication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
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
            timestamp = (map["timestamp"] as? com.google.firebase.Timestamp)?.toDate()
        )
    }
    
    fun toMap(): Map<String, Any?> = mapOf(
        "quizId" to quizId,
        "score" to score,
        "totalQuestions" to totalQuestions,
        "timestamp" to com.google.firebase.Timestamp.now()
    )
}

class QuizScoreRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    private fun getUserId(): String = auth.currentUser?.uid ?: ""
    
    suspend fun saveQuizScore(score: Int, totalQuestions: Int): Result<Unit> = try {
        val docRef = db.collection("testScores").document(getUserId())
            .collection("scores").document()
        val quizScore = QuizScore(score = score, totalQuestions = totalQuestions)
        docRef.set(quizScore.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun getQuizScores(): Result<List<QuizScore>> = try {
        val snapshot = db.collection("testScores").document(getUserId())
            .collection("scores")
            .orderBy("timestamp")
            .get()
            .await()
        val scores = snapshot.documents.map { 
            QuizScore.fromMap(it.id, it.data ?: emptyMap()) 
        }
        Result.success(scores)
    } catch (e: Exception) {
        Result.failure(e)
    }
}