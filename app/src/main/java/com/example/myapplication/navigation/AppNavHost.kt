package com.example.myapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.myapplication.*
import com.example.myapplication.ui.ProfileScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBottomBar = currentDestination?.hasRoute(HomeRoute::class) == true ||
            currentDestination?.hasRoute(PlansRoute::class) == true ||
            currentDestination?.hasRoute(LMSAttendanceRoute::class) == true ||
            currentDestination?.hasRoute(AchievementsRoute::class) == true ||
            currentDestination?.hasRoute(ProfileRoute::class) == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        Triple("Главная", "🏠", HomeRoute),
                        Triple("Планы", "📅", PlansRoute),
                        Triple("Обучение", "📚", LMSAttendanceRoute),
                        Triple("Рейтинг", "🏆", AchievementsRoute),
                        Triple("Профиль", "👤", ProfileRoute)
                    )
                    items.forEach { (label, icon, route) ->
                        val isSelected = currentDestination?.hasRoute(route::class) == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(HomeRoute::class) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Text(
                                    text = icon,
                                    fontSize = 20.sp
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = WelcomeRoute,
            modifier = modifier.padding(innerPadding)
        ) {
            composable<WelcomeRoute> {
                WelcomeScreen(
                    onSignIn = { navController.navigate(LoginRoute) },
                    onCreateAccount = { navController.navigate(RegisterRoute) }
                )
            }
            composable<LoginRoute> {
                LoginScreen(
                    onBackClick = { navController.popBackStack() },
                    onLoginSuccess = { _, _ ->
                        navController.navigate(HomeRoute) {
                            popUpTo(LoginRoute) {
                                inclusive = true
                            }
                        }
                    },
                    onForgotPassword = { }
                )
            }
            composable<RegisterRoute> {
                RegisterScreen(
                    onBackClick = { navController.popBackStack() },
                    onRegisterSuccess = { _, _, _, _ -> navController.navigate(HomeRoute) }
                )
            }
            composable<HomeRoute> {
                HomeScreen(
                    onNavigateToPlans = { navController.navigate(PlansRoute) },
                    onNavigateToProfile = { navController.navigate(ProfileRoute) },
                    onNavigateToChatbot = { navController.navigate(ChatbotRoute) }
                )
            }
            composable<ProfileRoute> {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToMuscleFatigue = { navController.navigate(MuscleFatigueRoute) },
                    onNavigateToStats = { navController.navigate(StatsRoute) }
                )
            }
            composable<PlansRoute> {
                PlanListScreen(
                    onPlanClick = { navController.navigate(PlanDetailRoute) },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<PlanDetailRoute> {
                PlanDetailScreen(
                    onBackClick = { navController.popBackStack() },
                    onFeedbackClick = { navController.navigate(FeedbackRoute) },
                    onAIClick = { navController.navigate(AIExplanationRoute) }
                )
            }
            composable<FeedbackRoute> {
                ExerciseFeedbackScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<MuscleFatigueRoute> {
                MuscleFatigueScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<StatsRoute> {
                InteractionStatsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<AIExplanationRoute> {
                AIExplanationScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<ChatbotRoute> {
                AIChatbotScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<StudentsRoute> {
                StudentsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<LMSAttendanceRoute> {
                LMSAttendanceScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToQRScanner = { navController.navigate(QRScannerRoute) }
                )
            }
            composable<AchievementsRoute> {
                AchievementsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<QRScannerRoute> {
                QRScannerScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
