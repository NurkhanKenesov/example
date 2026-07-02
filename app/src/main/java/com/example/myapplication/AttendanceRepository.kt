package com.example.myapplication

import com.example.myapplication.data.models.AttendanceRecord
import com.example.myapplication.data.models.AttendanceSession
import com.example.myapplication.data.models.AttendanceStatus
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun checkIn(qrCode: String): Result<AttendanceRecord>
    suspend fun getJournal(): Result<List<AttendanceRecord>>
    fun getJournalStream(): Flow<List<AttendanceRecord>>
    suspend fun getSessions(): Result<List<AttendanceSession>>
    suspend fun saveSession(session: AttendanceSession): Result<Unit>
}
