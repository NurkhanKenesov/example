package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Design tokens ──────────────────────────────────────────────────────────────

private val Ebony = Color(0xFF0F0F23)
private val EbonyAlpha80 = Color(0xCC0F0F23)
private val EbonyAlpha70 = Color(0xB30F0F23)
private val EbonyAlpha40 = Color(0x660F0F23)
private val PrimaryBlue = Color(0xFF6C63FF)
private val PrimaryViolet = Color(0xFFA78BFA)
private val SpringGreen = Color(0xFF4ADE80)
private val CyanBlue = Color(0xFF22D3EE)
private val LightningYellow = Color(0xFFFBBF24)
private val Froly = Color(0xFFF87171)
private val ProgressTrack = Color(0x14000000)
private val PurpleCardBorder = Color(0x336C63FF)
private val PurpleCardBgStart = Color(0x146C63FF)
private val PurpleCardBgEnd = Color(0x0D4ECDC4)
private val ShapButtonBg = Color(0xFFEEEBFF)

// ── Data ───────────────────────────────────────────────────────────────────────

private data class FactorRow(
    val label: String,
    val value: String,
    val valueColor: Color,
    val barColor: Color,
    val barFraction: Float,
)

private val factors = listOf(
    FactorRow("Физ. готовность", "+0.24", SpringGreen, SpringGreen, 0.85f),
    FactorRow("Свежесть мышц", "+0.18", SpringGreen, CyanBlue, 0.70f),
    FactorRow("История выполнения", "+0.12", PrimaryBlue, PrimaryBlue, 0.50f),
    FactorRow("Мед. группа", "+0.08", PrimaryBlue, PrimaryBlue, 0.35f),
    FactorRow("Сложность упр.", "-0.05", Froly, Froly, 0.18f),
)

// ── Screen ─────────────────────────────────────────────────────────────────────

@Composable
fun AIExplanationScreen(onBackClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AiTopNavBar(onBackClick = onBackClick)
            AiHeaderSection()
            Spacer(Modifier.height(8.dp))
            PushUpsExplanationCard()
            Spacer(Modifier.height(12.dp))
            SquatsExplanationCard()
            Spacer(Modifier.height(4.dp))
            KeyFactorsSectionLabel()
            Spacer(Modifier.height(8.dp))
            KeyFactorsCard()
            Spacer(Modifier.height(16.dp))
            ShapButton()
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Top navigation bar ─────────────────────────────────────────────────────────

@Composable
private fun AiTopNavBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "‹ План",
            color = PrimaryBlue,
            fontSize = 17.sp,
            fontWeight = FontWeight.W400,
            modifier = Modifier.clickable { onBackClick() },
        )
        Text(
            text = "🧠 ИИ-Объяснение",
            color = Ebony,
            fontSize = 17.sp,
            fontWeight = FontWeight.W600,
        )
        Spacer(Modifier.width(56.dp))
    }
}

// ── AI header (avatar + title + subtitle) ──────────────────────────────────────

@Composable
private fun AiHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        RobotAvatarCircle()
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Почему именно эти упражнения?",
            color = Ebony,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "XGBoost + SHAP объяснение",
            color = EbonyAlpha40,
            fontSize = 13.sp,
            fontWeight = FontWeight.W400,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RobotAvatarCircle() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = Color(0x4D6C63FF),
            )
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(PrimaryBlue, PrimaryViolet),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "🤖",
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Push-ups explanation card ──────────────────────────────────────────────────

@Composable
private fun PushUpsExplanationCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PurpleCardBorder, RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to PurpleCardBgStart,
                        1f to PurpleCardBgEnd,
                    )
                )
            )
            .padding(16.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = PrimaryViolet, fontWeight = FontWeight.W700)) {
                    append("Push-ups")
                }
                withStyle(SpanStyle(color = EbonyAlpha80, fontWeight = FontWeight.W400)) {
                    append(" рекомендованы, потому что ваш\n")
                }
                withStyle(SpanStyle(color = SpringGreen, fontWeight = FontWeight.W700)) {
                    append("уровень силы (3.5/4)")
                }
                withStyle(SpanStyle(color = EbonyAlpha80, fontWeight = FontWeight.W400)) {
                    append(" позволяет выполнять их комфортно. Исторический показатель выполнения — ")
                }
                withStyle(SpanStyle(color = Ebony, fontWeight = FontWeight.W700)) {
                    append("95%")
                }
                withStyle(SpanStyle(color = EbonyAlpha80, fontWeight = FontWeight.W400)) {
                    append(".")
                }
            },
            fontSize = 14.sp,
            lineHeight = 23.8.sp,
        )
    }
}

// ── Squats explanation card ────────────────────────────────────────────────────

@Composable
private fun SquatsExplanationCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = PrimaryViolet, fontWeight = FontWeight.W700)) {
                    append("Squats")
                }
                withStyle(SpanStyle(color = EbonyAlpha80, fontWeight = FontWeight.W400)) {
                    append(" выбраны несмотря на среднюю сложность, т.к. ваши ")
                }
                withStyle(SpanStyle(color = LightningYellow, fontWeight = FontWeight.W700)) {
                    append("мышцы ног полностью восстановлены")
                }
                withStyle(SpanStyle(color = EbonyAlpha80, fontWeight = FontWeight.W400)) {
                    append(" и ИМТ (23.4) в нормальном диапазоне.")
                }
            },
            fontSize = 14.sp,
            lineHeight = 23.8.sp,
        )
    }
}

// ── Key factors section label ──────────────────────────────────────────────────

@Composable
private fun KeyFactorsSectionLabel() {
    Text(
        text = "КЛЮЧЕВЫЕ ФАКТОРЫ",
        color = EbonyAlpha40,
        fontSize = 13.sp,
        fontWeight = FontWeight.W600,
        letterSpacing = 0.5.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
    )
}

// ── Key factors card ───────────────────────────────────────────────────────────

@Composable
private fun KeyFactorsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        factors.forEach { factor ->
            FactorRowItem(factor = factor)
        }
    }
}

@Composable
private fun FactorRowItem(factor: FactorRow) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = factor.label,
                color = EbonyAlpha70,
                fontSize = 13.sp,
                fontWeight = FontWeight.W400,
            )
            Text(
                text = factor.value,
                color = factor.valueColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.W700,
            )
        }
        FactorProgressBar(fraction = factor.barFraction, barColor = factor.barColor)
    }
}

@Composable
private fun FactorProgressBar(fraction: Float, barColor: Color) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(ProgressTrack),
    ) {
        val fillWidth = maxWidth * fraction
        Box(
            modifier = Modifier
                .width(fillWidth)
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(barColor),
        )
    }
}

// ── SHAP button ────────────────────────────────────────────────────────────────

@Composable
private fun ShapButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ShapButtonBg)
            .clickable { }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "📊 Показать SHAP-график",
            color = PrimaryBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.W600,
        )
    }
}



// ── Preview ────────────────────────────────────────────────────────────────────

@Preview(showSystemUi = true)
@Composable
fun AIExplanationScreenPreview() {
    AIExplanationScreen()
}
