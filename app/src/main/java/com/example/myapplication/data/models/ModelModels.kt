package com.example.myapplication.data.models

enum class RetrainStatus { REPLACED, SKIPPED }

data class RetrainHistoryEntry(
    val id: String,
    val status: RetrainStatus,
    val date: String,
    val aucBefore: Double,
    val aucAfter: Double
)

data class ModelInfo(
    val fileName: String = "",
    val algorithm: String = "",
    val isActive: Boolean = false,
    val retrainCount: Int = 0,
    val newDataCount: Int = 0,
    val threshold: Int = 0,
    val history: List<RetrainHistoryEntry> = emptyList()
)
