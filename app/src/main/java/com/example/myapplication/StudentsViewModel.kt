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
        fun fromMap(id: String, map: Map<String, Any?>): Student {
            val name = map["name"] as? String ?: ""
            val initials = name.split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
                .joinToString("")
            val ageRaw = map["age"]
            val age = when (ageRaw) {
                is Long -> ageRaw.toString()
                is Double -> ageRaw.toInt().toString()
                is String -> ageRaw
                else -> "--"
            }
            return Student(
                id = id,
                initials = initials,
                name = name,
                gender = map["gender"] as? String ?: "--",
                age = age,
                groupName = map["groupName"] as? String ?: map["healthGroup"] as? String ?: "basic",
                score = (map["score"] as? Double)?.let { "%.1f/4".format(it) } ?: "--",
                hasAlert = map["hasAlert"] as? Boolean ?: false
            )
        }
    }
}

sealed interface StudentsUiState {
    object Loading : StudentsUiState
    data class Loaded(
        val students: List<Student>,
        val total: Int,
        val attendancePercent: Int = 0,
        val groupLabel: String = ""
    ) : StudentsUiState
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
                    val attended = students.count { !it.hasAlert }
                    val pct = if (students.isNotEmpty()) (attended * 100 / students.size) else 0
                    _uiState.value = StudentsUiState.Loaded(
                        students = students,
                        total = students.size,
                        attendancePercent = pct,
                        groupLabel = "Подгруппа физ. образования"
                    )
                },
                onFailure = { e ->
                    _uiState.value = StudentsUiState.Error(e.message ?: "Ошибка загрузки студентов")
                }
            )
        }
    }
}
