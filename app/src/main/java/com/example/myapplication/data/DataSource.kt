package com.example.myapplication.data

import com.example.myapplication.*
import com.example.myapplication.data.models.*
import kotlinx.coroutines.flow.Flow

interface DataSource {
    // Auth
    suspend fun login(email: String, password: String): Result<LocalUser>
    suspend fun register(email: String, password: String, name: String, role: String): Result<LocalUser>
    suspend fun logout(): Result<Unit>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun refreshToken(): Result<String>

    // Profile
    suspend fun getCurrentProfile(): Result<UserProfile?>
    suspend fun getProfile(userId: String): Result<UserProfile?>
    suspend fun updateProfile(profile: UserProfile): Result<Unit>
    suspend fun getMuscleGroups(): Result<List<MuscleGroup>>
    suspend fun getProfileStats(): Result<DashboardStats>

    // Students (teacher)
    suspend fun getStudents(): Result<List<Student>>
    suspend fun getStudent(id: String): Result<Student?>
    suspend fun updateStudent(student: Student): Result<Unit>
    suspend fun exportStudents(): Result<String>

    // Plans
    suspend fun getPlans(): Result<List<Plan>>
    suspend fun generatePlan(goal: String, medicalGroup: String): Result<WorkoutPlan>
    suspend fun getPlan(id: String): Result<WorkoutPlan?>
    suspend fun getPlanExercises(planId: String): Result<List<WorkoutExercise>>
    suspend fun submitPlanFeedback(planId: String, rating: Int, comment: String): Result<Unit>

    // Exercises
    suspend fun getExercises(): Result<List<Exercise>>
    suspend fun submitExerciseFeedback(exerciseId: String, rating: Int, comment: String): Result<Unit>

    // AI
    suspend fun sendChatMessage(message: String): Result<String>
    suspend fun explainPlan(planId: String): Result<String>
    suspend fun getRecommendations(): Result<String>

    // Activity
    suspend fun saveDailyActivity(activity: DailyActivity): Result<Unit>
    suspend fun getActivityHistory(days: Int): Result<List<DailyActivity>>

    // Health / Trauma
    suspend fun getTraumas(): Result<List<Injury>>
    suspend fun saveTrauma(injury: Injury): Result<Unit>

    // Attendance
    suspend fun checkIn(qrCode: String): Result<AttendanceRecord>
    suspend fun getAttendanceJournal(): Result<List<AttendanceRecord>>

    // Norms & Tests
    suspend fun getNorms(): Result<List<Norm>>
    suspend fun getTests(): Result<List<PhysicalTest>>
    suspend fun saveTestResult(test: PhysicalTest): Result<Unit>

    // Rating
    suspend fun getLeaderboard(): Result<List<LeaderboardEntry>>

    // Achievements
    suspend fun getAchievements(): Result<List<Achievement>>
    suspend fun unlockAchievement(id: String): Result<Unit>

    // Config
    suspend fun getRoles(): Result<List<RoleConfig>>
    suspend fun getMuscleGroupConfigs(): Result<List<MuscleGroupConfig>>
    suspend fun getExerciseTypeConfigs(): Result<List<ExerciseTypeConfig>>

    // Teacher / Model
    suspend fun getTeacherModel(): Result<String>
    suspend fun retrainModel(): Result<Unit>
    suspend fun forceRetrainModel(): Result<Unit>
}
