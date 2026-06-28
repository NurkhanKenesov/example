package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Loaded(val profile: UserProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class UserProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    // Отдельный флаг — сохраняется ли прямо сейчас
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        loadProfile()
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _state.value = ProfileUiState.Loading
            try {
                val doc = db.collection("users").document(uid).get().await()
                val profile = if (doc.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    UserProfile.fromMap(doc.data as Map<String, Any>)
                } else {
                    // Первый вход — создаём пустой профиль с данными из Firebase Auth
                    UserProfile(
                        uid   = uid,
                        email = auth.currentUser?.email ?: "",
                        name  = auth.currentUser?.displayName ?: ""
                    )
                }
                _state.value = ProfileUiState.Loaded(profile)
            } catch (e: Exception) {
                _state.value = ProfileUiState.Error(e.localizedMessage ?: "Ошибка загрузки профиля")
            }
        }
    }

    // ── Save (setup after register) ───────────────────────────────────────────

    fun saveProfile(profile: UserProfile, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val finalProfile = profile.copy(
                    uid             = uid,
                    email           = auth.currentUser?.email ?: profile.email,
                    profileComplete = true
                )
                db.collection("users")
                    .document(uid)
                    .set(finalProfile.toMap())
                    .await()
                _state.value = ProfileUiState.Loaded(finalProfile)
                onSuccess()
            } catch (e: Exception) {
                _state.update { current ->
                    if (current is ProfileUiState.Loaded) {
                        ProfileUiState.Error(e.localizedMessage ?: "Ошибка сохранения")
                    } else current
                }
            } finally {
                _isSaving.value = false
            }
        }
    }

    // ── Update individual fields (из ProfileScreen) ───────────────────────────

    fun updateField(key: String, value: Any) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(uid).update(key, value).await()
                // Перезагружаем чтобы state был консистентен
                loadProfile()
            } catch (_: Exception) { }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    fun isProfileComplete(): Boolean {
        val loaded = _state.value as? ProfileUiState.Loaded ?: return false
        return loaded.profile.profileComplete
    }
}
