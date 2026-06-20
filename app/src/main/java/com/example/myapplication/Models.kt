package com.example.myapplication

import androidx.compose.ui.graphics.Color

data class Exercise(
    val emoji: String,
    val name: String,
    val description: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val score: Double? = null,
    val iconBg: Color = Color(0x336C63FF),
    val recommendedSets: Int = 0,
    val recommendedReps: Int = 0
)

data class ExerciseSection(
    val emoji: String,
    val title: String,
    val exercises: List<Exercise>
)
