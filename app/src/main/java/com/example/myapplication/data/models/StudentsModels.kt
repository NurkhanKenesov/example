package com.example.myapplication.data.models

import androidx.compose.ui.graphics.Brush

enum class HealthGroup { BASIC, PREPARED, SPECIAL }

data class UiStudent(
    val id: String,
    val initials: String,
    val name: String,
    val gender: String,
    val age: String,
    val group: HealthGroup,
    val score: String,
    val hasAlert: Boolean,
    val avatarGradient: Brush
)
