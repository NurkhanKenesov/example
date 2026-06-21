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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AchievPrimary = Color(0xFF6C63FF)
private val AchievDark = Color(0xFF0F0F23)
private val AchievTextMuted = Color(0xFF0F0F23).copy(alpha = 0.50f)
private val AchievTextFaint = Color(0xFF0F0F23).copy(alpha = 0.35f)
private val AchievTabActiveBg = Color(0xFF6C63FF).copy(alpha = 0.10f)
private val AchievCardBorder = Color(0xFF000000).copy(alpha = 0.07f)
private val AchievCardShadow = Color(0xFF000000).copy(alpha = 0.06f)
private val AchievNavBorder = Color(0xFF000000).copy(alpha = 0.08f)

private val AchievGradientBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))
)

private data class Achievement(
    val emoji: String,
    val name: String,
    val description: String,
    val unlocked: Boolean = true
)

private val achievements = listOf(
    Achievement("🌅", "Ранняя пташка", "Тренировка до 8:00 утра"),
    Achievement("🏃", "Марафонец", "10 км за одну неделю"),
    Achievement("🔥", "Железная воля", "Тренировки 5 дней подряд", unlocked = false),
    Achievement("💪", "Атлет 1 уровня", "Сдать все нормативы на 5", unlocked = false)
)

@Composable
fun AchievementsScreen(onBackClick: () -> Unit = {}) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AchievGradientBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Nav bar
            AchievNavBar(onBackClick = onBackClick)

            // Tab switcher
            AchievTabSwitcher(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 16.dp)
            ) {
                AchievementsGrid(achievements = achievements)
            }
        }
    }
}

@Composable
private fun AchievNavBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹ Назад",
            color = AchievPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.clickable { onBackClick() }
        )
        Text(
            text = "Достижения",
            color = AchievDark,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        // Spacer to balance layout
        Spacer(modifier = Modifier.width(60.dp))
    }
}

@Composable
private fun AchievTabSwitcher(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Ачивки", "Рейтинг (Топ-10)")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), ambientColor = Color(0xFF000000).copy(alpha = 0.04f))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) AchievTabActiveBg else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) AchievPrimary else AchievTextFaint.copy(alpha = 0.40f),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AchievementsGrid(achievements: List<Achievement>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        achievements.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd item
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .then(if (!achievement.unlocked) Modifier.alpha(0.6f) else Modifier)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = AchievCardShadow)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, AchievCardBorder, RoundedCornerShape(16.dp))
            .padding(start = 12.dp, end = 12.dp, top = 20.dp, bottom = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Emoji
        Text(
            text = achievement.emoji,
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Name
        Text(
            text = achievement.name,
            color = AchievDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        // Description
        Text(
            text = achievement.description,
            color = AchievTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun AchievementsScreenPreview() {
    AchievementsScreen()
}
