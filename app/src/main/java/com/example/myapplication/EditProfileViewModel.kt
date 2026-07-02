package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.PhysicalTest
import com.example.myapplication.data.models.studentPhysicalTestNorms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EditProfileUiState {
    object Loading : EditProfileUiState
    data class Loaded(
        val student: Student,
        val testValues: Map<String, String>,
        val injuries: List<Injury>
    ) : EditProfileUiState
    data class Error(val message: String) : EditProfileUiState
}

class EditProfileViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private var studentId: String = ""

    fun load(id: String) {
        studentId = id
        viewModelScope.launch {
            _uiState.value = EditProfileUiState.Loading
            val student = repository.getStudent(id).getOrNull()
            if (student == null) {
                _uiState.value = EditProfileUiState.Error("Студент не найден")
                return@launch
            }
            val testValues = repository.getPhysicalTests(id).getOrDefault(emptyList())
                .associate { it.normId to it.value }
            val injuries = repository.getInjuries(id).getOrDefault(emptyList())
            _uiState.value = EditProfileUiState.Loaded(student, testValues, injuries)
        }
    }

    fun updateMedicalGroup(group: MedicalGroup) {
        val current = _uiState.value as? EditProfileUiState.Loaded ?: return
        _uiState.value = current.copy(student = current.student.copy(medicalGroup = group))
    }

    fun updateHeight(value: Float) {
        val current = _uiState.value as? EditProfileUiState.Loaded ?: return
        _uiState.value = current.copy(student = current.student.copy(heightCm = value))
    }

    fun updateWeight(value: Float) {
        val current = _uiState.value as? EditProfileUiState.Loaded ?: return
        _uiState.value = current.copy(student = current.student.copy(weightKg = value))
    }

    fun updateTest(normId: String, value: String) {
        val current = _uiState.value as? EditProfileUiState.Loaded ?: return
        _uiState.value = current.copy(testValues = current.testValues + (normId to value))
    }

    fun save(onSuccess: () -> Unit) {
        val current = _uiState.value as? EditProfileUiState.Loaded ?: return
        viewModelScope.launch {
            _isSaving.value = true
            val tests = studentPhysicalTestNorms.map { norm ->
                PhysicalTest(
                    id = "${studentId}_${norm.id}",
                    userId = studentId,
                    normId = norm.id,
                    value = current.testValues[norm.id] ?: "",
                    unit = norm.unit
                )
            }
            val studentSaved = repository.updateStudent(current.student).isSuccess
            val testsSaved = repository.updatePhysicalTests(studentId, tests).isSuccess
            _isSaving.value = false
            if (studentSaved && testsSaved) onSuccess()
        }
    }
}
