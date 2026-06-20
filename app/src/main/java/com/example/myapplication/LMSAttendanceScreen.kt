package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorDark = Color(0xFF0F0F23)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorCyan = Color(0xFF22D3EE)
private val ColorRed = Color(0xFFF87171)
private val ColorYellow = Color(0xFFFBBF24)
private val ColorPurple = Color(0xFFA78BFA)
private val ColorBackground = Color(0xFFF5F7FF)
private val ColorSurface = Color(0xFFF0F2FF)
private val ColorCardBg = Color(0xFFFFFFFF)
private val ColorSubtext = Color(0x80_0F0F23)

private val GradientQrButton = Brush.horizontalGradient(listOf(ColorGreen, ColorCyan))

@Composable
fun LMSAttendanceScreen(onBackClick: () -> Unit = {}) {
    Scaffold(
        containerColor = ColorBackground,
        bottomBar = { AttendanceBottomNav() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(ColorSurface, ColorBackground))
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AttendanceTopBar(onBack = onBackClick)
                Spacer(modifier = Modifier.height(8.dp))
                QrCheckInButton()
                Spacer(modifier = Modifier.height(24.dp))
                AttendanceJournalSection()
                Spacer(modifier = Modifier.height(20.dp))
                NormativesSection()
                Spacer(modifier = Modifier.height(20.dp))
                TheoryTestsSection()
            }
        }
    }
}

@Composable
private fun AttendanceTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "‹ Назад",
                color = ColorPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Text(
            text = "Обучение и зачет",
            color = ColorDark,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun QrCheckInButton() {
    Box(
        modifier = Modifier
            .width(353.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(GradientQrButton)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "📷", fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Отметиться на паре (QR)",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = ColorDark.copy(alpha = 0.4f),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 0.dp)
    )
}

@Composable
private fun AttendanceJournalSection() {
    SectionHeader("ЖУРНАЛ ПОСЕЩАЕМОСТИ")
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Всего посещений",
                    color = ColorDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "12 / 16",
                    color = ColorDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 12f / 16f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ColorGreen,
                trackColor = ColorGreen.copy(alpha = 0.15f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            AttendanceRow(date = "10 Октября, Вт", status = "Присутствовал", statusColor = ColorGreen)
            Spacer(modifier = Modifier.height(8.dp))
            AttendanceRow(date = "08 Октября, Вс", status = "Тренировка ИИ", statusColor = ColorPurple)
            Spacer(modifier = Modifier.height(8.dp))
            AttendanceRow(date = "03 Октября, Вт", status = "Пропуск", statusColor = ColorRed)
        }
    }
}

@Composable
private fun AttendanceRow(date: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = date, color = ColorDark, fontSize = 14.sp, fontWeight = FontWeight.Normal)
        Text(text = status, color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun NormativesSection() {
    SectionHeader("НОРМАТИВЫ НА СЕМЕСТР")
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            NormativeRow(
                title = "Бег 3 км",
                deadline = "Дедлайн: 15 Ноября",
                badgeText = "Ожидает",
                badgeTextColor = ColorYellow,
                badgeBgColor = ColorYellow.copy(alpha = 0.15f)
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = ColorDark.copy(alpha = 0.07f)
            )
            NormativeRow(
                title = "Подтягивания",
                deadline = "Дедлайн: 1 Декабря",
                badgeText = "Сдано (15)",
                badgeTextColor = ColorGreen,
                badgeBgColor = ColorGreen.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun NormativeRow(
    title: String,
    deadline: String,
    badgeText: String,
    badgeTextColor: Color,
    badgeBgColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, color = ColorDark, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = deadline, color = ColorDark.copy(alpha = 0.5f), fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(badgeBgColor)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(text = badgeText, color = badgeTextColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TheoryTestsSection() {
    SectionHeader("ТЕОРЕТИЧЕСКИЕ ТЕСТЫ")
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Основы ЗОЖ",
                    color = ColorDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "+5 баллов к зачету",
                    color = ColorDark.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Пройти",
                    color = ColorPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AttendanceBottomNav() {
    NavigationBar(
        containerColor = ColorCardBg,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple("Главная", Icons.Default.Home, false),
            Triple("Планы", Icons.Default.DateRange, false),
            Triple("Обучение", Icons.Default.MenuBook, true),
            Triple("Рейтинг", Icons.Default.EmojiEvents, false),
            Triple("Профиль", Icons.Default.Person, false),
        )
        items.forEach { (label, icon, selected) ->
            NavigationBarItem(
                selected = selected,
                onClick = {},
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) ColorPrimary else ColorDark.copy(alpha = 0.4f)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = if (selected) ColorPrimary else ColorDark.copy(alpha = 0.4f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LMSAttendanceScreenPreview() {
    LMSAttendanceScreen()
}
