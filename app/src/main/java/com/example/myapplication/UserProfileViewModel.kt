package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Loaded(val profile: UserProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class UserProfileViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileUiState.Loading
            userProfileRepository.getProfile().fold(
                onSuccess = { profile ->
                    if (profile != null) {
                        _state.value = ProfileUiState.Loaded(profile)
                    } else {
                        _state.value = ProfileUiState.Loaded(UserProfile())
                    }
                },
                onFailure = { e ->
                    _state.value = ProfileUiState.Error(e.message ?: "Ошибка загрузки профиля")
                }
            )
        }
    }

    fun saveProfile(profile: UserProfile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            userProfileRepository.saveProfile(profile).fold(
                onSuccess = { onSuccess() },
                onFailure = { e ->
                    _state.update { current ->
                        if (current is ProfileUiState.Loaded) {
                            ProfileUiState.Error(e.message ?: "Ошибка сохранения")
                        } else current
                    }
                }
            )
            _isSaving.value = false
        }
    }

    fun updateField(key: String, value: Any) {
        viewModelScope.launch {
            userProfileRepository.updateField(key, value).fold(
                onSuccess = { loadProfile() },
                onFailure = { /* silently ignore */ }
            )
        }
    }

    fun saveRole(role: UserRole, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            userProfileRepository.updateField("role", role.name).fold(
                onSuccess = { loadProfile() },
                onFailure = { /* silently ignore */ }
            )
            onComplete()
        }
    }

    fun isProfileComplete(): Boolean {
        val loaded = _state.value as? ProfileUiState.Loaded ?: return false
        return loaded.profile.profileComplete
    }
}
