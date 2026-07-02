package com.example.myapplication.data.models

data class DashboardStats(
    val todaySteps: Int = 0,
    val weeklyGoalProgress: Int = 0,
    val attendanceRate: Int = 0,
    val averageScore: String = "0/4",
    val upcomingSessions: Int = 0,
    val pendingTasks: Int = 0
)

data class TrainingDayStat(
    val dayName: String = "",
    val completed: Int = 0,
    val total: Int = 0
)
