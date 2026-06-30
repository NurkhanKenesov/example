package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorDark = Color(0xFF0F0F23)
private val ColorMuted = Color(0x660F0F23)
private val ColorSurface = Color(0xFFF5F7FF)

@Composable
fun LeaderboardScreen(onBackClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0F2FF), ColorSurface)))
    ) {
        TopNavBar(
            title = "🏆 Рейтинг",
            onBackClick = onBackClick
        )
        EmptyLeaderboardState()
    }
}

@Composable
private fun TopNavBar(title: String, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBackClick, contentPadding = PaddingValues(0.dp)) {
            Text(
                text = "‹ Назад",
                color = ColorPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = title,
            color = ColorDark,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun EmptyLeaderboardState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, start = 40.dp, end = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "--",
                color = ColorDark.copy(alpha = 0.2f),
                fontSize = 48.sp,
                fontWeight = FontWeight.W700
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Таблица лидеров пуста",
                color = ColorMuted,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}