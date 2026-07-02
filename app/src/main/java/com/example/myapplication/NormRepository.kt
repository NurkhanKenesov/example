package com.example.myapplication

import com.example.myapplication.data.models.Norm
import com.example.myapplication.data.models.PhysicalTest

interface NormRepository {
    suspend fun getNorms(): Result<List<Norm>>
    suspend fun saveTestResult(test: PhysicalTest): Result<Unit>
    suspend fun getTestResults(userId: String): Result<List<PhysicalTest>>
}
