package com.example.myapplication

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
            date = map["date"] as? Date,
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
