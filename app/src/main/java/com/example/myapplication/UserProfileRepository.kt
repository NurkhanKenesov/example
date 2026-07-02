package com.example.myapplication

import com.example.myapplication.data.LocalUser

interface UserProfileRepository {
    suspend fun getProfile(): Result<UserProfile?>
    suspend fun saveProfile(profile: UserProfile): Result<Unit>
    suspend fun updateField(key: String, value: Any): Result<Unit>

    suspend fun register(email: String, password: String, name: String, role: String): Result<LocalUser>
    suspend fun login(email: String, password: String): Result<LocalUser>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): LocalUser?
}
