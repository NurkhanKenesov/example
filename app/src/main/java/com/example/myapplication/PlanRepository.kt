package com.example.myapplication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.util.Date

data class Plan(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Date? = null,
    val status: String = "pending",
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

data class WorkoutExercise(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val emoji: String = "🏃‍♂️",
    val videoUrl: String? = null
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): WorkoutExercise = WorkoutExercise(
            id = id,
            name = map["name"] as? String ?: "",
            description = map["description"] as? String ?: "",
            sets = (map["sets"] as? Long)?.toInt() ?: 0,
            reps = (map["reps"] as? Long)?.toInt() ?: 0,
            emoji = map["emoji"] as? String ?: "🏃‍♂️",
            videoUrl = map["videoUrl"] as? String
        )
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "description" to description,
        "sets" to sets,
        "reps" to reps,
        "emoji" to emoji,
        "videoUrl" to videoUrl
    )
}

data class WorkoutPlan(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val targetMedicalGroup: MedicalGroup = MedicalGroup.BASIC,
    val exercises: List<WorkoutExercise> = emptyList()
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): WorkoutPlan {
            @Suppress("UNCHECKED_CAST")
            val exercisesList = (map["exercises"] as? List<Map<String, Any?>>)
                ?.mapIndexedNotNull { index, exerciseMap ->
                    WorkoutExercise(
                        id = (exerciseMap["id"] as? String) ?: "ex_$index",
                        name = exerciseMap["name"] as? String ?: "",
                        description = exerciseMap["description"] as? String ?: "",
                        sets = (exerciseMap["sets"] as? Long)?.toInt() ?: 0,
                        reps = (exerciseMap["reps"] as? Long)?.toInt() ?: 0,
                        emoji = exerciseMap["emoji"] as? String ?: "🏃‍♂️",
                        videoUrl = exerciseMap["videoUrl"] as? String
                    )
                } ?: emptyList()

            val groupName = map["targetMedicalGroup"] as? String ?: return WorkoutPlan(id = id)
            val group = MedicalGroup.entries.firstOrNull { it.name == groupName } ?: MedicalGroup.BASIC

            return WorkoutPlan(
                id = id,
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                targetMedicalGroup = group,
                exercises = exercisesList
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "title" to title,
        "description" to description,
        "targetMedicalGroup" to targetMedicalGroup.name,
        "exercises" to exercises.map {
            mapOf(
                "id" to it.id,
                "name" to it.name,
                "description" to it.description,
                "sets" to it.sets,
                "reps" to it.reps,
                "emoji" to it.emoji,
                "videoUrl" to it.videoUrl
            )
        }
    )
}

interface PlanRepository {
    suspend fun savePlan(plan: Plan): Result<Unit>
    suspend fun getPlans(): Result<List<Plan>>
    suspend fun updatePlanStatus(planId: String, status: String): Result<Unit>
    suspend fun getWorkoutPlans(): Result<List<WorkoutPlan>>
    suspend fun getWorkoutPlan(planId: String): Result<WorkoutPlan>
    suspend fun saveUserWorkoutPlan(plan: WorkoutPlan): Result<Unit>
    suspend fun getUserWorkoutPlan(): Result<WorkoutPlan?>
    suspend fun getUserMedicalGroup(): Result<MedicalGroup>
    suspend fun seedWorkoutPlans(): Result<Unit>
}

class PlanRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : PlanRepository {

    private fun getUserId(): String = auth.currentUser?.uid ?: ""

