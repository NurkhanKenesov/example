package com.example.myapplication.data.models

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

data class QuizState(
    val currentIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val isCompleted: Boolean = false
)
