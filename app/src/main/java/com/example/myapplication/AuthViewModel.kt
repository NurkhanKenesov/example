package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    val currentUserEmail: String
        get() = auth.currentUser?.email.orEmpty()

    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registerState: StateFlow<AuthUiState> = _registerState.asStateFlow()

    // ── Login ────────────────────────────────────────────────────────────────

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthUiState.Loading
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                _loginState.value = AuthUiState.Success("Вход выполнен")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _loginState.value = AuthUiState.Error("Неверный email или пароль")
            } catch (e: Exception) {
                _loginState.value = AuthUiState.Error(e.localizedMessage ?: "Ошибка входа")
            }
        }
    }

    fun clearLoginError() {
        _loginState.value = AuthUiState.Idle
    }

    // ── Register ──────────────────────────────────────────────────────────────

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = AuthUiState.Loading
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password).await()
                _registerState.value = AuthUiState.Success("Аккаунт создан")
            } catch (e: FirebaseAuthWeakPasswordException) {
                _registerState.value = AuthUiState.Error("Пароль слишком слабый")
            } catch (e: FirebaseAuthUserCollisionException) {
                _registerState.value = AuthUiState.Error("Этот email уже зарегистрирован")
            } catch (e: Exception) {
                _registerState.value = AuthUiState.Error(e.localizedMessage ?: "Ошибка регистрации")
            }
        }
    }

    fun clearRegisterError() {
        _registerState.value = AuthUiState.Idle
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    fun logout() {
        auth.signOut()
        _loginState.value = AuthUiState.Idle
        _registerState.value = AuthUiState.Idle
    }

    // ── Check current session ─────────────────────────────────────────────────

    fun isLoggedIn(): Boolean = auth.currentUser != null
}
