package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Design tokens ──────────────────────────────────────────────────────────────

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

// ── Data models ────────────────────────────────────────────────────────────────

data class DayTab(val shortName: String, val type: String)

// ── Static data ────────────────────────────────────────────────────────────────

private val dayTabs = listOf(
    DayTab("ПН", "Сила"),
    DayTab("СР", "Сила"),
    DayTab("ПТ", "Кардио"),
)

private val warmupSection = ExerciseSection(
    emoji = "🔥",
    title = "РАЗМИНКА",
    exercises = listOf(
        Exercise("🏃", "Jumping Jacks", "Full body warmup", 2, 20, 0.87, AmberIconBg, 2, 20),
        Exercise("💫", "Arm Circles", "Shoulder warmup", 2, 15, 0.82, AmberIconBg, 2, 15),
    ),
)

private val mainSection = ExerciseSection(
    emoji = "💪",
    title = "ОСНОВНАЯ ЧАСТЬ",
    exercises = listOf(
        Exercise("🦵", "Push-ups", "Chest and triceps • Сложность 2", 3, 12, 0.91, VioletIconBg, 3, 12),
        Exercise("🤸", "Squats", "Quad and glute • Сложность 2", 3, 12, 0.88, VioletIconBg, 3, 12),
        Exercise("🦾", "Barbell Row", "Upper back • Сложность 4", 3, 8, 0.85, VioletIconBg, 3, 8),
    ),
)

private val cooldownSection = ExerciseSection(
    emoji = "🧘",
    title = "ЗАМИНКА",
    exercises = listOf(
        Exercise("🧘", "Hamstring Stretch", "Flexibility", 2, 1, null, GreenIconBg, 2, 1),
        Exercise("🐄", "Cat-Cow Stretch", "Spine mobility", 2, 10, null, GreenIconBg, 2, 10),
    ),
)

private val allSections = listOf(warmupSection, mainSection, cooldownSection)

// ── Screen ─────────────────────────────────────────────────────────────────────

@Composable
fun PlanDetailScreen(onBack: () -> Unit = {}) {
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
                .padding(bottom = 96.dp),
        ) {
            TopNavBar(onBack = onBack)
            DayTabsRow(selectedTabIndex = selectedTabIndex, onTabSelected = { selectedTabIndex = it })
            Spacer(Modifier.height(8.dp))
            allSections.forEach { section ->
                SectionHeader(emoji = section.emoji, title = section.title)
                Spacer(Modifier.height(8.dp))
                ExerciseCard(section = section)
                Spacer(Modifier.height(20.dp))
            }
        }

        PlanDetailBottomNavBar(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

// ── Top navigation bar ─────────────────────────────────────────────────────────

@Composable
private fun TopNavBar(onBack: () -> Unit) {
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
            modifier = Modifier.clickable { onBack() },
        )
        Text(
            text = "План #1248",
            color = Ebony,
            fontSize = 17.sp,
            fontWeight = FontWeight.W600,
        )
        Text(
            text = "🧠",
            color = PrimaryBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.W700,
        )
    }
}

// ── Day tabs ───────────────────────────────────────────────────────────────────

@Composable
private fun DayTabsRow(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
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

// ── Section header ─────────────────────────────────────────────────────────────

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

// ── Exercise card (white card containing rows) ─────────────────────────────────

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

// ── Exercise row ───────────────────────────────────────────────────────────────

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

// ── Bottom navigation bar ──────────────────────────────────────────────────────

@Composable
private fun PlanDetailBottomNavBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navItems.map { item ->
                val isActive = item.label == "Планы"
                NavBottomItem(icon = item.icon, label = item.label, isActive = isActive)
            }
        }
    }
}

@Composable
private fun NavBottomItem(icon: String, label: String, isActive: Boolean) {
    val labelColor = if (isActive) PrimaryBlue else EbonyAlpha40

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        Text(text = icon, fontSize = 22.sp)
        Text(
            text = label,
            color = labelColor,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.W600 else FontWeight.W400,
        )
    }
}

// ── Preview ────────────────────────────────────────────────────────────────────

@Preview(showSystemUi = true)
@Composable
fun PlanDetailScreenPreview() {
    PlanDetailScreen()
}
