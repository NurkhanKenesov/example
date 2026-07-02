package com.example.myapplication.data.models

import androidx.compose.ui.graphics.Color

data class MuscleGroup(
    val name: String,
    val hoursRemaining: String,
    val recoveryPercent: Int,
    val totalHours: Int,
    val dotColor: Color,
    val barColor: Color,
)

sealed interface MuscleFatigueUiState {
    object Loading : MuscleFatigueUiState
    data class Loaded(val recovering: List<MuscleGroup>, val recovered: List<String>) : MuscleFatigueUiState
    data class Error(val message: String) : MuscleFatigueUiState
}
