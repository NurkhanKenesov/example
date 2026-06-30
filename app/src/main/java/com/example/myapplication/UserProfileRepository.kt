package com.example.myapplication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

interface UserProfileRepository {
    suspend fun getProfile(): Result<UserProfile?>
    suspend fun saveProfile(profile: UserProfile): Result<Unit>
    suspend fun updateField(key: String, value: Any): Result<Unit>
}

class UserProfileRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : UserProfileRepository {

    private fun getUserId(): String = auth.currentUser?.uid ?: ""

    override suspend fun getProfile(): Result<UserProfile?> = try {
        val uid = getUserId()
        val doc = db.collection("users").document(uid).get().await()
        if (doc.exists()) {
            @Suppress("UNCHECKED_CAST")
            Result.success(UserProfile.fromMap(doc.data as Map<String, Any>))
        } else {
            Result.success(null)
        }
    } catch (e: FirebaseAuthException) {
        Result.failure(DataError.Auth(e))
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun saveProfile(profile: UserProfile): Result<Unit> = try {
        val uid = getUserId()
        val finalProfile = profile.copy(
            uid = uid,
            email = auth.currentUser?.email ?: profile.email,
            profileComplete = true
        )
        db.collection("users")
            .document(uid)
            .set(finalProfile.toMap())
            .await()
        Result.success(Unit)
    } catch (e: FirebaseAuthException) {
        Result.failure(DataError.Auth(e))
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun updateField(key: String, value: Any): Result<Unit> = try {
        val uid = getUserId()
        db.collection("users").document(uid).update(key, value).await()
        Result.success(Unit)
    } catch (e: FirebaseAuthException) {
        Result.failure(DataError.Auth(e))
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataError.Network(e))
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }
}
