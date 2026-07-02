package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.LocalUser
import com.example.myapplication.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userProfileRepository: UserProfileRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registerState: StateFlow<AuthUiState> = _registerState.asStateFlow()

    private val _logoutComplete = MutableStateFlow(false)
    val logoutComplete: StateFlow<Boolean> = _logoutComplete.asStateFlow()

    private val _currentUser = MutableStateFlow<LocalUser?>(null)
    val currentUser: StateFlow<LocalUser?> = _currentUser.asStateFlow()

    val currentUserEmail: String
        get() = _currentUser.value?.email.orEmpty()

    val currentUserRole: String
        get() = _currentUser.value?.role ?: "Student"

    init {
        viewModelScope.launch {
            _currentUser.value = userProfileRepository.getCurrentUser()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthUiState.Loading
            userProfileRepository.login(email, password).fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _loginState.value = AuthUiState.Success("Вход выполнен")
                },
                onFailure = { e ->
                    _loginState.value = AuthUiState.Error(e.message ?: "Ошибка входа")
                }
            )
        }
    }

    fun register(email: String, password: String, role: String = "Student", name: String = "") {
        viewModelScope.launch {
            _registerState.value = AuthUiState.Loading
            userProfileRepository.register(email, password, name, role).fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _registerState.value = AuthUiState.Success("Аккаунт создан")
                    android.util.Log.d("AuthViewModel", "Registration success: uid=${user.uid}, email=${user.email}, role=${user.role}")
                },
                onFailure = { e ->
                    _registerState.value = AuthUiState.Error(e.message ?: "Ошибка регистрации")
                    android.util.Log.e("AuthViewModel", "Registration failed: ${e.message}")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            userProfileRepository.logout().fold(
                onSuccess = {
                    _currentUser.value = null
                    _loginState.value = AuthUiState.Idle
                    _registerState.value = AuthUiState.Idle
                    preferencesManager.setUserName("")
                    preferencesManager.setUserEmail("")
                    _logoutComplete.value = true
                },
                onFailure = { e ->
                    _loginState.value = AuthUiState.Error(e.message ?: "Ошибка выхода")
                }
            )
        }
    }

    fun resetLogoutState() { _logoutComplete.value = false }
    fun clearLoginError() { _loginState.value = AuthUiState.Idle }
    fun clearRegisterError() { _registerState.value = AuthUiState.Idle }
    fun isLoggedIn(): Boolean = _currentUser.value != null

    fun refreshCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = userProfileRepository.getCurrentUser()
        }
    }
}
