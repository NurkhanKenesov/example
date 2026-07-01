package com.example.myapplication

import com.example.myapplication.data.LocalAuthManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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

interface QuizScoreRepository {
    suspend fun saveQuizScore(score: Int, totalQuestions: Int): Result<Unit>
    suspend fun getQuizScores(): Result<List<QuizScore>>
}

class QuizScoreRepositoryImpl(
    private val localAuthManager: LocalAuthManager,
    private val db: FirebaseFirestore
) : QuizScoreRepository {

    private suspend fun getUserId(): String =
        localAuthManager.getCurrentUser()?.uid ?: ""

    override suspend fun saveQuizScore(score: Int, totalQuestions: Int): Result<Unit> = try {
        val docRef = db.collection("testScores").document(getUserId())
            .collection("scores").document()
        val quizScore = QuizScore(score = score, totalQuestions = totalQuestions)
        docRef.set(quizScore.toMap()).await()
        Result.success(Unit)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getQuizScores(): Result<List<QuizScore>> = try {
        val snapshot = db.collection("testScores").document(getUserId())
            .collection("scores")
            .orderBy("timestamp")
            .get()
            .await()
        val scores = snapshot.documents.map {
            QuizScore.fromMap(it.id, it.data ?: emptyMap())
        }
        Result.success(scores)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }
}
