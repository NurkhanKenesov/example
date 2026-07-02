package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.AttendanceRecord
import com.example.myapplication.data.models.AttendanceSession
import com.example.myapplication.data.models.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

sealed interface AttendanceUiState {
    object Loading : AttendanceUiState
    data class Loaded(
        val sessions: List<AttendanceSession>,
        val journal: List<AttendanceRecord>,
        val checkedInCount: Int = 0,
        val totalStudents: Int = 0
    ) : AttendanceUiState
    data class Error(val message: String) : AttendanceUiState
}

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        observeAttendance()
    }

    private fun observeAttendance() {
        viewModelScope.launch {
            combine(
                attendanceRepository.getJournalStream(),
                attendanceRepository.getSessions()
            ) { journal, sessionsResult ->
                sessionsResult.getOrNull() ?: emptyList() to journal
            }.catch { e ->
                _uiState.value = AttendanceUiState.Error(e.message ?: "Unknown error")
            }.collect { (sessions, journal) ->
                val studentsResult = studentRepository.getStudents()
                val students = studentsResult.getOrNull() ?: emptyList()
                
                val currentSession = sessions.find { 
                    Date().time >= it.startTime && Date().time <= it.endTime 
                }
                
                val checkedInForCurrent = if (currentSession != null) {
                    journal.count { it.sessionId == currentSession.id && it.status == AttendanceStatus.CHECKED_IN }
                } else 0
                
                _uiState.value = AttendanceUiState.Loaded(
                    sessions = sessions,
                    journal = journal,
                    checkedInCount = checkedInForCurrent,
                    totalStudents = students.size
                )
            }
        }
    }

    suspend fun checkIn(qrCode: String): Result<AttendanceRecord> {
        return attendanceRepository.checkIn(qrCode)
    }

    fun getCurrentActiveSession(): AttendanceSession? {
        val state = _uiState.value
        if (state !is AttendanceUiState.Loaded) return null
        val now = Date().time
        return state.sessions.find { now >= it.startTime && now <= it.endTime }
    }

    fun getCheckedInCountForSession(sessionId: String): Int {
        val journal = (_uiState.value as? AttendanceUiState.Loaded)?.journal ?: return 0
        return journal.count { it.sessionId == sessionId && it.status == AttendanceStatus.CHECKED_IN }
    }
}