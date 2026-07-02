package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.ModelInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ModelUiState {
    object Loading : ModelUiState
    data class Loaded(val modelInfo: ModelInfo) : ModelUiState
    data class Error(val message: String) : ModelUiState
}

class ModelViewModel(
    private val repository: ModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ModelUiState>(ModelUiState.Loading)
    val uiState: StateFlow<ModelUiState> = _uiState.asStateFlow()

    init {
        loadModel()
    }

    fun loadModel() {
        viewModelScope.launch {
            _uiState.value = ModelUiState.Loading
            repository.getModelInfo().fold(
                onSuccess = { _uiState.value = ModelUiState.Loaded(it) },
                onFailure = { e -> _uiState.value = ModelUiState.Error(e.message ?: "Ошибка загрузки модели") }
            )
        }
    }

    fun retrain(force: Boolean = false) {
        viewModelScope.launch {
            repository.retrainModel(force).fold(
                onSuccess = { _uiState.value = ModelUiState.Loaded(it) },
                onFailure = { e -> _uiState.value = ModelUiState.Error(e.message ?: "Ошибка переобучения модели") }
            )
        }
    }
}
