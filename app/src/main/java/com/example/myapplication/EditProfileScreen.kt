package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.myapplication.data.models.StudentTestNorm

// ── Colours (local to this screen, matching StudentsScreen/ExerciseFeedbackScreen) ──

private val ScreenBg = Color(0xFFF5F7FF)
private val Dark = Color(0xFF0F0F23)
private val Dark50 = Color(0x800F0F23)
private val Dark40 = Color(0x660F0F23)
private val Dark30 = Color(0x4D0F0F23)
private val Purple = Color(0xFF6C63FF)
private val Violet = Color(0xFF8B5CF6)
private val PurpleBorder = Color(0x336C63FF)
private val CardBorder = Color(0x120F0F23)
private val InputBg = Color(0xFFF5F7FF)
private val White = Color(0xFFFFFFFF)
private val Green = Color(0xFF4ADE80)
private val GreenBg = Color(0x264ADE80)
private val Yellow = Color(0xFFFBBF24)
private val YellowBg = Color(0x26FBBF24)
private val Red = Color(0xFFF87171)
private val RedBg = Color(0x26F87171)

private val avatarGradient = Brush.linearGradient(listOf(Purple, Violet))

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun EditProfileScreen(
    studentId: String,
    onBackClick: () -> Unit = {},
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()

    LaunchedEffect(studentId) { viewModel.load(studentId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        EditProfileTopBar(
            isSaving = isSaving,
            onBackClick = onBackClick,
            onSaveClick = { viewModel.save(onSuccess = onBackClick) }
        )

        when (val state = uiState) {
            is EditProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Purple)
                }
            }
            is EditProfileUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Red, fontSize = 14.sp)
                }
            }
            is EditProfileUiState.Loaded -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        StudentIdentitySection(student = state.student)
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        SectionLabel(text = "МЕДИЦИНСКАЯ ГРУППА")
                        Spacer(Modifier.height(8.dp))
                        MedicalGroupSelector(
                            selected = state.student.medicalGroup,
                            onSelect = viewModel::updateMedicalGroup,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(20.dp))
                    }

                    item {
                        SectionLabel(text = "БИОМЕТРИЯ")
                        Spacer(Modifier.height(8.dp))
                        BiometricsCard(
                            heightCm = state.student.heightCm,
                            weightKg = state.student.weightKg,
                            onHeightChange = { viewModel.updateHeight(it) },
                            onWeightChange = { viewModel.updateWeight(it) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(20.dp))
                    }

                    item {
                        SectionLabel(text = "ФИЗИЧЕСКИЕ ТЕСТЫ")
                        Spacer(Modifier.height(8.dp))
                        PhysicalTestsCard(
                            testValues = state.testValues,
                            onValueChange = viewModel::updateTest,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(20.dp))
                    }

                    item {
                        SectionLabel(text = "ТРАВМЫ")
                        Spacer(Modifier.height(8.dp))
                        InjuriesCard(
                            injuries = state.injuries,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun EditProfileTopBar(
    isSaving: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBackClick() }
        ) {
            Text(text = "‹", fontSize = 24.sp, fontWeight = FontWeight.Normal, color = Purple)
            Spacer(Modifier.width(2.dp))
            Text(text = "Студенты", fontSize = 15.sp, fontWeight = FontWeight.Normal, color = Purple)
        }

        Text(
            text = "Редактирование",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Dark
        )

        Text(
            text = "💾",
            fontSize = 18.sp,
            color = if (isSaving) Dark30 else Purple,
            modifier = Modifier.clickable(enabled = !isSaving) { onSaveClick() }
        )
    }
}

// ── Identity ──────────────────────────────────────────────────────────────────

@Composable
private fun StudentIdentitySection(student: Student) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(avatarGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = student.initials,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = White
            )
        }
        Column {
            Text(text = student.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Dark)
            Spacer(Modifier.height(2.dp))
            Text(text = "Student ID: ${student.id}", fontSize = 13.sp, fontWeight = FontWeight.Normal, color = Dark50)
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 20.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Dark30,
        letterSpacing = 0.5.sp
    )
}

// ── Medical group selector (reuses HealthGroup colour semantics) ─────────────

