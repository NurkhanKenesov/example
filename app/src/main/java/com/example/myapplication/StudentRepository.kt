package com.example.myapplication

import com.example.myapplication.data.models.PhysicalTest
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getStudentsStream(): Flow<List<Student>>
    suspend fun getStudents(): Result<List<Student>>
    suspend fun getStudent(id: String): Result<Student?>
    suspend fun updateStudent(student: Student): Result<Unit>
    suspend fun getPhysicalTests(studentId: String): Result<List<PhysicalTest>>
    suspend fun updatePhysicalTests(studentId: String, tests: List<PhysicalTest>): Result<Unit>
    suspend fun getInjuries(studentId: String): Result<List<Injury>>
}
