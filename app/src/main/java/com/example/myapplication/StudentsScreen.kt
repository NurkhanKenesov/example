package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Design tokens ─────────────────────────────────────────────────────────────

private val ColorDark = Color(0xFF0F0F23)
private val ColorDark50 = Color(0x800F0F23)
private val ColorDark40 = Color(0x660F0F23)
private val ColorDark30 = Color(0x4D0F0F23)
private val ColorPurple = Color(0xFF6C63FF)
private val ColorViolet = Color(0xFF8B5CF6)
private val ColorHeliotrope = Color(0xFFA78BFA)
private val ColorSurface = Color(0xFFF5F7FF)
private val ColorCardBorder = Color(0x120F0F23)
private val ColorPurpleBg = Color(0x336C63FF)
private val ColorPurpleBorder = Color(0x666C63FF)
private val ColorAttendanceBg1 = Color(0x146C63FF)
private val ColorAttendanceBg2 = Color(0x0D4ECDC4)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorGreenBg = Color(0x264ADE80)
private val ColorYellow = Color(0xFFFBBF24)
private val ColorYellowBg = Color(0x26FBBF24)
private val ColorRed = Color(0xFFF87171)
private val ColorRedBg = Color(0x26F87171)

private val PurpleGradient = Brush.linearGradient(
    colors = listOf(ColorPurple, ColorViolet),
    start = Offset(0f, 0f), end = Offset(Float.MAX_VALUE, Float.MAX_VALUE)
)
private val AttendanceGradient = Brush.linearGradient(
    colors = listOf(ColorAttendanceBg1, ColorAttendanceBg2),
    start = Offset(0f, 0f), end = Offset(Float.MAX_VALUE, 0f)
)

// ── Data models ───────────────────────────────────────────────────────────────

enum class HealthGroup { BASIC, PREPARED, SPECIAL }

data class Student(
    val id: Int,
    val initials: String,
    val name: String,
    val gender: String,
    val age: String,
    val group: HealthGroup,
    val score: String,
    val hasAlert: Boolean = false,
    val avatarGradient: Brush
)

private val sampleStudents = listOf(
    Student(
        id = 1, initials = "AS", name = "Amir Seitkali",
        gender = "Male", age = "22 года", group = HealthGroup.BASIC, score = "3.2/4",
        avatarGradient = Brush.linearGradient(
            colors = listOf(Color(0xFF6C63FF), Color(0xFF8B5CF6)),
            start = Offset(0f, Float.MAX_VALUE), end = Offset(Float.MAX_VALUE, 0f)
        )
    ),
    Student(
        id = 2, initials = "ZA", name = "Zarina Akhmetova",
        gender = "Female", age = "20 лет", group = HealthGroup.PREPARED, score = "2.5/4",
        hasAlert = true,
        avatarGradient = Brush.linearGradient(
            colors = listOf(Color(0xFFF97316), Color(0xFFFBBF24)),
            start = Offset(0f, Float.MAX_VALUE), end = Offset(Float.MAX_VALUE, 0f)
        )
    ),
    Student(
        id = 3, initials = "RN", name = "Ruslan Nurlanov",
        gender = "Male", age = "21 год", group = HealthGroup.BASIC, score = "3.8/4",
        avatarGradient = Brush.linearGradient(
            colors = listOf(Color(0xFF22D3EE), Color(0xFF4ADE80)),
            start = Offset(0f, Float.MAX_VALUE), end = Offset(Float.MAX_VALUE, 0f)
        )
    ),
    Student(
        id = 4, initials = "DI", name = "Dias Issayev",
        gender = "Male", age = "19 лет", group = HealthGroup.SPECIAL, score = "1.8/4",
        avatarGradient = Brush.linearGradient(
            colors = listOf(Color(0xFFF87171), Color(0xFFFB923C)),
            start = Offset(0f, Float.MAX_VALUE), end = Offset(Float.MAX_VALUE, 0f)
        )
    )
)

private val filterChips = listOf("Все", "basic", "prepared", "special", "С травмами")

data class TeacherNavItem(val label: String, val icon: ImageVector)

