package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val PurplePrimary = Color(0xFF6C63FF)
private val DarkText = Color(0xFF0F0F23)

private val ScreenGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))
)

@Composable
fun WorkoutRecommendationScreen(
    profile: UserProfile = UserProfile(),
    onBackClick: () -> Unit = {}
) {
    var workoutPlan by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenGradient)
    ) {
        WorkoutNavBar(onBackClick = onBackClick, isLoading = isLoading)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            when {
                !profile.profileComplete -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Загрузи профиль сначала",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 16.sp
                        )
                    }
                }
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PurplePrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Генерация плана...",
                                color = DarkText,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }
                workoutPlan != null -> {
                    WorkoutPlanCard(plan = workoutPlan!!)
                }
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    workoutPlan = null
                    errorMessage = null
                    isLoading = true
                    GeminiRepository.generateWorkoutPlan(profile)
                        .onSuccess { workoutPlan = it }
                        .onFailure { errorMessage = "Ошибка: ${it.message ?: "Неизвестная ошибка"}" }
                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
        ) {
            Text(
                text = if (workoutPlan != null) "Обновить" else "Получить план тренировки",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun WorkoutNavBar(onBackClick: () -> Unit, isLoading: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onBackClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "‹ Назад",
                color = PurplePrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "План тренировок",
            color = DarkText,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(72.dp))
    }
}

@Composable
private fun WorkoutPlanCard(plan: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = plan,
            modifier = Modifier.padding(20.dp),
            color = DarkText,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    }
}