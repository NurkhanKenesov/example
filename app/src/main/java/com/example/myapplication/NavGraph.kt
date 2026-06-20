package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onSignIn = { navController.navigate("login") },
                onCreateAccount = { navController.navigate("register") }
            )
        }
        composable("login") {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = { _, _ -> navController.navigate("home") },
                onForgotPassword = { }
            )
        }
        composable("register") {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = { _, _, _, _ -> navController.navigate("home") }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToPlans = { navController.navigate("plans") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToChatbot = { navController.navigate("chatbot") }
            )
        }
        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToMuscleFatigue = { navController.navigate("muscle_fatigue") },
                onNavigateToStats = { navController.navigate("stats") }
            )
        }
        composable("plans") {
            PlanListScreen(
                onPlanClick = { navController.navigate("plan_detail") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("plan_detail") {
            PlanDetailScreen(
                onBackClick = { navController.popBackStack() },
                onFeedbackClick = { navController.navigate("feedback") },
                onAIClick = { navController.navigate("ai_explanation") }
            )
        }
        composable("feedback") {
            ExerciseFeedbackScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("muscle_fatigue") {
            MuscleFatigueScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("stats") {
            InteractionStatsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("ai_explanation") {
            AIExplanationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("chatbot") {
            AIChatbotScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("students") {
            StudentsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("lms_attendance") {
            LMSAttendanceScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("achievements") {
            AchievementsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("qr_scanner") {
            QRScannerScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
