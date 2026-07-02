package com.example.myapplication

import com.example.myapplication.data.models.ModelInfo

interface ModelRepository {
    suspend fun getModelInfo(): Result<ModelInfo>
    suspend fun retrainModel(force: Boolean = false): Result<ModelInfo>
}
