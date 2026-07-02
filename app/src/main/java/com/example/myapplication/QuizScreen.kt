package com.example.myapplication

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.androidx.compose.koinViewModel
import com.example.myapplication.data.models.QuizQuestion
import com.example.myapplication.data.models.QuizState

// ── Quiz questions ────────────────────────────────────────────────────────────

private val quizQuestions = listOf(
    QuizQuestion(
        text = "Что такое здоровый образ жизни (ЗОЖ)?",
        options = listOf(
            "Образ жизни, направленный на укрепление здоровья и профилактику заболеваний",
            "Только занятие профессиональным спортом",
            "Полный отказ от любой еды",
            "Регулярное посещение врачей без других изменений"
        ),
        correctIndex = 0
    ),
    QuizQuestion(
        text = "Сколько минут умеренной физической активности рекомендуется в неделю по нормам ВОЗ?",
        options = listOf(
            "Не менее 60 минут",
            "Не менее 90 минут",
            "Не менее 150 минут",
            "Не менее 300 минут"
        ),
        correctIndex = 2
    ),
    QuizQuestion(
        text = "Какое соотношение белков, жиров и углеводов рекомендуется в сбалансированном питании?",
        options = listOf(
            "Белки 30%, жиры 30%, углеводы 40%",
            "Белки 15%, жиры 30%, углеводы 55%",
            "Белки 40%, жиры 20%, углеводы 40%",
            "Белки 20%, жиры 40%, углеводы 40%"
        ),
        correctIndex = 1
    ),
    QuizQuestion(
        text = "Что такое физическая культура?",
        options = listOf(
            "Только профессиональный спорт и соревнования",
            "Занятия исключительно в тренажёрном зале",
            "Часть культуры общества, совокупность средств физического совершенствования человека",
            "Система медицинских процедур"
        ),
        correctIndex = 2
    ),
    QuizQuestion(
        text = "Сколько воды рекомендуется выпивать в день взрослому человеку?",
        options = listOf(
            "0.5–1 литр",
            "1–1.5 литра",
            "1.5–2 литра",
            "3–4 литра"
        ),
        correctIndex = 2
    ),
    QuizQuestion(
        text = "Сколько часов здорового сна рекомендуется взрослому человеку?",
        options = listOf(
            "4–5 часов",
            "6–8 часов",
            "9–10 часов",
            "10–12 часов"
        ),
        correctIndex = 1
    ),
    QuizQuestion(
        text = "Что такое закаливание?",
        options = listOf(
            "Система процедур, повышающая устойчивость организма к внешним воздействиям",
            "Исключительно купание в холодной воде",
            "Обязательное зимнее плавание",
            "Ежедневное обтирание снегом"
        ),
        correctIndex = 0
    ),
    QuizQuestion(
        text = "Какой фактор НЕ относится к здоровому образу жизни?",
        options = listOf(
            "Рациональное питание",
            "Режим труда и отдыха",
            "Регулярная физическая активность",
            "Употребление алкоголя"
        ),
        correctIndex = 3
    )
)

// ── Colors ────────────────────────────────────────────────────────────────────

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorDark = Color(0xFF0F0F23)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorRed = Color(0xFFF87171)
private val ColorBackground = Color(0xFFF5F7FF)
private val ColorSurface = Color(0xFFF0F2FF)
private val ColorCardBg = Color(0xFFFFFFFF)

@Composable
fun QuizScreen(
    onQuizComplete: (Int, Int) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {}
) {
    var quizState by remember { mutableStateOf(QuizState()) }
    val scrollState = rememberScrollState()
    val totalQuestions = quizQuestions.size
    val currentQuestion = quizQuestions[quizState.currentIndex]
    val selectedAnswer = quizState.selectedAnswers[quizState.currentIndex]
    val progress = (quizState.currentIndex + 1).toFloat() / totalQuestions
    val quizScoresViewModel: QuizScoresViewModel = koinViewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(ColorSurface, ColorBackground))
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    text = "Тест",
                    color = ColorDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Progress bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Вопрос ${quizState.currentIndex + 1} из $totalQuestions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorDark.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ColorPrimary,
                    trackColor = ColorPrimary.copy(alpha = 0.12f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Question content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Question card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Question number badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorPrimary.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Вопрос ${quizState.currentIndex + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentQuestion.text,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorDark,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Options
                currentQuestion.options.forEachIndexed { index, optionText ->
                    val isSelected = selectedAnswer == index
                    QuizOption(
                        text = optionText,
                        index = index,
                        isSelected = isSelected,
                        onClick = {
                            quizState = quizState.copy(
                                selectedAnswers = quizState.selectedAnswers + (quizState.currentIndex to index)
                            )
                        }
                    )
                }
            }
        }

        // Bottom button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(ColorCardBg)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = {
                    if (selectedAnswer != null) {
                        if (quizState.currentIndex < totalQuestions - 1) {
                            quizState = quizState.copy(
                                currentIndex = quizState.currentIndex + 1
                            )
                        } else {
                            val correctCount = quizQuestions.filterIndexed { index, question ->
                                quizState.selectedAnswers[index] == question.correctIndex
                            }.size
                            quizState = quizState.copy(isCompleted = true)
                            quizScoresViewModel.saveScore(correctCount, totalQuestions)
                            onQuizComplete(correctCount, totalQuestions)
                        }
                    }
                },
                enabled = selectedAnswer != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimary,
                    disabledContainerColor = ColorPrimary.copy(alpha = 0.35f)
                )
            ) {
                Text(
                    text = if (quizState.currentIndex < totalQuestions - 1) "Далее →" else "Завершить ✓",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun QuizOption(
    text: String,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val labels = listOf("А", "Б", "В", "Г")
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) ColorPrimary.copy(alpha = 0.1f) else ColorCardBg,
        animationSpec = tween(200),
        label = "optionBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) ColorPrimary else ColorDark.copy(alpha = 0.08f),
        animationSpec = tween(200),
        label = "optionBorder"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) ColorPrimary else ColorDark.copy(alpha = 0.06f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labels.getOrElse(index) { "${index + 1}" },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else ColorDark.copy(alpha = 0.5f)
                )
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = ColorDark,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .border(
                        width = 2.dp,
                        color = if (isSelected) ColorPrimary else ColorDark.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(11.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ColorPrimary)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizScreenPreview() {
    QuizScreen()
}
