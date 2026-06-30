package com.example.myapplication

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

interface StudentRepository {
    suspend fun getStudents(): Result<List<Student>>
}

class StudentRepositoryImpl(
    private val db: FirebaseFirestore
) : StudentRepository {

    override suspend fun getStudents(): Result<List<Student>> = try {
        val snapshot = db.collection("users")
            .whereEqualTo("role", "student")
            .get()
            .await()
        val students = snapshot.documents.map {
            Student.fromMap(it.id, it.data ?: emptyMap())
        }
        Result.success(students)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }
}
