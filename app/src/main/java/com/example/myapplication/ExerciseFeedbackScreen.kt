package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Design tokens
private val Purple = Color(0xFF6C63FF)
private val PurpleBorder = Color(0x336C63FF)
private val PurpleDim = Color(0x206C63FF)
private val Green = Color(0xFF4ADE80)
private val Red = Color(0xFFF87171)
private val RedLight = Color(0x18F87171)
private val RedBorder = Color(0x4DF87171)
private val Dark = Color(0xFF0F0F23)
private val DarkMuted = Color(0x660F0F23)
private val InputBg = Color(0xFFF5F7FF)
private val White = Color(0xFFFFFFFF)
private val NavBg = Color(0xFFF8F9FF)

private val cardGradient = Brush.linearGradient(
    colors = listOf(Color(0x146C63FF), Color(0x0D4ECDC4))
)

enum class Difficulty { Easy, Normal, Hard }

data class ExerciseFeedbackState(
    val isEnabled: Boolean = true,
    val actualSets: String = "3",
    val actualReps: String = "12",
    val difficulty: Difficulty = Difficulty.Normal,
    val isSkipped: Boolean = false,
    val isCancelled: Boolean = false
)


private val defaultExercises = listOf(
    Exercise("🫸", "Push-ups", "", 3, 12, null, Color(0x336C63FF), 3, 12),
    Exercise("🤸", "Squats", "", 3, 12, null, Color(0x33F87171), 3, 12),
    Exercise("💪", "Barbell Row", "", 3, 8, null, Color(0x33F5A623), 3, 8)
)