    override suspend fun savePlan(plan: Plan): Result<Unit> = try {
        val docRef = if (plan.id.isBlank()) {
            db.collection("users").document(getUserId()).collection("plans").document()
        } else {
            db.collection("users").document(getUserId()).collection("plans").document(plan.id)
        }
        docRef.set(plan.toMap()).await()
        Result.success(Unit)
    } catch (e: FirebaseAuthException) {
        Result.failure(DataError.Auth(e))
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getPlans(): Result<List<Plan>> = try {
        val snapshot = db.collection("users").document(getUserId())
            .collection("plans")
            .orderBy("date")
            .get()
            .await()
        val plans = snapshot.documents.map {
            Plan.fromMap(it.id, it.data ?: emptyMap())
        }
        Result.success(plans)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun updatePlanStatus(planId: String, status: String): Result<Unit> = try {
        db.collection("users").document(getUserId())
            .collection("plans")
            .document(planId)
            .update("status", status)
            .await()
        Result.success(Unit)
    } catch (e: FirebaseAuthException) {
        Result.failure(DataError.Auth(e))
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getWorkoutPlans(): Result<List<WorkoutPlan>> = try {
        val snapshot = db.collection("workout_plans")
            .get()
            .await()
        val plans = snapshot.documents.map {
            WorkoutPlan.fromMap(it.id, it.data ?: emptyMap())
        }
        Result.success(plans)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getWorkoutPlan(planId: String): Result<WorkoutPlan> = try {
        val doc = db.collection("workout_plans")
            .document(planId)
            .get()
            .await()
        if (doc.exists()) {
            Result.success(WorkoutPlan.fromMap(doc.id, doc.data ?: emptyMap()))
        } else {
            Result.failure(DataError.NotFound("План не найден"))
        }
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun saveUserWorkoutPlan(plan: WorkoutPlan): Result<Unit> = try {
        val docRef = if (plan.id.isBlank()) {
            db.collection("users").document(getUserId()).collection("plans").document()
        } else {
            db.collection("users").document(getUserId()).collection("plans").document(plan.id)
        }
        docRef.set(plan.toMap()).await()
        Result.success(Unit)
    } catch (e: FirebaseAuthException) {
        Result.failure(DataError.Auth(e))
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getUserWorkoutPlan(): Result<WorkoutPlan?> = try {
        val snapshot = db.collection("users").document(getUserId())
            .collection("plans")
            .limit(1)
            .get()
            .await()
        if (snapshot.documents.isEmpty()) {
            Result.success(null)
        } else {
            val doc = snapshot.documents[0]
            Result.success(WorkoutPlan.fromMap(doc.id, doc.data ?: emptyMap()))
        }
    } catch (e: FirebaseAuthException) {
        Result.failure(DataError.Auth(e))
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getUserMedicalGroup(): Result<MedicalGroup> = try {
        val uid = getUserId()
        val doc = db.collection("users").document(uid).get().await()
        val groupName = doc.getString("medicalGroup") ?: "BASIC"
        val group = MedicalGroup.entries.firstOrNull { it.name == groupName }
            ?: MedicalGroup.entries.firstOrNull { it.displayName == groupName }
            ?: MedicalGroup.BASIC
        Result.success(group)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun seedWorkoutPlans(): Result<Unit> {
        if (!isTeacher()) {
            return Result.failure(DataError.Validation("Только преподаватель может добавить планы"))
        }
        return try {
            val basicPlan = WorkoutPlan(
                id = "basic_plan_01",
                title = "Базовая тренировка",
                description = "Упражнения для основной медицинской группы",
                targetMedicalGroup = MedicalGroup.BASIC,
                exercises = listOf(
                    WorkoutExercise(id = "ex_1", name = "Ходьба на месте", description = "Легкая разминка", sets = 1, reps = 60),
                    WorkoutExercise(id = "ex_2", name = "Приседания", description = "Без веса, медленно", sets = 2, reps = 12),
                    WorkoutExercise(id = "ex_3", name = "Отжимания от стены", description = "Для начала", sets = 2, reps = 10),
                    WorkoutExercise(id = "ex_4", name = "Планка", description = "На коленях, на дыша", sets = 2, reps = 30)
                )
            )
            val preparatoryPlan = WorkoutPlan(
                id = "preparatory_plan_01",
                title = "Подготовительная тренировка",
                description = "Умеренная физическая нагрузка",
                targetMedicalGroup = MedicalGroup.PREPARATORY,
                exercises = listOf(
                    WorkoutExercise(id = "ex_1", name = "Быстрая ходьба", description = "На месте 3 минуты", sets = 1, reps = 180),
                    WorkoutExercise(id = "ex_2", name = "Приседания", description = "С приседанием до пар", sets = 3, reps = 15),
                    WorkoutExercise(id = "ex_3", name = "Отжимания", description = "От колен, медленно", sets = 3, reps = 12),
                    WorkoutExercise(id = "ex_4", name = "Планка", description = "На лодыжках", sets = 3, reps = 45),
                    WorkoutExercise(id = "ex_5", name = "Махи руками", description = "Для плеч", sets = 2, reps = 20)
                )
            )
            val specialPlan = WorkoutPlan(
                id = "special_plan_01",
                title = "Спецгруппа - Интенсив",
                description = "Для СМГ медицинской группы",
                targetMedicalGroup = MedicalGroup.SPECIAL,
                exercises = listOf(
                    WorkoutExercise(id = "ex_1", name = "Бег на месте", description = "Интенсивно", sets = 2, reps = 120),
                    WorkoutExercise(id = "ex_2", name = "Приседания с прыжком", description = "Высокое подъём", sets = 3, reps = 20),
                    WorkoutExercise(id = "ex_3", name = "Отжимания от пола", description = "Классические", sets = 3, reps = 15),
                    WorkoutExercise(id = "ex_4", name = "Планка", description = "На руках", sets = 3, reps = 60),
                    WorkoutExercise(id = "ex_5", name = "Берпи", description = "Сокращённая версия", sets = 2, reps = 10)
                )
            )
            db.collection("workout_plans").document(basicPlan.id).set(basicPlan.toMap()).await()
            db.collection("workout_plans").document(preparatoryPlan.id).set(preparatoryPlan.toMap()).await()
            db.collection("workout_plans").document(specialPlan.id).set(specialPlan.toMap()).await()
            Result.success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Result.failure(DataError.Network(e))
        } catch (e: Exception) {
            Result.failure(DataError.Unknown(e))
        }
    }

    private fun isTeacher(): Boolean {
        val email = auth.currentUser?.email ?: return false
        val lower = email.lowercase()
        return lower.contains("teacher") || lower.contains("prof")
    }
}
