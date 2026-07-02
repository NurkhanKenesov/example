package com.example.myapplication

import java.util.Date

data class Injury(
    val id: String = "",
    val type: String = "",
    val bodyPart: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val severity: String = "medium"
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Injury = Injury(
            id = id,
            type = map["type"] as? String ?: "",
            bodyPart = map["bodyPart"] as? String ?: "",
            startDate = map["startDate"] as? Date,
            endDate = map["endDate"] as? Date,
            severity = map["severity"] as? String ?: "medium"
        )
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "type" to type,
        "bodyPart" to bodyPart,
        "startDate" to startDate,
        "endDate" to endDate,
        "severity" to severity
    )
}

interface InjuryRepository {
    suspend fun saveInjury(injury: Injury): Result<Unit>
    suspend fun getInjuries(): Result<List<Injury>>
    suspend fun deleteInjury(injuryId: String): Result<Unit>
}
