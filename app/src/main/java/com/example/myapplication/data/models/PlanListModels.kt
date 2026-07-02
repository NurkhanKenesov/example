package com.example.myapplication.data.models

import androidx.compose.ui.graphics.Color

enum class PlanStatus(
    val emoji: String,
    val label: String,
    val badgeColor: Color,
    val badgeBg: Color,
) {
    COMPLETED("✅", "COMPLETED", Color(0xFF4ADE80), Color(0x264ADE80)),
    SCHEDULED("🕐", "SCHEDULED", Color(0xFFFB923C), Color(0x26FB923C)),
    DISCARDED("❌", "DISCARDED", Color(0xFFF87171), Color(0x26F87171)),
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