@Composable
private fun MedicalGroupSelector(
    selected: MedicalGroup,
    onSelect: (MedicalGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MedicalGroup.entries.forEach { group ->
            MedicalGroupChip(
                group = group,
                isSelected = group == selected,
                onClick = { onSelect(group) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private data class GroupChipStyle(val label: String, val subtitle: String, val color: Color, val bg: Color)

private fun groupChipStyle(group: MedicalGroup): GroupChipStyle = when (group) {
    MedicalGroup.BASIC -> GroupChipStyle("basic", "Без огранич.", Green, GreenBg)
    MedicalGroup.PREPARATORY -> GroupChipStyle("prepared", "Средняя инт.", Yellow, YellowBg)
    MedicalGroup.SPECIAL -> GroupChipStyle("special", "Низкая инт.", Red, RedBg)
}

@Composable
private fun MedicalGroupChip(
    group: MedicalGroup,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val style = groupChipStyle(group)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) style.bg else White)
            .border(
                width = 1.dp,
                color = if (isSelected) style.color else CardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = style.label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) style.color else Dark
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = style.subtitle,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = if (isSelected) style.color else Dark30,
            textAlign = TextAlign.Center
        )
    }
}

// ── Card shell ────────────────────────────────────────────────────────────────

@Composable
private fun CardShell(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        content = content
    )
}

// ── Biometrics ────────────────────────────────────────────────────────────────

@Composable
private fun BiometricsCard(
    heightCm: Float,
    weightKg: Float,
    onHeightChange: (Float) -> Unit,
    onWeightChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    CardShell(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EditNumberField(
                label = "Рост (см)",
                value = formatNumber(heightCm),
                onValueChange = { onHeightChange(it.toFloatOrNull() ?: heightCm) },
                modifier = Modifier.weight(1f)
            )
            EditNumberField(
                label = "Вес (кг)",
                value = formatNumber(weightKg),
                onValueChange = { onWeightChange(it.toFloatOrNull() ?: weightKg) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ── Physical tests ────────────────────────────────────────────────────────────

@Composable
private fun PhysicalTestsCard(
    testValues: Map<String, String>,
    onValueChange: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    CardShell(modifier = modifier) {
        PhysicalTestRow(
            leftLabel = "Cooper (м)", leftNormId = StudentTestNorm.COOPER,
            rightLabel = "Отжимания", rightNormId = StudentTestNorm.PUSHUPS,
            values = testValues, onValueChange = onValueChange
        )
        Spacer(Modifier.height(14.dp))
        PhysicalTestRow(
            leftLabel = "Подтягивания", leftNormId = StudentTestNorm.PULLUPS,
            rightLabel = "Гибкость (см)", rightNormId = StudentTestNorm.FLEXIBILITY,
            values = testValues, onValueChange = onValueChange
        )
        Spacer(Modifier.height(14.dp))
        PhysicalTestRow(
            leftLabel = "Пресс", leftNormId = StudentTestNorm.ABS,
            rightLabel = "Прыжок (см)", rightNormId = StudentTestNorm.JUMP,
            values = testValues, onValueChange = onValueChange
        )
    }
}

@Composable
private fun PhysicalTestRow(
    leftLabel: String,
    leftNormId: String,
    rightLabel: String,
    rightNormId: String,
    values: Map<String, String>,
    onValueChange: (String, String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EditNumberField(
            label = leftLabel,
            value = values[leftNormId] ?: "",
            onValueChange = { onValueChange(leftNormId, it) },
            modifier = Modifier.weight(1f)
        )
        EditNumberField(
            label = rightLabel,
            value = values[rightNormId] ?: "",
            onValueChange = { onValueChange(rightNormId, it) },
            modifier = Modifier.weight(1f)
        )
    }
}

// ── Editable rounded number field ─────────────────────────────────────────────

@Composable
private fun EditNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = Dark40,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(InputBg)
                .border(width = 1.dp, color = PurpleBorder, shape = RoundedCornerShape(14.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = value,
                onValueChange = { input ->
                    val filtered = input.filterIndexed { index, c ->
                        c.isDigit() || (c == '.' && input.indexOf('.') == index)
                    }
                    onValueChange(filtered)
                },
                textStyle = TextStyle(
                    color = Dark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun formatNumber(value: Float): String =
    if (value == value.toInt().toFloat()) value.toInt().toString() else value.toString()

// ── Injuries ──────────────────────────────────────────────────────────────────

@Composable
private fun InjuriesCard(injuries: List<Injury>, modifier: Modifier = Modifier) {
    CardShell(modifier = modifier) {
        if (injuries.isEmpty()) {
            Text(
                text = "Нет зарегистрированных травм",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Dark30
            )
        } else {
            injuries.forEachIndexed { index, injury ->
                InjuryRow(injury = injury)
                if (index != injuries.lastIndex) {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = CardBorder)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun InjuryRow(injury: Injury) {
    Row(verticalAlignment = Alignment.Top) {
        Text(text = "• ", fontSize = 13.sp, color = Red, fontWeight = FontWeight.Medium)
        Column {
            Text(
                text = "${injury.type} — ${injury.bodyPart}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Red
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = injury.severity,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Dark30
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditProfileScreenPreview() {
    MaterialTheme {
        EditProfileScreen(studentId = "1")
    }
}
