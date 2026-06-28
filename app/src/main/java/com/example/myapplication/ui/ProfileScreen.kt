package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.*

// Design tokens
private val ColorPrimary = Color(0xFF6C63FF)
private val ColorPrimaryLight = Color(0x266C63FF)   // 15% opacity
private val ColorViolet = Color(0xFF8B5CF6)
private val ColorTextDark = Color(0xFF0F0F23)
private val ColorTextMedium = Color(0x660F0F23)     // 40% opacity
private val ColorTextLight = Color(0x4D0F0F23)      // 30% opacity
private val ColorGreen = Color(0xFF4ADE80)
private val ColorGreenLight = Color(0x264ADE80)     // 15% opacity
private val ColorRed = Color(0xFFF87171)
private val ColorRedLight = Color(0x26F87171)       // 15% opacity
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorBackground = Color(0xFFF0F2FF)
private val ColorBackgroundEnd = Color(0xFFF5F7FF)
private val ColorDivider = Color(0x110F0F23)

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onNavigateToMuscleFatigue: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    viewModel: UserProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ColorBackground, ColorBackgroundEnd)
                )
            )
    ) {
        when (val current = state) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorPrimary)
                }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = current.message,
                        color = ColorRed,
                        fontSize = 16.sp
                    )
                }
            }
            is ProfileUiState.Loaded -> {
                val profile = current.profile
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
                        TopNavBar(
                            onBackClick = onBackClick,
                            onSettingsClick = onSettingsClick
                        )
                        AvatarSection(profile = profile)
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionLabel(text = "БИОМЕТРИЯ")
                        BiometricsCard(profile = profile)
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionLabel(text = "МЕДИЦИНСКАЯ КАРТА")
                        MedicalCard(
                            profile = profile,
                            onNavigateToMuscleFatigue = onNavigateToMuscleFatigue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionLabel(text = "ФИЗИЧЕСКИЕ ТЕСТЫ")
                        PhysicalTestsCard(onNavigateToStats = onNavigateToStats)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TopNavBar(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹",
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            color = ColorPrimary,
            modifier = Modifier.clickable { onBackClick() }
        )
        Text(
            text = "Мой профиль",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextDark
        )
        Text(
            text = "⚙️",
            fontSize = 15.sp,
            color = ColorPrimary,
            modifier = Modifier.clickable { onSettingsClick() }
        )
    }
}

@Composable
private fun AvatarSection(profile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(ColorPrimary, ColorViolet)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = profile.initials,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Name
        Text(
            text = profile.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextDark
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Email
        Text(
            text = profile.email,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = ColorTextMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tags row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileTag(
                text = if (profile.role == "teacher") "👩‍🏫 Преподаватель" else "🎓 Студент",
                textColor = ColorPrimary,
                backgroundColor = ColorPrimaryLight
            )
            ProfileTag(
                text = "${profile.medicalGroup.displayName.lowercase()} (${profile.medicalGroup.name.lowercase()})",
                textColor = ColorGreen,
                backgroundColor = ColorGreenLight
            )
        }
    }
}

@Composable
private fun ProfileTag(
    text: String,
    textColor: Color,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = ColorTextLight,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun BiometricsCard(profile: UserProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BiometricItem(label = "Возраст", value = profile.age.toString(), unit = "лет")
            BiometricDivider()
            BiometricItem(label = "Рост", value = profile.heightCm.toString(), unit = "см")
            BiometricDivider()
            BiometricItem(label = "Вес", value = "%.0f".format(profile.weightKg), unit = "кг")
            BiometricDivider()
            BiometricItem(
                label = "ИМТ",
                value = "%.1f".format(profile.bmi),
                unit = profile.bmiLabel.lowercase(),
                valueColor = ColorGreen
            )
        }
    }
}

@Composable
private fun BiometricItem(
    label: String,
    value: String,
    unit: String,
    valueColor: Color = ColorTextDark
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = ColorTextLight
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = unit,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = if (valueColor == ColorGreen) ColorGreen else ColorTextLight
        )
    }
}

@Composable
private fun BiometricDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(48.dp)
            .background(ColorDivider)
    )
}

@Composable
private fun MedicalCard(
    profile: UserProfile,
    onNavigateToMuscleFatigue: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onNavigateToMuscleFatigue() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Health group row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Группа здоровья",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextDark
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ColorGreenLight)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${profile.medicalGroup.displayName} (${profile.medicalGroup.name.lowercase()})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = ColorDivider)
            Spacer(modifier = Modifier.height(12.dp))

            // Contraindications
            Text(
                text = "Противопоказания:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ColorTextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "• ",
                    fontSize = 13.sp,
                    color = ColorRed,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Осевая нагрузка на колени (до 01.08.2026)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = ColorRed
                )
            }
        }
    }
}

@Composable
private fun PhysicalTestsCard(onNavigateToStats: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onNavigateToStats() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Column headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.weight(1.5f))
                listOf("Сила", "Выносл.", "Гибкость", "Норма").forEach { header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(1f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorTextLight,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = ColorDivider)
            Spacer(modifier = Modifier.height(12.dp))

            // Push-ups row
            PhysicalTestRow(
                icon = "🏅",
                name = "Отжимания",
                values = listOf("32", "", "", "4/4")
            )
        }
    }
}

@Composable
private fun PhysicalTestRow(
    icon: String,
    name: String,
    values: List<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = icon, fontSize = 14.sp)
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ColorTextDark
            )
        }
        values.forEach { value ->
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (value.contains("/")) ColorGreen else ColorTextDark,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onBackClick = {},
        onNavigateToMuscleFatigue = {},
        onNavigateToStats = {}
    )
}
