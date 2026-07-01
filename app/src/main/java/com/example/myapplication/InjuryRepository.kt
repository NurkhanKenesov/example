package com.example.myapplication

import com.example.myapplication.data.LocalAuthManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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

interface InjuryRepository {
    suspend fun saveInjury(injury: Injury): Result<Unit>
    suspend fun getInjuries(): Result<List<Injury>>
    suspend fun deleteInjury(injuryId: String): Result<Unit>
}

class InjuryRepositoryImpl(
    private val localAuthManager: LocalAuthManager,
    private val db: FirebaseFirestore
) : InjuryRepository {

    private suspend fun getUserId(): String =
        localAuthManager.getCurrentUser()?.uid ?: ""

    override suspend fun saveInjury(injury: Injury): Result<Unit> = try {
        val docRef = if (injury.id.isBlank()) {
            db.collection("users").document(getUserId()).collection("injuries").document()
        } else {
            db.collection("users").document(getUserId()).collection("injuries").document(injury.id)
        }
        docRef.set(injury.toMap()).await()
        Result.success(Unit)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getInjuries(): Result<List<Injury>> = try {
        val snapshot = db.collection("users").document(getUserId())
            .collection("injuries")
            .get()
            .await()
        val injuries = snapshot.documents.map {
            Injury.fromMap(it.id, it.data ?: emptyMap())
        }
        Result.success(injuries)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun deleteInjury(injuryId: String): Result<Unit> = try {
        db.collection("users").document(getUserId())
            .collection("injuries")
            .document(injuryId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }
}
