package com.example.myapplication.data.models

import org.json.JSONObject

data class Achievement(
    val emoji: String,
    val name: String,
    val description: String,
    val unlocked: Boolean = true,
    val id: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "emoji" to emoji,
        "name" to name,
        "description" to description,
        "unlocked" to unlocked
    )
}