private val teacherNavItems = listOf(
    TeacherNavItem("Главная", Icons.Outlined.Home),
    TeacherNavItem("Студенты", Icons.Outlined.School),
    TeacherNavItem("Расписание", Icons.Outlined.CalendarMonth),
    TeacherNavItem("Профиль", Icons.Outlined.Person)
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun StudentsScreen(
    onBackClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Все") }
    var selectedNavIndex by remember { mutableIntStateOf(1) }

    Scaffold(
        containerColor = ColorSurface,
        bottomBar = { StudentsBottomNav(selectedNavIndex) { selectedNavIndex = it } }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            ScreenTitle()
            Spacer(Modifier.height(12.dp))
            AttendanceCard(modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(18.dp))
            SearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(14.dp))
            FilterChipRow(selected = selectedFilter, onSelect = { selectedFilter = it })
            Spacer(Modifier.height(10.dp))
            StudentList(
                students = sampleStudents,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(16.dp))
            FooterLabel(
                shown = sampleStudents.size,
                total = 500,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun ScreenTitle() {
    Text(
        text = "👨\u200D🏫 Студенты",
        fontSize = 34.sp,
        fontWeight = FontWeight.W800,
        color = ColorDark,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun AttendanceCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(AttendanceGradient)
            .border(1.dp, Color(0x336C63FF), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Посещаемость: 85%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ColorDark
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Факультет ИТ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = ColorDark50
            )
        }
        ExcelReportButton()
    }
}

@Composable
private fun ExcelReportButton() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PurpleGradient)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "📥 Отчет Excel",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorSurface)
            .border(1.dp, Color(0x336C63FF), RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🔍", fontSize = 16.sp, color = ColorDark30)
        Spacer(Modifier.width(10.dp))
        if (query.isEmpty()) {
            Text(
                text = "Поиск по имени или ID...",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = ColorDark30
            )
        } else {
            Text(text = query, fontSize = 15.sp, color = ColorDark)
        }
    }
}

@Composable
private fun FilterChipRow(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        filterChips.forEach { label ->
            FilterChipItem(
                label = label,
                isSelected = label == selected,
                onClick = { onSelect(label) }
            )
        }
    }
}

@Composable
private fun FilterChipItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) ColorPurpleBg else Color.White
    val borderColor = if (isSelected) ColorPurpleBorder else ColorCardBorder
    val textColor = if (isSelected) ColorHeliotrope else ColorDark50

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

@Composable
private fun StudentList(students: List<Student>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        students.forEach { student -> StudentCard(student) }
    }
}

@Composable
private fun StudentCard(student: Student) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, ColorCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StudentAvatar(initials = student.initials, gradient = student.avatarGradient)
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = student.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorDark
                )
                if (student.hasAlert) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = "Alert",
                        tint = ColorYellow,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "ID: ${student.id} • ${student.gender} • ${student.age}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = ColorDark40
            )
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            HealthGroupBadge(group = student.group)
            Text(text = student.score, fontSize = 11.sp, color = ColorDark30)
        }
    }
}

@Composable
private fun StudentAvatar(initials: String, gradient: Brush) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = ColorDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HealthGroupBadge(group: HealthGroup) {
    val (label, textColor, bgColor) = when (group) {
        HealthGroup.BASIC -> Triple("basic", ColorGreen, ColorGreenBg)
        HealthGroup.PREPARED -> Triple("prepared", ColorYellow, ColorYellowBg)
        HealthGroup.SPECIAL -> Triple("special", ColorRed, ColorRedBg)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

@Composable
private fun FooterLabel(shown: Int, total: Int, modifier: Modifier = Modifier) {
    Text(
        text = "Показано $shown из $total студентов",
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        color = ColorDark30,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun StudentsBottomNav(selectedIndex: Int, onSelect: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.border(
            width = 1.dp,
            color = ColorCardBorder,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        )
    ) {
        teacherNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (index == selectedIndex) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ColorPurple,
                    selectedTextColor = ColorPurple,
                    unselectedIconColor = ColorDark40,
                    unselectedTextColor = ColorDark40,
                    indicatorColor = ColorPurpleBg
                )
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentsScreenPreview() {
    MaterialTheme {
        StudentsScreen()
    }
}
