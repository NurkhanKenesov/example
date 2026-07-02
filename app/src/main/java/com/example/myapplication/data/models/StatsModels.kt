package com.example.myapplication.data.models

import androidx.compose.ui.graphics.Color

data class StatCard(
    val value: String,
    val label: String,
    val valueColor: Color,
    val bgColor: Color
)

data class ExerciseStat(
    val emoji: String,
    val emojiBg: Color,
    val name: String,
    val attempts: String,
    val percentage: String,
    val difficulty: String,
    val percentageColor: Color
)
