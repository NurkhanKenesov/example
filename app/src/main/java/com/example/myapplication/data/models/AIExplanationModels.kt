package com.example.myapplication.data.models

import androidx.compose.ui.graphics.Color

data class FactorRow(
    val label: String,
    val value: String,
    val valueColor: Color,
    val barColor: Color,
    val barFraction: Float,
)
