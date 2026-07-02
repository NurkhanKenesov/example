package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.example.myapplication.data.models.HealthGroup
import com.example.myapplication.data.models.UiStudent

// ── Colours (local to this screen) ───────────────────────────────────────────

private val ScreenBg          = Color(0xFFF5F7FF)
private val Dark              = Color(0xFF0F0F23)
private val Dark50            = Color(0x800F0F23)
private val Dark40            = Color(0x660F0F23)
private val Dark30            = Color(0x4D0F0F23)
private val Purple            = Color(0xFF6C63FF)
private val Violet            = Color(0xFF8B5CF6)
private val Heliotrope        = Color(0xFFA78BFA)
private val CardBorder        = Color(0x120F0F23)
private val PurpleBg          = Color(0x336C63FF)
private val PurpleBorder      = Color(0x666C63FF)
private val AttendanceBg1     = Color(0x146C63FF)
private val AttendanceBg2     = Color(0x0D4ECDC4)
private val Green             = Color(0xFF4ADE80)
private val GreenBg           = Color(0x264ADE80)
private val Yellow            = Color(0xFFFBBF24)
private val YellowBg          = Color(0x26FBBF24)
private val Red               = Color(0xFFF87171)
private val RedBg             = Color(0x26F87171)

private val PurpleGradient = Brush.linearGradient(
    colors = listOf(Purple, Violet),
    start = Offset.Zero, end = Offset.Infinite
)
private val AttendanceCardGradient = Brush.linearGradient(
    colors = listOf(AttendanceBg1, AttendanceBg2),
    start = Offset.Zero, end = Offset.Infinite
)

private val avatarGradients = listOf(
    Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFF8B5CF6))),
    Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFFBBF24))),
    Brush.linearGradient(listOf(Color(0xFF4ADE80), Color(0xFF22D3EE))),
    Brush.linearGradient(listOf(Color(0xFFF87171), Color(0xFFFBBF24))),
    Brush.linearGradient(listOf(Color(0xFF22D3EE), Color(0xFF6C63FF))),
)

// ── Data ─────────────────────────────────────────────────────────────────────

// ── Data ─────────────────────────────────────────────────────────────────────

private fun Student.toUiStudent(index: Int) = UiStudent(
     id = id,
     initials = initials.ifEmpty {
         name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.joinToString("")
     },
     name = name,
     gender = gender.ifEmpty { "--" },
     age = age.ifEmpty { "--" },
     group = when (medicalGroup) {
         MedicalGroup.BASIC -> HealthGroup.BASIC
         MedicalGroup.PREPARATORY -> HealthGroup.PREPARED
         MedicalGroup.SPECIAL -> HealthGroup.SPECIAL
     },
     score = score,
     hasAlert = hasAlert,
     avatarGradient = avatarGradients[index % avatarGradients.size]
 )