@Composable
fun ExerciseFeedbackScreen(onBackClick: () -> Unit = {}) {
    var workoutRating by remember { mutableStateOf<Boolean?>(null) }
    val states = remember {
        mutableStateListOf(
            ExerciseFeedbackState(isEnabled = true, actualSets = "3", actualReps = "10", difficulty = Difficulty.Normal),
            ExerciseFeedbackState(isEnabled = false, actualSets = "3", actualReps = "12", difficulty = Difficulty.Normal),
            ExerciseFeedbackState(isEnabled = true, actualSets = "3", actualReps = "8", difficulty = Difficulty.Hard)
        )
    }

    Scaffold(
        containerColor = White,
        bottomBar = { FeedbackBottomNavBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            FeedbackTopBar(onBackClick = onBackClick)

            Text(
                text = "Отметьте как прошло выполнение каждого упражнения — это поможет ИИ улучшить следующие планы.",
                color = DarkMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            defaultExercises.forEachIndexed { index, exercise ->
                ExerciseFeedbackCard(
                    exercise = exercise,
                    state = states[index],
                    onStateChange = { states[index] = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            WorkoutRatingSection(
                rating = workoutRating,
                onRatingChange = { workoutRating = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeedbackTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹ План",
            color = Purple,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.clickable { onBackClick() }
        )
        Text(
            text = "Обратная связь",
            color = Dark,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        // Spacer to balance the title center alignment
        Box(modifier = Modifier.width(60.dp))
    }
}

@Composable
private fun ExerciseFeedbackCard(
    exercise: Exercise,
    state: ExerciseFeedbackState,
    onStateChange: (ExerciseFeedbackState) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(brush = cardGradient)
            .border(width = 1.dp, color = PurpleBorder, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Header row: icon + name + toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(exercise.iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = exercise.emoji, fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = exercise.name,
                            color = Dark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Рек: ${exercise.recommendedSets}×${exercise.recommendedReps}",
                            color = DarkMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                FeedbackToggle(
                    checked = state.isEnabled,
                    onCheckedChange = { onStateChange(state.copy(isEnabled = it)) }
                )
            }

            if (state.isEnabled) {
                // Actual sets and reps inputs
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NumberInputField(
                        label = "ФАКТ. ПОДХОДЫ",
                        value = state.actualSets,
                        onValueChange = { onStateChange(state.copy(actualSets = it)) },
                        modifier = Modifier.weight(1f)
                    )
                    NumberInputField(
                        label = "ФАКТ. ПОВТОРЕНИЯ",
                        value = state.actualReps,
                        onValueChange = { onStateChange(state.copy(actualReps = it)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Difficulty selector
                Text(
                    text = "ОЩУЩАЕМАЯ СЛОЖНОСТЬ",
                    color = DarkMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )
                DifficultySelector(
                    selected = state.difficulty,
                    onSelect = { onStateChange(state.copy(difficulty = it)) }
                )
            } else {
                // Skip / Cancel buttons
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SkipCancelButton(
                        label = "Пропущено",
                        prefix = "⏸",
                        isActive = state.isSkipped,
                        onClick = {
                            onStateChange(state.copy(isSkipped = !state.isSkipped, isCancelled = false))
                        }
                    )
                    SkipCancelButton(
                        label = "Отменено",
                        prefix = "✗",
                        isActive = state.isCancelled,
                        onClick = {
                            onStateChange(state.copy(isCancelled = !state.isCancelled, isSkipped = false))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackToggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .width(51.dp)
            .height(31.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (checked) Green else Color(0xFFD1D5DB))
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(27.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(White)
        )
    }
}

@Composable
private fun NumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = DarkMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.5.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(InputBg)
                .border(width = 1.dp, color = PurpleBorder, shape = RoundedCornerShape(14.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
                textStyle = TextStyle(
                    color = Dark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DifficultySelector(selected: Difficulty, onSelect: (Difficulty) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf(
            Difficulty.Easy to "Легко",
            Difficulty.Normal to "Нормально",
            Difficulty.Hard to "Тяжело"
        ).forEach { (difficulty, label) ->
            val isSelected = selected == difficulty
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) PurpleDim else White)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Purple else Color(0x1A000000),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(difficulty) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Purple else DarkMuted,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun SkipCancelButton(
    label: String,
    prefix: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isActive) Color(0x30F87171) else RedLight)
            .border(width = 1.dp, color = RedBorder, shape = RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = "$prefix $label",
            color = Red,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun WorkoutRatingSection(rating: Boolean?, onRatingChange: (Boolean) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "ОЦЕНКА ТРЕНИРОВКИ",
            color = DarkMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.5.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RatingButton(
                emoji = "👍",
                label = "Liked",
                isSelected = rating == true,
                selectedBg = Color(0x334ADE80),
                selectedLabelColor = Color(0xFF22C55E),
                modifier = Modifier.weight(1f),
                onClick = { onRatingChange(true) }
            )
            RatingButton(
                emoji = "👎",
                label = "Disliked",
                isSelected = rating == false,
                selectedBg = Color(0x33F87171),
                selectedLabelColor = Red,
                modifier = Modifier.weight(1f),
                onClick = { onRatingChange(false) }
            )
        }
    }
}

@Composable
private fun RatingButton(
    emoji: String,
    label: String,
    isSelected: Boolean,
    selectedBg: Color,
    selectedLabelColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) selectedBg else InputBg)
            .border(
                width = 1.dp,
                color = if (isSelected) selectedLabelColor.copy(alpha = 0.3f) else Color(0x1A000000),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Text(
                text = label,
                color = if (isSelected) selectedLabelColor else DarkMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FeedbackBottomNavBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBg)
            .border(width = 1.dp, color = Color(0x1A000000), shape = RoundedCornerShape(0.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(icon = Icons.Default.Home, label = "Главная", isSelected = false)
            NavBarItem(icon = Icons.Default.Star, label = "План", isSelected = true)
            NavBarItem(icon = Icons.Default.Favorite, label = "Прогресс", isSelected = false)
            NavBarItem(icon = Icons.Default.Person, label = "Профиль", isSelected = false)
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Purple else DarkMuted,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Purple else DarkMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExerciseFeedbackScreenPreview() {
    MaterialTheme {
        ExerciseFeedbackScreen()
    }
}
