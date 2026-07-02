package com.example.myapplication

import com.example.myapplication.data.models.Achievement

interface AchievementRepository {
    suspend fun getAchievements(): Result<List<Achievement>>
    suspend fun unlockAchievement(achievementId: String): Result<Unit>
}
