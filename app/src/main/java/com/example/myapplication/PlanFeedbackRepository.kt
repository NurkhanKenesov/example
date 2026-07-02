package com.example.myapplication

import com.example.myapplication.data.models.PlanFeedback

interface PlanFeedbackRepository {
    suspend fun saveFeedback(feedback: PlanFeedback): Result<Unit>
    suspend fun getFeedback(planId: String): Result<List<PlanFeedback>>
}
