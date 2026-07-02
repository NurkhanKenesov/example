package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.*
import com.example.myapplication.data.LocalAuthManager
import com.example.myapplication.data.MockData
import com.example.myapplication.data.models.AttendanceRecord
import com.example.myapplication.data.models.AttendanceSession
import com.example.myapplication.data.models.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

val Context.attendanceDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "attendance_store")

class LocalAttendanceRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : AttendanceRepository {

    private val sessionsKey = stringPreferencesKey("sessions")
    private val journalKey = stringPreferencesKey("journal")

    private val _sessions = MutableStateFlow<List<AttendanceSession>>(emptyList())
    val sessions: StateFlow<List<AttendanceSession>> = _sessions.asStateFlow()

    private val _journal = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val journal: StateFlow<List<AttendanceRecord>> = _journal.asStateFlow()

    init {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadFromStore()
            if (_sessions.value.isEmpty() && _journal.value.isEmpty()) {
                seedMockData()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.attendanceDataStore.data.first()
        _sessions.value = parseSessions(prefs[sessionsKey])
        _journal.value = parseJournal(prefs[journalKey])
    }

    private suspend fun saveSessionsToStore() {
        context.attendanceDataStore.edit { prefs ->
            prefs[sessionsKey] = JSONArray(_sessions.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private suspend fun saveJournalToStore() {
        context.attendanceDataStore.edit { prefs ->
            prefs[journalKey] = JSONArray(_journal.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private fun parseSessions(json: String?): List<AttendanceSession> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                AttendanceSession(
                    id = obj.optString("id", ""),
                    teacherId = obj.optString("teacherId", ""),
                    subject = obj.optString("subject", ""),
                    qrCode = obj.optString("qrCode", ""),
                    startTime = obj.optLong("startTime", 0L),
                    endTime = obj.optLong("endTime", 0L),
                    location = obj.optString("location", "")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseJournal(json: String?): List<AttendanceRecord> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                AttendanceRecord(
                    id = obj.optString("id", ""),
                    userId = obj.optString("userId", ""),
                    sessionId = obj.optString("sessionId", ""),
                    qrCode = obj.optString("qrCode", ""),
                    timestamp = obj.optLong("timestamp", 0L),
                    status = AttendanceStatus.valueOf(obj.optString("status", AttendanceStatus.ABSENT.name)),
                    createdAt = obj.optLong("createdAt", 0L)
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun checkIn(qrCode: String): Result<AttendanceRecord> = try {
        val userId = localAuthManager.getCurrentUser()?.uid ?: return Result.failure(Exception("Not logged in"))
        val session = _sessions.value.find { it.qrCode == qrCode }
            ?: return Result.failure(Exception("Session not found"))
        val now = Date().time
        val record = AttendanceRecord(
            id = now.toString(),
            userId = userId,
            sessionId = session.id,
            qrCode = qrCode,
            timestamp = now,
            status = if (now > session.startTime && now < session.endTime) AttendanceStatus.CHECKED_IN else AttendanceStatus.LATE,
            createdAt = now
        )
        _journal.value = _journal.value + record
        saveJournalToStore()
        Result.success(record)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getJournal(): Result<List<AttendanceRecord>> = try {
        Result.success(_journal.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override fun getJournalStream(): Flow<List<AttendanceRecord>> = _journal.asStateFlow()

    override suspend fun getSessions(): Result<List<AttendanceSession>> = try {
        Result.success(_sessions.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun saveSession(session: AttendanceSession): Result<Unit> = try {
        val current = _sessions.value.toMutableList()
        val newSession = if (session.id.isBlank()) session.copy(id = System.currentTimeMillis().toString()) else session
        current.add(newSession)
        _sessions.value = current
        saveSessionsToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockData() {
        val now = System.currentTimeMillis()
        val dayMs = 86_400_000L
        val hourMs = 3_600_000L

        val mockSessions = listOf(
            AttendanceSession(id = "sess_1", teacherId = "teacher_ivanova", subject = "Физвоспитание", qrCode = "QR-A-20260702-001", startTime = now - 2 * hourMs, endTime = now + hourMs, location = "Спортивный зал №3"),
            AttendanceSession(id = "sess_2", teacherId = "teacher_ivanova", subject = "Физвоспитание", qrCode = "QR-A-20260701-001", startTime = now - dayMs - 2 * hourMs, endTime = now - dayMs + hourMs, location = "Спортивный зал №3"),
            AttendanceSession(id = "sess_3", teacherId = "teacher_ivanova", subject = "Легкая атлетика", qrCode = "QR-A-20260630-001", startTime = now - 2 * dayMs - hourMs, endTime = now - 2 * dayMs + 2 * hourMs, location = "Стадион")
        )

        val mockJournal = listOf(
            AttendanceRecord(id = "att_1", userId = "amir", sessionId = "sess_1", qrCode = "QR-A-20260702-001", timestamp = now - hourMs, status = AttendanceStatus.CHECKED_IN, createdAt = now - hourMs),
            AttendanceRecord(id = "att_2", userId = "zarina", sessionId = "sess_1", qrCode = "QR-A-20260702-001", timestamp = now - hourMs, status = AttendanceStatus.CHECKED_IN, createdAt = now - hourMs),
            AttendanceRecord(id = "att_3", userId = "ruslan", sessionId = "sess_1", qrCode = "QR-A-20260702-001", timestamp = now - hourMs, status = AttendanceStatus.LATE, createdAt = now - hourMs),
            AttendanceRecord(id = "att_4", userId = "dias", sessionId = "sess_1", qrCode = "QR-A-20260702-001", timestamp = 0, status = AttendanceStatus.ABSENT, createdAt = now + hourMs),
            AttendanceRecord(id = "att_5", userId = "amir", sessionId = "sess_2", qrCode = "QR-A-20260701-001", timestamp = now - dayMs - hourMs, status = AttendanceStatus.CHECKED_IN, createdAt = now - dayMs - hourMs)
        )

        _sessions.value = mockSessions
        _journal.value = mockJournal
        saveSessionsToStore()
        saveJournalToStore()
    }
}
