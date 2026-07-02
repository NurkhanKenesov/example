package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GeneratedExercise(
    val name: String,
    val description: String,
    val sets: Int,
    val reps: Int,
    val emoji: String,
    val section: String
)

data class GeneratedPlan(
    val title: String,
    val days: List<String>,
    val exercises: List<GeneratedExercise>
)

sealed interface AiPlanState {
    object Idle : AiPlanState
    object Loading : AiPlanState
    data class Success(val plan: GeneratedPlan) : AiPlanState
    data class Error(val message: String) : AiPlanState
}

class AiPlanGeneratorViewModel : ViewModel() {

    private val _state = MutableStateFlow<AiPlanState>(AiPlanState.Idle)
    val state: StateFlow<AiPlanState> = _state.asStateFlow()

    fun generatePlan(userGoal: String, medicalGroup: String = "BASIC") {
        viewModelScope.launch {
            _state.value = AiPlanState.Loading
            val prompt = """
                Ты персональный тренер. Создай план тренировок.
                Запрос пользователя: $userGoal
                Медицинская группа: $medicalGroup (BASIC=полная нагрузка, PREPARATORY=умеренная, SPECIAL=минимальная)
                
                Верни ТОЛЬКО JSON без markdown, без пояснений:
                {
                  "title": "Название плана",
                  "days": ["ПН", "СР", "ПТ"],
                  "exercises": [
                    {"name": "Jumping Jacks", "description": "Full body warmup", "sets": 2, "reps": 20, "emoji": "🤸", "section": "warmup"},
                    {"name": "Push-ups", "description": "Chest and triceps", "sets": 3, "reps": 12, "emoji": "💪", "section": "main"},
                    {"name": "Hamstring Stretch", "description": "Flexibility", "sets": 2, "reps": 30, "emoji": "🧘", "section": "cooldown"}
                  ]
                }
                
                Требования: 2-3 упражнения warmup, 4-5 упражнений main, 2 упражнения cooldown.
            """.trimIndent()

            GeminiRepository.sendMessage(prompt).fold(
                onSuccess = { response ->
                    try {
                        val json = response
                            .trim()
                            .removePrefix("```json")
                            .removePrefix("```")
                            .removeSuffix("```")
                            .trim()
                        _state.value = AiPlanState.Success(parseGeneratedPlan(json))
                    } catch (e: Exception) {
                        _state.value = AiPlanState.Error("Ошибка разбора ответа ИИ")
                    }
                },
                onFailure = { e ->
                    _state.value = AiPlanState.Error(e.message ?: "Ошибка генерации плана")
                }
            )
        }
    }

    private fun parseGeneratedPlan(json: String): GeneratedPlan {
        val title = Regex("\"title\"\\s*:\\s*\"([^\"]+)\"")
            .find(json)?.groupValues?.get(1) ?: "Мой план"
        val daysRaw = Regex("\"days\"\\s*:\\s*\\[([^\\]]+)\\]")
            .find(json)?.groupValues?.get(1) ?: ""
        val days = Regex("\"([^\"]+)\"").findAll(daysRaw)
            .map { it.groupValues[1] }.toList()

        val exercises = mutableListOf<GeneratedExercise>()
        val blocks = Regex("\\{[^{}]+\\}", RegexOption.DOT_MATCHES_ALL).findAll(json)
        blocks.forEach { block ->
            val text = block.value
            if (!text.contains("\"section\"")) return@forEach
            val name = Regex("\"name\"\\s*:\\s*\"([^\"]+)\"").find(text)?.groupValues?.get(1) ?: return@forEach
            val desc = Regex("\"description\"\\s*:\\s*\"([^\"]+)\"").find(text)?.groupValues?.get(1) ?: ""
            val sets = Regex("\"sets\"\\s*:\\s*(\\d+)").find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 3
            val reps = Regex("\"reps\"\\s*:\\s*(\\d+)").find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 10
            val emoji = Regex("\"emoji\"\\s*:\\s*\"([^\"]+)\"").find(text)?.groupValues?.get(1) ?: "💪"
            val section = Regex("\"section\"\\s*:\\s*\"([^\"]+)\"").find(text)?.groupValues?.get(1) ?: "main"
            exercises.add(GeneratedExercise(name, desc, sets, reps, emoji, section))
        }
        return GeneratedPlan(title = title, days = days, exercises = exercises)
    }

    fun reset() { _state.value = AiPlanState.Idle }
}
