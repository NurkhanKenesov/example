package com.example.myapplication

import com.example.myapplication.data.models.RoleConfig
import com.example.myapplication.data.models.MuscleGroupConfig
import com.example.myapplication.data.models.ExerciseTypeConfig

interface ConfigRepository {
    suspend fun getRoles(): Result<List<RoleConfig>>
    suspend fun getMuscleGroups(): Result<List<MuscleGroupConfig>>
    suspend fun getExerciseTypes(): Result<List<ExerciseTypeConfig>>
}
