package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorPrimaryEnd = Color(0xFF8B5CF6)
private val ColorDark = Color(0xFF0F0F23)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorYellow = Color(0xFFFBBF24)
private val ColorViolet = Color(0xFFA78BFA)

@Composable
fun WelcomeScreen(
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HeroSection(modifier = Modifier.weight(1f))
            BottomActions(
                onSignIn = onSignIn,
                onCreateAccount = onCreateAccount
            )
        }
    }
}

@Composable
private fun HeroSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppLogo()
        Spacer(modifier = Modifier.height(28.dp))
        AppTitle()
        Spacer(modifier = Modifier.height(8.dp))
        AppSubtitle()
        Spacer(modifier = Modifier.height(36.dp))
        FeaturePills()
    }
}

@Composable
private fun AppLogo() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = ColorPrimary.copy(alpha = 0.35f),
                spotColor = ColorPrimary.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(ColorPrimary, ColorPrimaryEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🏋️",
            fontSize = 48.sp
        )
    }
}

@Composable
private fun AppTitle() {
    Text(
        text = "Smart PE",
        fontSize = 36.sp,
        fontWeight = FontWeight.Black,
        color = ColorDark,
        textAlign = TextAlign.Center,
        lineHeight = 39.6.sp
    )
}

@Composable
private fun AppSubtitle() {
    Text(
        text = "Персональный ИИ-тренер для студентов университета",
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        color = ColorDark.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
        lineHeight = 22.5.sp
    )
}

@Composable
private fun FeaturePills() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeaturePill(
                label = "🤖 ИИ-планы",
                textColor = ColorViolet,
                backgroundColor = ColorPrimary.copy(alpha = 0.12f),
                borderColor = ColorPrimary.copy(alpha = 0.25f)
            )
            FeaturePill(
                label = "💪 Трекинг",
                textColor = ColorGreen,
                backgroundColor = ColorGreen.copy(alpha = 0.12f),
                borderColor = ColorGreen.copy(alpha = 0.25f)
            )
        }
        FeaturePill(
            label = "📊 Аналитика",
            textColor = ColorYellow,
            backgroundColor = ColorYellow.copy(alpha = 0.12f),
            borderColor = ColorYellow.copy(alpha = 0.25f)
        )
    }
}

@Composable
private fun FeaturePill(
    label: String,
    textColor: Color,
    backgroundColor: Color,
    borderColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
private fun BottomActions(
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SignInButton(onClick = onSignIn)
        CreateAccountButton(onClick = onCreateAccount)
    }
}

@Composable
private fun SignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(ColorPrimary, ColorPrimaryEnd)
                )
            ),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = "Войти",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun CreateAccountButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, ColorPrimary.copy(alpha = 0.30f), RoundedCornerShape(14.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPrimary.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = "Создать аккаунт",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorPrimary
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(onSignIn = {}, onCreateAccount = {})
}
