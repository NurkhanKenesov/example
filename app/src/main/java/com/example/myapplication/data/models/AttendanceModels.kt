package com.example.myapplication.data.models

import org.json.JSONObject

data class AttendanceRecord(
    val id: String = "",
    val userId: String = "",
    val sessionId: String = "",
    val qrCode: String = "",
    val timestamp: Long = 0L,
    val status: AttendanceStatus = AttendanceStatus.ABSENT,
    val createdAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "sessionId" to sessionId,
        "qrCode" to qrCode,
        "timestamp" to timestamp,
        "status" to status.name,
        "createdAt" to createdAt
    )
}

data class AttendanceSession(
    val id: String = "",
    val teacherId: String = "",
    val subject: String = "",
    val qrCode: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val location: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "teacherId" to teacherId,
        "subject" to subject,
        "qrCode" to qrCode,
        "startTime" to startTime,
        "endTime" to endTime,
        "location" to location
    )
}

enum class AttendanceStatus(val displayName: String) {
    CHECKED_IN("Посещено"),
    ABSENT("Отсутствует"),
    LATE("Опоздание")
}
