package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Student(
    val id: String = "",
    val initials: String = "",
    val name: String = "",
    val gender: String = "",
    val age: String = "",
    val groupName: String = "",
    val score: String = "",
    val hasAlert: Boolean = false
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Student = Student(
            id = id,
            name = map["name"] as? String ?: "",
            groupName = map["groupName"] as? String ?: "",
            score = (map["score"] as? Double)?.let { "%.1f/4".format(it) } ?: "--"
        )
    }
}

sealed interface StudentsUiState {
    object Loading : StudentsUiState
    data class Loaded(val students: List<Student>, val total: Int) : StudentsUiState
    data class Error(val message: String) : StudentsUiState
}

class StudentsViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentsUiState>(StudentsUiState.Loading)
    val uiState: StateFlow<StudentsUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = StudentsUiState.Loading
            repository.getStudents().fold(
                onSuccess = { students ->
                    _uiState.value = StudentsUiState.Loaded(students, students.size)
                },
                onFailure = { e ->
                    _uiState.value = StudentsUiState.Error(e.message ?: "Ошибка загрузки студентов")
                }
            )
        }
    }
}
