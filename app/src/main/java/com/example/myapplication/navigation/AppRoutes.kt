package com.example.myapplication.navigation

import kotlinx.serialization.Serializable

@Serializable data object WelcomeRoute
@Serializable data object LoginRoute
@Serializable data object RegisterRoute
@Serializable data object HomeRoute
@Serializable data object ProfileRoute
@Serializable data object PlansRoute
@Serializable data object PlanDetailRoute
@Serializable data object FeedbackRoute
@Serializable data object MuscleFatigueRoute
@Serializable data object StatsRoute
@Serializable data object AIExplanationRoute
@Serializable data object ChatbotRoute
@Serializable data object StudentsRoute
@Serializable data object LMSAttendanceRoute
@Serializable data object AchievementsRoute
@Serializable data object QRScannerRoute
@Serializable data object SettingsRoute
@Serializable data object TheoryRoute
@Serializable data object QuizRoute
@Serializable data class QuizResultRoute(
    val score: Int = 0,
    val total: Int = 8
)
