package com.example.myapplication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

data class Plan(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Date? = null,
    val status: String = "pending"
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Plan = Plan(
            id = id,
            title = map["title"] as? String ?: "",
            description = map["description"] as? String ?: "",
            date = (map["date"] as? com.google.firebase.Timestamp)?.toDate(),
            status = map["status"] as? String ?: "pending"
        )
    }
    
    fun toMap(): Map<String, Any?> = mapOf(
        "title" to title,
        "description" to description,
        "date" to date,
        "status" to status
    )
}

class PlanRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    private fun getUserId(): String = auth.currentUser?.uid ?: ""
    
    suspend fun savePlan(plan: Plan): Result<Unit> = try {
        val docRef = if (plan.id.isBlank()) {
            db.collection("users").document(getUserId()).collection("plans").document()
        } else {
            db.collection("users").document(getUserId()).collection("plans").document(plan.id)
        }
        docRef.set(plan.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun getPlans(): Result<List<Plan>> = try {
        val snapshot = db.collection("users").document(getUserId())
            .collection("plans")
            .orderBy("date")
            .get()
            .await()
        val plans = snapshot.documents.map { 
            Plan.fromMap(it.id, it.data ?: emptyMap()) 
        }
        Result.success(plans)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun updatePlanStatus(planId: String, status: String): Result<Unit> = try {
        db.collection("users").document(getUserId())
            .collection("plans")
            .document(planId)
            .update("status", status)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}