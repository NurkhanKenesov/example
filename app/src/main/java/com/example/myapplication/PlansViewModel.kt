package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PlansUiState {
    object Loading : PlansUiState
    data class Loaded(val plans: List<Plan>) : PlansUiState
    data class Error(val message: String) : PlansUiState
}

class PlansViewModel : ViewModel() {
    private val repository = PlanRepository()
    
    private val _uiState = MutableStateFlow<PlansUiState>(PlansUiState.Loading)
    val uiState: StateFlow<PlansUiState> = _uiState.asStateFlow()
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        loadPlans()
    }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = PlansUiState.Loading
            repository.getPlans().fold(
                onSuccess = { plans ->
                    _uiState.value = PlansUiState.Loaded(plans)
                },
                onFailure = { e ->
                    _uiState.value = PlansUiState.Error(e.message ?: "Ошибка загрузки планов")
                }
            )
        }
    }

    fun savePlan(title: String, description: String) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.savePlan(Plan(title = title, description = description))
            _isSaving.value = false
            loadPlans()
        }
    }
}