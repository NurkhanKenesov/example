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
    data class WorkoutPlanLoaded(val plan: WorkoutPlan) : PlansUiState
    data class Error(val message: String) : PlansUiState
}

class PlansViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlansUiState>(PlansUiState.Loading)
    val uiState: StateFlow<PlansUiState> = _uiState.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _profileMedicalGroup = MutableStateFlow(MedicalGroup.BASIC)
    val profileMedicalGroup: StateFlow<MedicalGroup> = _profileMedicalGroup.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            repository.getUserMedicalGroup().fold(
                onSuccess = { group ->
                    _profileMedicalGroup.value = group
                },
                onFailure = { /* silently ignore */ }
            )
        }
    }

    fun loadAssignedWorkoutPlan() {
        viewModelScope.launch {
            _uiState.value = PlansUiState.Loading
            repository.getUserWorkoutPlan().fold(
                onSuccess = { plan ->
                    if (plan != null) {
                        _uiState.value = PlansUiState.WorkoutPlanLoaded(plan)
                    } else {
                        loadTemplateWorkoutPlan(_profileMedicalGroup.value)
                    }
                },
                onFailure = { e ->
                    _uiState.value = PlansUiState.Error(e.message ?: "Ошибка загрузки плана")
                }
            )
        }
    }

    private fun loadTemplateWorkoutPlan(medicalGroup: MedicalGroup) {
        viewModelScope.launch {
            repository.getWorkoutPlans().fold(
                onSuccess = { plans ->
                    val matchingPlan = plans.find { it.targetMedicalGroup == medicalGroup }
                    if (matchingPlan != null) {
                        _uiState.value = PlansUiState.WorkoutPlanLoaded(matchingPlan)
                    } else {
                        _uiState.value = PlansUiState.Error("План для медгруппы '${medicalGroup.displayName}' не найден")
                    }
                },
                onFailure = { e ->
                    _uiState.value = PlansUiState.Error(e.message ?: "Ошибка загрузки плана")
                }
            )
        }
    }

    fun assignWorkoutPlan(plan: WorkoutPlan, onResult: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.saveUserWorkoutPlan(plan).also { result ->
                if (result.isSuccess) {
                    _uiState.value = PlansUiState.WorkoutPlanLoaded(plan)
                }
                onResult(result)
                _isSaving.value = false
            }
        }
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
