package com.example.myapplication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

data class Injury(
    val id: String = "",
    val type: String = "",
    val bodyPart: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val severity: String = "medium"
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Injury = Injury(
            id = id,
            type = map["type"] as? String ?: "",
            bodyPart = map["bodyPart"] as? String ?: "",
            startDate = (map["startDate"] as? com.google.firebase.Timestamp)?.toDate(),
            endDate = (map["endDate"] as? com.google.firebase.Timestamp)?.toDate(),
            severity = map["severity"] as? String ?: "medium"
        )
    }
    
    fun toMap(): Map<String, Any?> = mapOf(
        "type" to type,
        "bodyPart" to bodyPart,
        "startDate" to startDate,
        "endDate" to endDate,
        "severity" to severity
    )
}

class InjuryRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    private fun getUserId(): String = auth.currentUser?.uid ?: ""
    
    suspend fun saveInjury(injury: Injury): Result<Unit> = try {
        val docRef = if (injury.id.isBlank()) {
            db.collection("users").document(getUserId()).collection("injuries").document()
        } else {
            db.collection("users").document(getUserId()).collection("injuries").document(injury.id)
        }
        docRef.set(injury.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun getInjuries(): Result<List<Injury>> = try {
        val snapshot = db.collection("users").document(getUserId())
            .collection("injuries")
            .get()
            .await()
        val injuries = snapshot.documents.map { 
            Injury.fromMap(it.id, it.data ?: emptyMap()) 
        }
        Result.success(injuries)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun deleteInjury(injuryId: String): Result<Unit> = try {
        db.collection("users").document(getUserId())
            .collection("injuries")
            .document(injuryId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}