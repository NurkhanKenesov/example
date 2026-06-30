package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface QuizScoresUiState {
    object Loading : QuizScoresUiState
    data class Loaded(val scores: List<QuizScore>) : QuizScoresUiState
    data class Error(val message: String) : QuizScoresUiState
}

class QuizScoresViewModel(
    private val repository: QuizScoreRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<QuizScoresUiState>(QuizScoresUiState.Loading)
    val uiState: StateFlow<QuizScoresUiState> = _uiState.asStateFlow()
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun loadScores() {
        viewModelScope.launch {
            _uiState.value = QuizScoresUiState.Loading
            repository.getQuizScores().fold(
                onSuccess = { scores ->
                    _uiState.value = QuizScoresUiState.Loaded(scores)
                },
                onFailure = { e ->
                    _uiState.value = QuizScoresUiState.Error(e.message ?: "Ошибка загрузки результатов")
                }
            )
        }
    }

    fun saveScore(score: Int, totalQuestions: Int) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.saveQuizScore(score, totalQuestions)
            _isSaving.value = false
        }
    }
}