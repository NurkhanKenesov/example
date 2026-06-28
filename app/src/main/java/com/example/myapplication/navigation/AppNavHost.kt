package com.example.myapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.example.myapplication.*
import com.example.myapplication.ui.ProfileScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: UserProfileViewModel = viewModel()
) {
    var currentRole by remember { mutableStateOf(UserRole.Student) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val loginState    by authViewModel.loginState.collectAsStateWithLifecycle()
    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()

    val startDestination: Any = when {
        !authViewModel.isLoggedIn()           -> WelcomeRoute
        !profileViewModel.isProfileComplete() -> ProfileSetupRoute
        else                                  -> HomeRoute
    }

    val showBottomBar = currentDestination?.hasRoute(HomeRoute::class) == true ||
            currentDestination?.hasRoute(PlansRoute::class) == true ||
            currentDestination?.hasRoute(PlanDetailRoute::class) == true ||
            currentDestination?.hasRoute(LMSAttendanceRoute::class) == true ||
            currentDestination?.hasRoute(AchievementsRoute::class) == true ||
            currentDestination?.hasRoute(ProfileRoute::class) == true ||
            currentDestination?.hasRoute(StudentsRoute::class) == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val studentRouteMap = mapOf(
                        "Главная"  to HomeRoute,
                        "Планы"    to PlansRoute,
                        "Обучение" to LMSAttendanceRoute,
                        "Рейтинг"  to AchievementsRoute,
                        "Профиль"  to ProfileRoute
                    )
                    val teacherRouteMap = mapOf(
                        "Главная"  to HomeRoute,
                        "Студенты" to StudentsRoute,
                        "Планы"    to PlansRoute,
                        "Профиль"  to ProfileRoute
                    )
                    val routeMap = if (currentRole == UserRole.Teacher) teacherRouteMap else studentRouteMap
                    val teacherNavItems = listOf(
                        NavItem("🏠", "Главная"),
                        NavItem("👨‍🏫", "Студенты"),
                        NavItem("📅", "Планы"),
                        NavItem("👤", "Профиль")
                    )
                    val itemsToShow = if (currentRole == UserRole.Teacher) teacherNavItems else navItems

                    itemsToShow.forEach { item ->
                        val route = routeMap[item.label] ?: HomeRoute
                        val isSelected = currentDestination?.hasRoute(route::class) == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    if (route == HomeRoute) {
                                        navController.popBackStack(HomeRoute, inclusive = false)
                                    } else {
                                        navController.navigate(route) {
                                            popUpTo(HomeRoute::class) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            },
                            icon  = { Text(text = item.icon, fontSize = 20.sp) },
                            label = { Text(text = item.label, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
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
                    uiState = loginState,
                    onLoginClick = { email, password -> authViewModel.login(email, password) },
                    onLoginSuccess = {
                        val email = com.google.firebase.auth.FirebaseAuth.getInstance()
                            .currentUser?.email.orEmpty()
                        currentRole = deriveRoleFromEmail(email)
                        if (profileViewModel.isProfileComplete()) {
                            navController.navigate(HomeRoute) { popUpTo(LoginRoute) { inclusive = true } }
                        } else {
                            navController.navigate(ProfileSetupRoute) { popUpTo(LoginRoute) { inclusive = true } }
                        }
                    },
                    onErrorDismiss   = authViewModel::clearLoginError,
                    onBackClick      = { navController.popBackStack() },
                    onForgotPassword = { }
                )
            }
            composable<RegisterRoute> {
                RegisterScreen(
                    uiState          = registerState,
                    onRegisterClick  = { email, password, _, _ -> authViewModel.register(email, password) },
                    onRegisterSuccess = {
                        navController.navigate(ProfileSetupRoute) { popUpTo(RegisterRoute) { inclusive = true } }
                    },
                    onErrorDismiss = authViewModel::clearRegisterError,
                    onBackClick    = { navController.popBackStack() }
                )
            }

            // ── Profile Setup ─────────────────────────────────────────────────
            composable<ProfileSetupRoute> {
                ProfileSetupScreen(
                    onSetupComplete = {
                        navController.navigate(HomeRoute) { popUpTo(ProfileSetupRoute) { inclusive = true } }
                    },
                    viewModel = profileViewModel
                )
            }

            // ── Main ──────────────────────────────────────────────────────────
            composable<HomeRoute> {
                HomeScreen(
                    userRole                  = currentRole,
                    onNavigateToPlans         = { navController.navigate(PlansRoute) },
                    onNavigateToProfile       = { navController.navigate(ProfileRoute) },
                    onNavigateToChatbot       = { navController.navigate(ChatbotRoute) },
                    onNavigateToStudents      = { navController.navigate(StudentsRoute) },
                    onNavigateToLMSAttendance = { navController.navigate(LMSAttendanceRoute) },
                    onNavigateToQRScanner     = { navController.navigate(QRScannerRoute) },
                    onNavigateToAchievements  = { navController.navigate(AchievementsRoute) },
                    viewModel                 = profileViewModel
                )
            }
            composable<ProfileRoute> {
                ProfileScreen(
                    onBackClick               = { navController.popBackStack() },
                    onNavigateToMuscleFatigue = { navController.navigate(MuscleFatigueRoute) },
                    onNavigateToStats         = { navController.navigate(StatsRoute) },
                    onSettingsClick           = { navController.navigate(SettingsRoute) },
                    viewModel                 = profileViewModel
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
                    onBackClick     = { navController.popBackStack() },
                    onFeedbackClick = { navController.navigate(FeedbackRoute) },
                    onAIClick       = { navController.navigate(AIExplanationRoute) }
                )
            }
            composable<FeedbackRoute>      { ExerciseFeedbackScreen(onBackClick = { navController.popBackStack() }) }
            composable<MuscleFatigueRoute> { MuscleFatigueScreen(onBackClick = { navController.popBackStack() }) }
            composable<StatsRoute>         { InteractionStatsScreen(onBackClick = { navController.popBackStack() }) }
            composable<AIExplanationRoute> { AIExplanationScreen(onBackClick = { navController.popBackStack() }) }
            composable<ChatbotRoute>       { AIChatbotScreen(onBackClick = { navController.popBackStack() }) }
            composable<StudentsRoute>      { StudentsScreen(onBackClick = { navController.popBackStack() }) }
            composable<LMSAttendanceRoute> {
                LMSAttendanceScreen(
                    onBackClick           = { navController.popBackStack() },
                    onNavigateToQRScanner = { navController.navigate(QRScannerRoute) },
                    onNavigateToTheory    = { navController.navigate(TheoryRoute) }
                )
            }
            composable<AchievementsRoute> { AchievementsScreen(onBackClick = { navController.popBackStack() }) }
            composable<QRScannerRoute>    { QRScannerScreen(navController = navController) }
            composable<SettingsRoute>     { SettingsScreen(onBackClick = { navController.popBackStack() }) }
            composable<TheoryRoute> {
                TheoryScreen(
                    onStartQuiz = { navController.navigate(QuizRoute) },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<QuizRoute> {
                QuizScreen(
                    onQuizComplete = { score, total ->
                        navController.navigate(QuizResultRoute(score = score, total = total)) {
                            popUpTo(TheoryRoute) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<QuizResultRoute> {
                val route = backStackEntry?.toRoute<QuizResultRoute>() ?: return@composable
                QuizResultScreen(
                    score   = route.score,
                    total   = route.total,
                    onRetry = {
                        navController.navigate(QuizRoute) {
                            popUpTo(QuizResultRoute(score = route.score, total = route.total)) { inclusive = true }
                        }
                    },
                    onGoHome = { navController.popBackStack(LMSAttendanceRoute, inclusive = false) }
                )
            }
        }
    }
}

private fun deriveRoleFromEmail(email: String): UserRole {
    val lower = email.lowercase()
    return if (lower.contains("teacher") || lower.contains("prof")) UserRole.Teacher
    else UserRole.Student
}