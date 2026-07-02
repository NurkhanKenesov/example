package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.example.myapplication.data.models.ModelInfo
import com.example.myapplication.data.models.RetrainHistoryEntry
import com.example.myapplication.data.models.RetrainStatus

// ── Colours (local to this screen) ───────────────────────────────────────────

private val ScreenBg     = Color(0xFFF5F7FF)
private val Dark         = Color(0xFF0F0F23)
private val Dark50       = Color(0x800F0F23)
private val Dark40       = Color(0x660F0F23)
private val Dark30       = Color(0x4D0F0F23)
private val Purple       = Color(0xFF6C63FF)
private val Violet       = Color(0xFF8B5CF6)
private val CardBorder   = Color(0x120F0F23)
private val PurpleBg     = Color(0x146C63FF)
private val PurpleBorder = Color(0x336C63FF)
private val Green        = Color(0xFF4ADE80)
private val GreenBg      = Color(0x264ADE80)
private val Yellow       = Color(0xFFFBBF24)
private val YellowBg     = Color(0x26FBBF24)
private val Red          = Color(0xFFF87171)
private val TrackBg      = Color(0x0F000000)

private val PurpleGradient = Brush.linearGradient(
    colors = listOf(Purple, Violet),
    start = Offset.Zero, end = Offset.Infinite
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun ModelScreen(onBackClick: () -> Unit = {}) {
    val viewModel: ModelViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                ModelScreenHeader(onBackClick = onBackClick)
            }

            when (val state = uiState) {
                is ModelUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Purple) }
                    }
                }
                is ModelUiState.Error -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.message,
                                color = Red,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
                is ModelUiState.Loaded -> {
                    val model = state.modelInfo

                    item {
                        ModelStatusCard(model = model, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(20.dp))
                    }

                    item {
                        SectionLabel(text = "ГОТОВНОСТЬ К ПЕРЕОБУЧЕНИЮ", modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(10.dp))
                        RetrainReadinessCard(model = model, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(16.dp))
                    }

                    item {
                        RetrainButton(
                            onClick = { viewModel.retrain(force = false) },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        ForceRetrainButton(
                            onClick = { viewModel.retrain(force = true) },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(Modifier.height(24.dp))
                    }

                    item {
                        SectionLabel(text = "ИСТОРИЯ ПЕРЕОБУЧЕНИЙ", modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(10.dp))
                    }

                    items(model.history, key = { it.id }) { entry ->
                        RetrainHistoryCard(
                            entry = entry,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun ModelScreenHeader(onBackClick: () -> Unit) {
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
            text = "🤖 Модель",
            fontSize = 34.sp,
            fontWeight = FontWeight.W800,
            color = Dark,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(2.dp))
        Text(
            text = "Управление ML-моделью рекомендаций",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Dark50,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(16.dp))
    }
}

// ── Status card ───────────────────────────────────────────────────────────────

@Composable
private fun ModelStatusCard(model: ModelInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (model.isActive) Green else Red),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (model.isActive) "✅" else "⛔", fontSize = 14.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = if (model.isActive) "Модель активна" else "Модель неактивна",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${model.fileName} • ${model.algorithm}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Dark50
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModelStatTile(value = model.retrainCount.toString(), label = "Переобучений", valueColor = Green, modifier = Modifier.weight(1f))
            ModelStatTile(value = model.newDataCount.toString(), label = "Новых данных", valueColor = Yellow, modifier = Modifier.weight(1f))
            ModelStatTile(value = model.threshold.toString(), label = "Порог", valueColor = Purple, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ModelStatTile(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ScreenBg)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Spacer(Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Normal, color = Dark50, textAlign = TextAlign.Center)
    }
}

// ── Retrain readiness ─────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Dark30,
        modifier = modifier
    )
}

@Composable
private fun RetrainReadinessCard(model: ModelInfo, modifier: Modifier = Modifier) {
    val percent = if (model.threshold > 0) (model.newDataCount * 100 / model.threshold).coerceIn(0, 100) else 0
    val remaining = (model.threshold - model.newDataCount).coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Новые взаимодействия", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Dark)
            Text(
                text = "${model.newDataCount} / ${model.threshold}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Yellow
            )
        }
        Spacer(Modifier.height(10.dp))
        RetrainProgressBar(percent = percent, barColor = Yellow)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Ещё $remaining взаимодействий до автоматического переобучения",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Dark50
        )
    }
}

@Composable
private fun RetrainProgressBar(percent: Int, barColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(TrackBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percent / 100f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(barColor)
        )
    }
}

// ── Action buttons ────────────────────────────────────────────────────────────

@Composable
private fun RetrainButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(PurpleGradient)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔄 Переобучить модель",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun ForceRetrainButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(PurpleBg)
            .border(1.dp, PurpleBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "⚡ Force Retrain",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Purple
        )
    }
}

// ── Retrain history ───────────────────────────────────────────────────────────

@Composable
private fun RetrainHistoryCard(entry: RetrainHistoryEntry, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            RetrainStatusBadge(status = entry.status)
            Spacer(Modifier.height(6.dp))
            Text(text = entry.date, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Dark)
            Spacer(Modifier.height(2.dp))
            Text(
                text = "AUC: ${entry.aucBefore} → ${entry.aucAfter}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Dark40
            )
        }
    }
}

@Composable
private fun RetrainStatusBadge(status: RetrainStatus) {
    val (label, textColor, bgColor) = when (status) {
        RetrainStatus.REPLACED -> Triple("replaced", Green, GreenBg)
        RetrainStatus.SKIPPED  -> Triple("skipped", Dark50, CardBorder)
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

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ModelScreenPreview() {
    MaterialTheme {
        ModelScreen()
    }
}
