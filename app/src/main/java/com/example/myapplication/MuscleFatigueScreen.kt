package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.androidx.compose.koinViewModel

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorDarkText = Color(0xFF0F0F23)
private val ColorMutedText = Color(0x660F0F23)
private val ColorRed = Color(0xFFF87171)
private val ColorYellow = Color(0xFFFBBF24)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorCardBorder = Color(0x336C63FF)
private val ColorListBorder = Color(0x12000000)
private val ColorShadow = Color(0x0F000000)
private val ColorYellowIconBg = Color(0x26FBBF24)
private val ColorGreenChipBorder = Color(0x404ADE80)
private val ColorGreenChipBg = Color(0x1A4ADE80)

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

class MuscleFatigueViewModel(
    private val repository: InjuryRepository
) : ViewModel() {
    
    var uiState by mutableStateOf<MuscleFatigueUiState>(MuscleFatigueUiState.Loading)
        private set

    fun loadMuscleData() {
        uiState = MuscleFatigueUiState.Loading
        uiState = MuscleFatigueUiState.Loaded(emptyList(), emptyList())
    }
}

@Composable
fun MuscleFatigueScreen(onBackClick: () -> Unit = {}) {
    val viewModel: MuscleFatigueViewModel = koinViewModel()
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadMuscleData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))))
            .verticalScroll(rememberScrollState())
    ) {
        NavBar(onBackClick)
        ScreenHeader()
        Spacer(modifier = Modifier.height(8.dp))
        WhenStatusCard(modifier = Modifier.padding(horizontal = 20.dp), uiState = uiState)
        Spacer(modifier = Modifier.height(12.dp))
        MuscleListCard(modifier = Modifier.padding(horizontal = 20.dp), uiState = uiState)
        Spacer(modifier = Modifier.height(112.dp))
    }
}

@Composable
private fun WhenStatusCard(modifier: Modifier = Modifier, uiState: MuscleFatigueUiState) {
    val recoveringCount = when (uiState) {
        is MuscleFatigueUiState.Loaded -> uiState.recovering.size
        else -> 0
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = ColorShadow)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0x146C63FF), Color(0x0D4ECDC4)),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(1000f, 400f)
                )
            )
            .border(1.dp, ColorCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ColorYellowIconBg)
            ) {
                Text(text = "⏳", fontSize = 28.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (recoveringCount > 0) "$recoveringCount группы" else "--",
                    color = ColorDarkText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = if (recoveringCount > 0) "ещё восстанавливаются" else "Нет данных о восстановлении",
                    color = ColorMutedText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun NavBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.TextButton(
            onClick = onBackClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "‹ Профиль",
                color = ColorPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun ScreenHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 4.dp)
    ) {
        Text(
            text = "💪 Мышцы",
            color = ColorDarkText,
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 39.sp,
            modifier = Modifier.padding(top = 3.5.dp, bottom = 16.dp)
        )
        Text(
            text = "Состояние восстановления мышечных групп",
            color = ColorMutedText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun MuscleListCard(modifier: Modifier = Modifier, uiState: MuscleFatigueUiState) {
    val muscles = when (uiState) {
        is MuscleFatigueUiState.Loaded -> uiState.recovering
        else -> emptyList()
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = ColorShadow)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, ColorListBorder, RoundedCornerShape(16.dp))
            .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (muscles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "--",
                    color = ColorMutedText,
                    fontSize = 16.sp
                )
            }
        } else {
            muscles.forEachIndexed { index, muscle ->
                MuscleRow(muscle)
                if (index < muscles.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x0A000000))
                    )
                }
            }
        }
    }
}

@Composable
private fun MuscleRow(muscle: MuscleGroup) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(muscle.dotColor)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = muscle.name,
                    color = ColorDarkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = muscle.hoursRemaining,
                    color = muscle.barColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            RecoveryProgressBar(
                percent = muscle.recoveryPercent,
                barColor = muscle.barColor
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${muscle.recoveryPercent}% восстановлено",
                    color = ColorMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = "из ${muscle.totalHours}ч",
                    color = ColorMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun RecoveryProgressBar(percent: Int, barColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0x0F000000))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percent / 100f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(3.dp))
                .background(barColor)
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun FullyRecoveredSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "✅", fontSize = 14.sp)
            Text(
                text = "ПОЛНОСТЬЮ ВОССТАНОВЛЕНЫ",
                color = ColorDarkText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
            )
        }
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("--", color = ColorMutedText, fontSize = 13.sp)
        }
    }
}

@Composable
private fun RecoveredChip(label: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ColorGreenChipBg)
            .border(1.dp, ColorGreenChipBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = ColorGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F7FF, widthDp = 393, heightDp = 866)
@Composable
fun MuscleFatigueScreenPreview() {
    MuscleFatigueScreen()
}