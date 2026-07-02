package com.example.myapplication.data.models

import org.json.JSONObject

data class RoleConfig(
    val id: String = "",
    val name: String = "",
    val permissions: List<String> = emptyList(),
    val description: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "permissions" to permissions,
        "description" to description
    )
}

data class MuscleGroupConfig(
    val id: String = "",
    val name: String = "",
    val emoji: String = "",
    val description: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "emoji" to emoji,
        "description" to description
    )
}

data class ExerciseTypeConfig(
    val id: String = "",
    val name: String = "",
    val emoji: String = "",
    val description: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "emoji" to emoji,
        "description" to description
    )
}
