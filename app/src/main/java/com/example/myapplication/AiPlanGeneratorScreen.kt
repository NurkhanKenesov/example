package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AiPlanGeneratorScreen(
    onBackClick: () -> Unit = {},
    viewModel: AiPlanGeneratorViewModel = koinViewModel()
) {
    val currentState by viewModel.state.collectAsStateWithLifecycle()
    var userGoal by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹",
                    color = Color(0xFF6C63FF),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.clickable { onBackClick() }
                )
                Text(
                    text = "✨ ИИ-Генератор плана",
                    color = Color(0xFF0F0F23),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700
                )
                Spacer(modifier = Modifier.width(20.dp))
            }

            val current = currentState
            when (current) {
                AiPlanState.Idle -> {
                    OutlinedTextField(
                        value = userGoal,
                        onValueChange = { userGoal = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Хочу похудеть за месяц, тренировки 3 раза в неделю...",
                                fontSize = 14.sp,
                                color = Color(0xFF0F0F23).copy(alpha = 0.4f)
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        minLines = 4
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (userGoal.isNotBlank()) {
                                viewModel.generatePlan(userGoal)
                            }
                        },
                        enabled = userGoal.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = ButtonDefaults.ContentPadding
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF6C63FF), Color(0xFF8B5CF6))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Сгенерировать план",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W700
                            )
                        }
                    }
                }

                AiPlanState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF6C63FF),
                                trackColor = Color(0xFF6C63FF).copy(alpha = 0.2f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "ИИ создаёт твой план...",
                                fontSize = 16.sp,
                                color = Color(0xFF0F0F23).copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                is AiPlanState.Success -> {
                    val plan = current.plan

                    Text(
                        text = plan.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W800,
                        color = Color(0xFF0F0F23)
                    )

                    if (plan.days.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            plan.days.forEach { day ->
                                Text(
                                    text = day,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF6C63FF).copy(alpha = 0.1f))
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = Color(0xFF6C63FF),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W600
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val grouped = plan.exercises.groupBy { it.section }
                        val warmup = grouped["warmup"] ?: emptyList()
                        val main = grouped["main"] ?: emptyList()
                        val cooldown = grouped["cooldown"] ?: emptyList()

                        if (warmup.isNotEmpty()) {
                            Text(text = "🔥 РАЗМИНКА", fontSize = 14.sp, fontWeight = FontWeight.W800, color = Color(0xFFFB923C))
                            warmup.forEach { exercise ->
                                ExerciseCard(exercise = exercise)
                            }
                        }

                        if (main.isNotEmpty()) {
                            Text(text = "💪 ОСНОВНАЯ ЧАСТЬ", fontSize = 14.sp, fontWeight = FontWeight.W800, color = Color(0xFF6C63FF))
                            main.forEach { exercise ->
                                ExerciseCard(exercise = exercise)
                            }
                        }

                        if (cooldown.isNotEmpty()) {
                            Text(text = "🧘 ЗАМИНКА", fontSize = 14.sp, fontWeight = FontWeight.W800, color = Color(0xFF60A5FA))
                            cooldown.forEach { exercise ->
                                ExerciseCard(exercise = exercise)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = ButtonDefaults.ContentPadding
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF6C63FF), Color(0xFF8B5CF6))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Сохранить план",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W700
                            )
                        }
                    }
                }

                is AiPlanState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Ошибка",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W700,
                                color = Color(0xFFF87171)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = current.message,
                                fontSize = 14.sp,
                                color = Color(0xFF0F0F23).copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.reset() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(text = "Повторить", color = Color.White, fontWeight = FontWeight.W600)
                            }
                        }
                    }
                }

                AiPlanState.Idle -> {}
            }
        }
    }
}

@Composable
private fun ExerciseCard(exercise: GeneratedExercise) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = exercise.emoji,
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF0F0F23)
                )
                if (exercise.description.isNotBlank()) {
                    Text(
                        text = exercise.description,
                        fontSize = 13.sp,
                        color = Color(0xFF0F0F23).copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Text(
                    text = "${exercise.sets} × ${exercise.reps}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF6C63FF),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
