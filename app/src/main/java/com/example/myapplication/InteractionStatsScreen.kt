package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

private val ColorPurple = Color(0xFF6C63FF)
private val ColorPurpleBorder = Color(0x336C63FF)
private val ColorPurpleBg = Color(0x146C63FF)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorGreenBorder = Color(0x334ADE80)
private val ColorGreenBg = Color(0x124ADE80)
private val ColorYellow = Color(0xFFFBBF24)
private val ColorYellowBorder = Color(0x33FBBF24)
private val ColorYellowBg = Color(0x12FBBF24)
private val ColorRed = Color(0xFFF87171)
private val ColorTextDark = Color(0xFF0F0F23)
private val ColorTextMuted = Color(0x660F0F23)
private val ColorSectionLabel = Color(0x4D0F0F23)
private val ColorDivider = Color(0x0F000000)
private val ColorCardBg = Color(0xFFFFFFFF)

private data class StatCard(
    val value: String,
    val label: String,
    val valueColor: Color,
    val bgColor: Color
)

private data class ExerciseStat(
    val emoji: String,
    val emojiBg: Color,
    val name: String,
    val attempts: String,
    val percentage: String,
    val difficulty: String,
    val percentageColor: Color
)

private val statCards = listOf(
    StatCard("87%", "Выполнение", ColorPurple, ColorPurpleBg),
    StatCard("3.1", "Ср. сложность", ColorGreen, ColorGreenBg),
    StatCard("0.92", "Set ratio", ColorYellow, ColorYellowBg)
)

private val exercises = listOf(
    ExerciseStat("🦵", Color(0x1A4ADE80), "Push-ups", "9 попыток", "95%", "Normal", ColorGreen),
    ExerciseStat("🤸", Color(0x1A6C63FF), "Jumping Jacks", "8 попыток", "100%", "Easy", ColorGreen),
    ExerciseStat("🏋️", Color(0x1AFBBF24), "Squats", "7 попыток", "78%", "Hard", ColorYellow),
    ExerciseStat("💪", Color(0x1AF87171), "Pull-ups", "6 попыток", "55%", "Very Hard", ColorRed),
    ExerciseStat("🧘", Color(0x1A4ADE80), "Plank", "8 попыток", "90%", "Normal", ColorGreen)
)

@Composable
fun InteractionStatsScreen(onBack: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Nav bar with back navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹ Профиль",
                    color = ColorPurple,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack
                    )
                )
            }

            // Large title
            Text(
                text = "📊 Статистика",
                color = ColorTextDark,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 39.sp,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 3.5.dp,
                    bottom = 15.dp
                )
            )

            // Summary stat cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                statCards.forEach { card ->
                    StatCardItem(card = card, modifier = Modifier.weight(1f))
                }
            }

            // Section header
            Text(
                text = "ПО УПРАЖНЕНИЯМ",
                color = ColorSectionLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Exercise list card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ColorCardBg)
            ) {
                exercises.forEachIndexed { index, exercise ->
                    ExerciseRowItem(exercise = exercise)
                    if (index < exercises.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(0.5.dp)
                                .background(ColorDivider)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatCardItem(card: StatCard, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(card.bgColor)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = card.value,
            color = card.valueColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = card.label,
            color = ColorTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ExerciseRowItem(exercise: ExerciseStat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(exercise.emojiBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = exercise.emoji,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                color = ColorTextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = exercise.attempts,
                color = ColorTextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = exercise.percentage,
                color = exercise.percentageColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = exercise.difficulty,
                color = ColorTextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 808)
@Composable
fun InteractionStatsScreenPreview() {
    InteractionStatsScreen()
}