private val filterLabels = listOf("Все", "basic", "prepared", "special", "С травмами")

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun StudentsScreen(onBackClick: () -> Unit = {}, onStudentClick: (String) -> Unit = {}) {
    val viewModel: StudentsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery    by remember { mutableStateOf("") }
    var activeFilter   by remember { mutableStateOf("Все") }

    val loadedState = uiState as? StudentsUiState.Loaded

    val allStudents = loadedState?.students?.mapIndexed { i, s -> s.toUiStudent(i) } ?: emptyList()

    val visibleStudents = allStudents
        .filter { s ->
            val matchSearch = searchQuery.isBlank() ||
                    s.name.contains(searchQuery, ignoreCase = true) ||
                    s.id.contains(searchQuery, ignoreCase = true)
            val matchFilter = when (activeFilter) {
                "basic"     -> s.group == HealthGroup.BASIC
                "prepared"  -> s.group == HealthGroup.PREPARED
                "special"   -> s.group == HealthGroup.SPECIAL
                "С травмами" -> s.hasAlert
                else        -> true
            }
            matchSearch && matchFilter
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                StudentListHeader(
                    title       = "👨\u200D🏫 Студенты",
                    subtitle    = loadedState?.groupLabel ?: "",
                    attendance  = loadedState?.attendancePercent ?: 0,
                    onBackClick = onBackClick
                )
            }

            item {
                when (uiState) {
                    is StudentsUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Purple) }
                    }
                    is StudentsUiState.Error -> {
                        val msg = (uiState as StudentsUiState.Error).message
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = msg,
                                color = Red,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                    else -> {} // content rendered below
                }
            }

            if (loadedState != null) {
                item {
                    Spacer(Modifier.height(14.dp))
                    StudentSearchField(
                        query    = searchQuery,
                        onChange = { searchQuery = it },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    StudentFilterRow(
                        active   = activeFilter,
                        onSelect = { activeFilter = it }
                    )
                    Spacer(Modifier.height(4.dp))
                }

                if (visibleStudents.isEmpty()) {
                    item { EmptyResultState() }
                } else {
                    items(visibleStudents, key = { it.id }) { student ->
                        StudentCard(
                            student  = student,
                            onClick  = { onStudentClick(student.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        )
                    }
                }

                item {
                    StudentCountFooter(
                        shown = visibleStudents.size,
                        total = loadedState.total,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun StudentListHeader(
    title: String,
    subtitle: String,
    attendance: Int,
    onBackClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                color = Purple,
                modifier = Modifier.clickable { onBackClick() }
            )
        }

        Text(
            text = title,
            fontSize = 34.sp,
            fontWeight = FontWeight.W800,
            color = Dark,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        if (subtitle.isNotEmpty()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Dark50,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        AttendanceSummaryCard(
            attendancePercent = attendance,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

// ── Attendance summary card ───────────────────────────────────────────────────

@Composable
private fun AttendanceSummaryCard(attendancePercent: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(AttendanceCardGradient)
            .border(1.dp, Color(0x336C63FF), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (attendancePercent > 0) "$attendancePercent% посещаемость" else "Посещаемость",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (attendancePercent > 0) "За текущий семестр" else "Загрузка данных...",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Dark50
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

// ── Search & filter ───────────────────────────────────────────────────────────

@Composable
private fun StudentSearchField(
    query: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = query,
        onValueChange = onChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Dark
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ScreenBg)
            .border(1.dp, Color(0x336C63FF), RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔍", fontSize = 16.sp, color = Dark30)
                Spacer(Modifier.width(10.dp))
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = "Поиск по имени или ID...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = Dark30
                        )
                    }
                    inner()
                }
            }
        }
    )
}

@Composable
private fun StudentFilterRow(active: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 8.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        filterLabels.forEach { label ->
            FilterChip(
                label      = label,
                isSelected = label == active,
                onClick    = { onSelect(label) }
            )
        }
    }
}

@Composable
private fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PurpleBg else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) PurpleBorder else CardBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Heliotrope else Dark50
        )
    }
}

// ── Student card ──────────────────────────────────────────────────────────────

@Composable
private fun StudentCard(student: UiStudent, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StudentAvatar(initials = student.initials, gradient = student.avatarGradient)

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = student.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Dark
                )
                if (student.hasAlert) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = "Предупреждение",
                        tint = Yellow,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "ID: ${student.id} • ${student.gender} • ${student.age}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Dark40
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            HealthGroupBadge(group = student.group)
            Text(text = student.score, fontSize = 11.sp, color = Dark30)
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
            color = Dark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HealthGroupBadge(group: HealthGroup) {
    val (label, textColor, bgColor) = when (group) {
        HealthGroup.BASIC    -> Triple("basic",    Green,  GreenBg)
        HealthGroup.PREPARED -> Triple("prepared", Yellow, YellowBg)
        HealthGroup.SPECIAL  -> Triple("special",  Red,    RedBg)
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

// ── Empty / footer states ─────────────────────────────────────────────────────

@Composable
private fun EmptyResultState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🔍", fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Студенты не найдены",
                color = Dark30,
                fontSize = 15.sp,
                fontWeight = FontWeight.W600
            )
        }
    }
}

@Composable
private fun StudentCountFooter(shown: Int, total: Int, modifier: Modifier = Modifier) {
    Text(
        text = "Показано $shown из $total студентов",
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        color = Dark30,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StudentsScreenPreview() {
    MaterialTheme {
        StudentsScreen()
    }
}
