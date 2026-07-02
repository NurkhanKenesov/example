package com.example.myapplication

import com.example.myapplication.data.models.LeaderboardEntry
import com.example.myapplication.data.models.RatingPeriod

interface RatingRepository {
    suspend fun getLeaderboard(period: RatingPeriod = RatingPeriod.ALL_TIME): Result<List<LeaderboardEntry>>
    suspend fun getUserRank(userId: String): Result<LeaderboardEntry?>
}
