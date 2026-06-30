package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ebony = Color(0xFF0F0F23)
private val EbonyAlpha40 = Color(0x660F0F23)
private val EbonyAlpha30 = Color(0x4D0F0F23)
private val PrimaryBlue = Color(0xFF6C63FF)
private val PrimaryViolet = Color(0xFF8B5CF6)
private val ScreenBgStart = Color(0xFFF0F2FF)
private val ScreenBgEnd = Color(0xFFF5F7FF)
private val AmberIconBg = Color(0x26FBBF24)
private val VioletIconBg = Color(0x266C63FF)
private val GreenIconBg = Color(0x264ADE80)
private val DividerColor = Color(0x0F000000)
private val CardBorderColor = Color(0x12000000)

data class DayTab(val shortName: String, val type: String)

@Composable
fun PlanDetailScreen(
    onBackClick: () -> Unit = {},
    onFeedbackClick: () -> Unit = {},
    onAIClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(ScreenBgStart, ScreenBgEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
        ) {
            TopNavBar(onBackClick = onBackClick, onAIClick = onAIClick)
            DayTabsRow(selectedTabIndex = selectedTabIndex, onTabSelected = { selectedTabIndex = it })
            Spacer(Modifier.height(8.dp))
            EmptyPlanState()
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onFeedbackClick) {
                Text("Обратная связь", color = PrimaryBlue)
            }
        }
    }
}

@Composable
private fun EmptyPlanState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "--",
            color = Ebony.copy(alpha = 0.3f),
            fontSize = 32.sp,
            fontWeight = FontWeight.W700
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Нет упражнений в плане",
            color = EbonyAlpha40,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun TopNavBar(onBackClick: () -> Unit, onAIClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "‹ Планы",
            color = PrimaryBlue,
            fontSize = 17.sp,
            fontWeight = FontWeight.W400,
            modifier = Modifier.clickable { onBackClick() },
        )
        Text(
            text = "--",
            color = Ebony,
            fontSize = 17.sp,
            fontWeight = FontWeight.W600,
        )
        Text(
            text = "🧠",
            color = PrimaryBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier.clickable { onAIClick() }
        )
    }
}

@Composable
private fun DayTabsRow(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val dayTabs = listOf(DayTab("--", "--"))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        dayTabs.forEachIndexed { index, tab ->
            DayTabItem(
                tab = tab,
                isSelected = index == selectedTabIndex,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(index) },
            )
        }
    }
}

@Composable
private fun DayTabItem(
    tab: DayTab,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bgModifier = if (isSelected) {
        Modifier.background(
            Brush.linearGradient(
                listOf(PrimaryBlue, PrimaryViolet),
                start = Offset(0f, 0f),
                end = Offset(300f, 300f),
            )
        )
    } else {
        Modifier
            .background(Color.White)
            .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp))
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(bgModifier)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = tab.shortName,
            color = if (isSelected) Ebony else EbonyAlpha40,
            fontSize = 13.sp,
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.Center,
        )
        Text(
            text = tab.type,
            color = if (isSelected) Ebony else EbonyAlpha40,
            fontSize = 10.sp,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SectionHeader(emoji: String, title: String) {
    Text(
        text = "$emoji $title",
        color = EbonyAlpha40,
        fontSize = 13.sp,
        fontWeight = FontWeight.W600,
        letterSpacing = 0.5.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    )
}

@Composable
private fun ExerciseCard(section: ExerciseSection) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 0.dp),
    ) {
        section.exercises.forEachIndexed { index, exercise ->
            ExerciseRow(
                exercise = exercise,
                showDivider = index < section.exercises.lastIndex,
            )
        }
    }
}

@Composable
private fun ExerciseRow(exercise: Exercise, showDivider: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ExerciseIcon(emoji = exercise.emoji, bg = exercise.iconBg)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                color = Ebony,
                fontSize = 15.sp,
                fontWeight = FontWeight.W600,
            )
            Text(
                text = exercise.description,
                color = EbonyAlpha40,
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${exercise.sets}×${exercise.reps}",
                color = Ebony,
                fontSize = 14.sp,
                fontWeight = FontWeight.W700,
                textAlign = TextAlign.End,
            )
            if (exercise.score != null) {
                Text(
                    text = "score ${"%.2f".format(exercise.score)}",
                    color = if (exercise.iconBg == VioletIconBg) PrimaryBlue else EbonyAlpha30,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.End,
                )
            }
        }
    }

    if (showDivider) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DividerColor),
        )
    }
}

@Composable
private fun ExerciseIcon(emoji: String, bg: Color) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, fontSize = 20.sp, textAlign = TextAlign.Center)
    }
}

@Preview(showSystemUi = true)
@Composable
fun PlanDetailScreenPreview() {
    PlanDetailScreen()
}