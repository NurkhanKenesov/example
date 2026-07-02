package com.example.myapplication.data.models

import com.example.myapplication.Gender
import org.json.JSONObject

data class StandardEntry(
    val age: Int = 0,
    val excellent: String = "",
    val good: String = "",
    val satisfactory: String = "",
    val pass: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "age" to age,
        "excellent" to excellent,
        "good" to good,
        "satisfactory" to satisfactory,
        "pass" to pass
    )
}

data class Norm(
    val id: String = "",
    val name: String = "",
    val category: NormCategory = NormCategory.STRENGTH,
    val unit: String = "",
    val gender: Gender = Gender.MALE,
    val minAge: Int = 0,
    val maxAge: Int = 100,
    val standards: List<StandardEntry> = emptyList()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "category" to category.name,
        "unit" to unit,
        "gender" to gender.name,
        "minAge" to minAge,
        "maxAge" to maxAge,
        "standards" to standards.map { it.toMap() }
    )
}

data class PhysicalTest(
    val id: String = "",
    val userId: String = "",
    val normId: String = "",
    val value: String = "",
    val unit: String = "",
    val date: Long = 0L,
    val score: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "normId" to normId,
        "value" to value,
        "unit" to unit,
        "date" to date,
        "score" to score
    )
}

enum class NormCategory(val displayName: String, val emoji: String) {
    STRENGTH("Сила", "💪"),
    ENDURANCE("Выносливость", "🏃"),
    FLEXIBILITY("Гибкость", "🧘"),
    SPEED("Скорость", "⚡"),
    COORDINATION("Координация", "🎯")
}
