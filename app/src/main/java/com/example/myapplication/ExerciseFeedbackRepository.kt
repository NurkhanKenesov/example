package com.example.myapplication

import com.example.myapplication.data.models.ExerciseFeedback

interface ExerciseFeedbackRepository {
    suspend fun saveFeedback(feedback: ExerciseFeedback): Result<Unit>
    suspend fun getFeedback(exerciseId: String): Result<List<ExerciseFeedback>>
}
