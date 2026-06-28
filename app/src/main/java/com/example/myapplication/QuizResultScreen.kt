package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorDark = Color(0xFF0F0F23)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorYellow = Color(0xFFFBBF24)
private val ColorBackground = Color(0xFFF5F7FF)
private val ColorSurface = Color(0xFFF0F2FF)
private val ColorCardBg = Color(0xFFFFFFFF)

private val PASS_THRESHOLD = 6

@Composable
fun QuizResultScreen(
    score: Int = 0,
    total: Int = 8,
    onRetry: () -> Unit = {},
    onGoHome: () -> Unit = {}
) {
    val passed = score >= PASS_THRESHOLD

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(ColorSurface, ColorBackground))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Result card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Result emoji
                    Text(
                        text = if (passed) "🎉" else "😅",
                        fontSize = 64.sp,
                        textAlign = TextAlign.Center
                    )

                    // Score circle
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(60.dp))
                            .background(
                                if (passed) ColorGreen.copy(alpha = 0.1f) else ColorYellow.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$score",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (passed) ColorGreen else ColorYellow
                            )
                            Text(
                                text = "из $total",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = ColorDark.copy(alpha = 0.4f)
                            )
                        }
                    }

                    Text(
                        text = if (passed) "Отлично!" else "Хорошая попытка",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorDark,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (passed) {
                            "Вы ответили правильно на $score из $total вопросов!\n+50 баллов получено!"
                        } else {
                            "Вы ответили правильно на $score из $total.\nПопробуйте ещё раз!"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = ColorDark.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    // Pass/fail badge
                    if (passed) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ColorGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Зачёт сдан ✅",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorGreen
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ColorYellow.copy(alpha = 0.12f))
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Нужно набрать $PASS_THRESHOLD+ правильных",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorYellow
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimary
                )
            ) {
                Text(
                    text = "Пройти снова 🔄",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ColorPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(ColorPrimary, ColorPrimary))
                )
            ) {
                Text(
                    text = "На главную 🏠",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizResultScreenPreview() {
    QuizResultScreen(score = 7, total = 8)
}
