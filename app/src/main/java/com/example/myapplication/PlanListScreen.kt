package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val Ebony = Color(0xFF0F0F23)
private val EbonyAlpha40 = Color(0x660F0F23)
private val EbonyAlpha30 = Color(0x4D0F0F23)

private val GradientStart = Color(0xFF6C63FF)
private val GradientEnd = Color(0xFF8B5CF6)
private val ScreenBgStart = Color(0xFFF0F2FF)
private val ScreenBgEnd = Color(0xFFF5F7FF)

private val Green = Color(0xFF4ADE80)
private val GreenBg = Color(0x264ADE80)
private val Blue = Color(0xFF60A5FA)
private val BlueBg = Color(0x2660A5FA)
private val Amber = Color(0xFFFB923C)
private val AmberBg = Color(0x26FB923C)
private val Red = Color(0xFFF87171)
private val RedBg = Color(0x26F87171)

enum class PlanStatus(
    val emoji: String,
    val label: String,
    val badgeColor: Color,
    val badgeBg: Color,
) {
    COMPLETED("✅", "COMPLETED", Green, GreenBg),
    SCHEDULED("🕐", "SCHEDULED", Amber, AmberBg),
    DISCARDED("❌", "DISCARDED", Red, RedBg),
}

data class TrainingPlan(
    val id: Int,
    val name: String,
    val exerciseCount: Int,
    val date: String,
    val status: PlanStatus,
    val statusNote: String,
    val progressFraction: Float,
    val currentDayIndex: Int,
)

private val emptyPlans = emptyList<TrainingPlan>()

@Composable
fun PlanListScreen(
    onPlanClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val viewModel: PlansViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Map Firestore plans to UI model (placeholder - will show empty state)
    val plans = when (uiState) {
        is PlansUiState.Loaded -> emptyPlans
        else -> emptyPlans
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(ScreenBgStart, ScreenBgEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 3.5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹",
                    color = GradientStart,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.clickable { onBackClick() }
                )
                Text(
                    text = "📅 Мои планы",
                    color = Ebony,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.W800,
                    lineHeight = 39.1.sp,
                    modifier = Modifier.paddingFromBaseline(bottom = 15.59.dp),
                )
                Spacer(modifier = Modifier.width(20.dp))
            }

            Spacer(Modifier.height(8.dp))

            GenerateButton()

            Spacer(Modifier.height(8.dp))
            
            if (plans.isEmpty()) {
                EmptyPlansState()
            }
        }
    }
}

@Composable
private fun EmptyPlansState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "—",
            fontSize = 48.sp,
            fontWeight = FontWeight.W700,
            color = Ebony.copy(alpha = 0.2f)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Планы отсутствуют",
            fontSize = 16.sp,
            color = Ebony.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Создайте первый план или подождите, пока преподаватель назначит тренировку",
            fontSize = 13.sp,
            color = Ebony.copy(alpha = 0.4f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun GenerateButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("✨", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.W700)
            Text(
                text = "Сгенерировать новый план",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
            )
        }
    }
}

@Composable
private fun PlanCard(plan: TrainingPlan, onPlanClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onPlanClick() }
            .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusBadge(status = plan.status)
            Text(
                text = plan.date,
                color = EbonyAlpha40,
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = plan.name,
            color = Ebony,
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(2.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${plan.exerciseCount} упражнение",
                color = EbonyAlpha40,
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
            )
            Text("•", color = EbonyAlpha40, fontSize = 12.sp, fontWeight = FontWeight.W400)
            Text(
                text = plan.statusNote,
                color = when (plan.status) {
                    PlanStatus.COMPLETED -> Green
                    PlanStatus.SCHEDULED -> EbonyAlpha40
                    PlanStatus.DISCARDED -> Red
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
            )
        }

        Spacer(Modifier.height(12.dp))

        WeeklyProgressBar(
            progressFraction = plan.progressFraction,
            currentDayIndex = plan.currentDayIndex,
            barColor = when (plan.status) {
                PlanStatus.COMPLETED -> Green
                PlanStatus.SCHEDULED -> Blue
                PlanStatus.DISCARDED -> EbonyAlpha30
            },
        )
    }
}

@Composable
private fun StatusBadge(status: PlanStatus) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(status.badgeBg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = "${status.emoji} ${status.label}",
            color = status.badgeColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
        )
    }
}

@Composable
private fun WeeklyProgressBar(
    progressFraction: Float,
    currentDayIndex: Int,
    barColor: Color,
) {
    val trackColor = Color(0xFFF0F0F8)

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(trackColor),
        ) {
            if (progressFraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(barColor),
                )
            }

            if (currentDayIndex >= 0) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Spacer(Modifier.weight(progressFraction.coerceAtLeast(0f)))
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(barColor),
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DayLabel(
                label = "ПН",
                isActive = currentDayIndex == 0,
                barColor = barColor,
            )
            Text("СР", color = EbonyAlpha40, fontSize = 10.sp, fontWeight = FontWeight.W400)
            Text("ПТ", color = EbonyAlpha40, fontSize = 10.sp, fontWeight = FontWeight.W400)
        }
    }
}

@Composable
private fun DayLabel(label: String, isActive: Boolean, barColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = if (isActive) barColor else EbonyAlpha40,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.W600 else FontWeight.W400,
        )
        if (isActive) {
            Text("▶", color = barColor, fontSize = 8.sp)
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PlanListScreenPreview() {
    PlanListScreen()
}