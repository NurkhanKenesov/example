package com.example.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Design tokens
private val ColorPrimary = Color(0xFF6C63FF)
private val ColorSecondary = Color(0xFF8B5CF6)
private val ColorDark = Color(0xFF0F0F23)
private val ColorTextMuted = Color(0xFF0F0F23).copy(alpha = 0.50f)
private val ColorTextFaint = Color(0xFF0F0F23).copy(alpha = 0.40f)
private val ColorSurface = Color(0xFFF8F9FF)
private val ColorCardBorder = Color(0xFF000000).copy(alpha = 0.06f)
private val ColorPrimaryBorder = Color(0xFF6C63FF).copy(alpha = 0.20f)
private val ColorRed = Color(0xFFF87171)
private val ColorRedBg = Color(0xFFF87171).copy(alpha = 0.07f)
private val ColorRedBorder = Color(0xFFF87171).copy(alpha = 0.20f)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorGreenBg = Color(0xFF4ADE80).copy(alpha = 0.10f)

private val GradientPrimary = Brush.linearGradient(
    colors = listOf(ColorPrimary, ColorSecondary)
)
private val GradientBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))
)
private val GradientProgressBar = Brush.horizontalGradient(
    colors = listOf(ColorPrimary, ColorSecondary)
)
// 135-degree gradient: top-start to bottom-end
private val GradientAvatar = Brush.linearGradient(
    colors = listOf(ColorPrimary, ColorSecondary),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

@Composable
fun HomeScreen(
    onNavigateToPlans: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToChatbot: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground)
    ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                WelcomeHeader(onNavigateToProfile = onNavigateToProfile)
                Spacer(modifier = Modifier.height(20.dp))
                CreditStatusCard()
                Spacer(modifier = Modifier.height(16.dp))
                SectionLabel("Активность дня")
                Spacer(modifier = Modifier.height(8.dp))
                DailyActivityRow()
                Spacer(modifier = Modifier.height(12.dp))
                NextSessionCard()
                Spacer(modifier = Modifier.height(10.dp))
                InjuryWarningCard()
                Spacer(modifier = Modifier.height(20.dp))
                SectionLabel("Быстрые действия")
                Spacer(modifier = Modifier.height(12.dp))
                QuickActionButtons(
                    onNavigateToPlans = onNavigateToPlans,
                    onNavigateToChatbot = onNavigateToChatbot
                )
            }
        }
}

@Composable
private fun WelcomeHeader(onNavigateToProfile: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Добро пожаловать 👋",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ColorTextMuted
            )
            Text(
                text = "Amir Seitkali",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorDark
            )
        }
        AvatarBadge(
            initials = "AS",
            modifier = Modifier.clickable { onNavigateToProfile() }
        )
    }
}

@Composable
private fun AvatarBadge(initials: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(GradientAvatar),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CreditStatusCard() {
    val progress = 0.68f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6C63FF).copy(alpha = 0.08f),
                        Color.White
                    )
                )
            )
            .border(1.dp, ColorPrimaryBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularScoreIndicator(score = 68, total = 100, progress = progress)
            CreditStatusInfo(remaining = 32, progress = progress)
        }
    }
}

@Composable
private fun CircularScoreIndicator(score: Int, total: Int, progress: Float) {
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
            val arcSize = Size(radius * 2f, radius * 2f)

            // Track arc
            drawArc(
                color = Color.Black.copy(alpha = 0.10f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                color = ColorPrimary,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorDark,
                lineHeight = 28.sp
            )
            Text(
                text = "из $total баллов",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = ColorTextFaint,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CreditStatusInfo(remaining: Int, progress: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Статус зачета",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ColorDark
        )
        Text(
            text = "Осталось $remaining балла до автомата",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = ColorTextMuted
        )
        LinearProgressBar(progress = progress)
    }
}

@Composable
private fun LinearProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.08f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(GradientProgressBar)
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = ColorTextFaint,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun DailyActivityRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricCard(emoji = "👣", value = "8 430", label = "Шагов", modifier = Modifier.weight(1f))
        MetricCard(emoji = "🔥", value = "450", label = "Ккал", modifier = Modifier.weight(1f))
        MetricCard(emoji = "⏱️", value = "45", label = "Мин", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MetricCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ColorSurface)
            .border(1.dp, ColorCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = emoji, fontSize = 16.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorDark,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = ColorTextFaint,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NextSessionCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorGreenBg)
            .border(1.dp, ColorGreen.copy(alpha = 0.30f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorGreen.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📅", fontSize = 18.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Ближайшее занятие",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorGreen
                )
                Text(
                    text = "Сегодня, 14:30 • Волейбол",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorDark
                )
                Text(
                    text = "Спортивный зал №3",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = ColorTextMuted
                )
            }
        }
    }
}

@Composable
private fun InjuryWarningCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorRedBg)
            .border(1.dp, ColorRedBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚠️", fontSize = 16.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Активная травма",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorRed
                )
                Text(
                    text = "knee_sprain (knee) — до 2026-08-01",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = ColorTextMuted
                )
            }
        }
    }
}

@Composable
private fun QuickActionButtons(
    onNavigateToPlans: () -> Unit,
    onNavigateToChatbot: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Primary CTA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(GradientPrimary)
                .clickable { onNavigateToPlans() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🚀 Начать тренировку",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        // Secondary CTA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(ColorSurface)
                .border(1.dp, ColorPrimaryBorder, RoundedCornerShape(14.dp))
                .clickable { onNavigateToChatbot() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🤖 ИИ-Ассистент",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorDark,
                textAlign = TextAlign.Center
            )
        }
    }
}



