package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

class StudentsViewModel : ViewModel() {
    private val repository = StudentRepository()
    
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

class StudentRepository {
    private val db = FirebaseFirestore.getInstance()
    
    suspend fun getStudents(): Result<List<Student>> = try {
        val snapshot = db.collection("users")
            .whereEqualTo("role", "student")
            .get()
            .await()
        val students = snapshot.documents.map { 
            Student.fromMap(it.id, it.data ?: emptyMap()) 
        }
        Result.success(students)
    } catch (e: Exception) {
        Result.failure(e)
    }
}