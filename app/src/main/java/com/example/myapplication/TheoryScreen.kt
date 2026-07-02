package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorDark = Color(0xFF0F0F23)
private val ColorBackground = Color(0xFFF5F7FF)
private val ColorSurface = Color(0xFFF0F2FF)
private val ColorCardBg = Color(0xFFFFFFFF)

@Composable
fun TheoryScreen(
    onStartQuiz: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(ColorSurface, ColorBackground))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                TextButton(
                    onClick = onBackClick,
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
                    text = "Теория",
                    color = ColorDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header with icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ColorPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "📖", fontSize = 24.sp)
                            }
                            Column {
                                Text(
                                    text = "Основы ЗОЖ",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorDark
                                )
                                Text(
                                    text = "Здоровый образ жизни",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = ColorDark.copy(alpha = 0.5f)
                                )
                            }
                        }

                        HorizontalDivider(color = ColorDark.copy(alpha = 0.07f))

                        // Theory content
                        TheoryParagraph(
                            title = "Что такое ЗОЖ?",
                            text = "Здоровый образ жизни (ЗОЖ) — это образ жизни человека, направленный на " +
                                    "сохранение и укрепление здоровья, профилактику заболеваний и обеспечение " +
                                    "высокого уровня работоспособности. Это комплексная система поведения, " +
                                    "включающая рациональное питание, физическую активность, соблюдение режима " +
                                    "труда и отдыха, отказ от вредных привычек."
                        )

                        TheoryParagraph(
                            title = "Физическая активность",
                            text = "Регулярная физическая активность — важнейший компонент ЗОЖ. Всемирная " +
                                    "организация здравоохранения рекомендует не менее 150 минут умеренной " +
                                    "аэробной активности или 75 минут интенсивной активности в неделю. " +
                                    "Физическая культура в вузе включает не только практические занятия, " +
                                    "но и формирование осознанной потребности в движении."
                        )

                        TheoryParagraph(
                            title = "Рациональное питание",
                            text = "Сбалансированное питание — основа здоровья. Рекомендуется соблюдать " +
                                    "соотношение белков, жиров и углеводов примерно 1:1:4, употреблять " +
                                    "достаточное количество овощей и фруктов (не менее 400 г в день), " +
                                    "ограничить потребление сахара, соли и насыщенных жиров. Оптимальный " +
                                    "питьевой режим — 1.5–2 литра воды в день."
                        )

                        TheoryParagraph(
                            title = "Режим дня и сон",
                            text = "Здоровый сон продолжительностью 7–9 часов необходим для восстановления " +
                                    "организма, укрепления иммунитета и нормальной работы нервной системы. " +
                                    "Соблюдение режима дня, чередование умственной и физической нагрузки, " +
                                    "регулярные перерывы в работе повышают продуктивность и предотвращают " +
                                    "переутомление."
                        )

                        TheoryParagraph(
                            title = "Закаливание и профилактика",
                            text = "Закаливание — система процедур, повышающая устойчивость организма к " +
                                    "неблагоприятным факторам окружающей среды. В сочетании с регулярными " +
                                    "медицинскими осмотрами, вакцинацией и соблюдением личной гигиены оно " +
                                    "составляет полноценную систему профилактики заболеваний."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Start quiz button
                Button(
                    onClick = onStartQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimary
                    )
                ) {
                    Text(
                        text = "Пройти тест 📝",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TheoryParagraph(title: String, text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorDark
        )
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = ColorDark.copy(alpha = 0.7f),
            lineHeight = 22.sp,
            textAlign = TextAlign.Start
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TheoryScreenPreview() {
    TheoryScreen()
}
