package com.example.myapplication

import com.example.myapplication.data.models.DailyActivity

interface ActivityRepository {
    suspend fun saveActivity(activity: DailyActivity): Result<Unit>
    suspend fun getHistory(userId: String, days: Int = 7): Result<List<DailyActivity>>
    suspend fun getToday(userId: String): Result<DailyActivity?>
}
