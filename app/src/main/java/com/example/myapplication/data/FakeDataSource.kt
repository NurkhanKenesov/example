package com.example.myapplication.data

import com.example.myapplication.*
import com.example.myapplication.data.models.*
import kotlinx.coroutines.delay

class FakeDataSource(
    private val authManager: LocalAuthManager,
    private val profileRepo: UserProfileRepository,
    private val planRepo: PlanRepository,
    private val quizRepo: QuizScoreRepository,
    private val injuryRepo: InjuryRepository,
    private val studentRepo: StudentRepository,
    private val attendanceRepo: AttendanceRepository,
    private val normRepo: NormRepository,
    private val ratingRepo: RatingRepository,
    private val achievementRepo: AchievementRepository,
    private val exerciseFeedbackRepo: ExerciseFeedbackRepository,
    private val planFeedbackRepo: PlanFeedbackRepository,
    private val activityRepo: ActivityRepository,
    private val configRepo: ConfigRepository
) : DataSource {

    // ── Auth ──────────────────────────────────────────────────────────────────

    override suspend fun login(email: String, password: String): Result<LocalUser> =
        authManager.login(email, password)

    override suspend fun register(email: String, password: String, name: String, role: String): Result<LocalUser> =
        profileRepo.register(email, password, name, role)

    override suspend fun logout(): Result<Unit> =
        profileRepo.logout()

    override suspend fun forgotPassword(email: String): Result<Unit> =
        Result.success(Unit).also { delay(300) }

    override suspend fun refreshToken(): Result<String> =
        Result.success("mock_jwt_token_${System.currentTimeMillis()}")

    // ── Profile ───────────────────────────────────────────────────────────────

    override suspend fun getCurrentProfile(): Result<UserProfile?> =
        profileRepo.getProfile()

    override suspend fun getProfile(userId: String): Result<UserProfile?> =
        profileRepo.getProfile()

    override suspend fun updateProfile(profile: UserProfile): Result<Unit> =
        profileRepo.saveProfile(profile)

    override suspend fun getMuscleGroups(): Result<List<MuscleGroup>> {
        return Result.success(MockData.muscleRecoveryGroups)
    }

    override suspend fun getProfileStats(): Result<DashboardStats> =
        Result.success(MockData.dashboardStats)

    // ── Students ──────────────────────────────────────────────────────────────

    override suspend fun getStudents(): Result<List<Student>> =
        studentRepo.getStudents()

    override suspend fun getStudent(id: String): Result<Student?> {
        return studentRepo.getStudents().map { list ->
            list.find { it.id == id }
        }
    }

    override suspend fun updateStudent(student: Student): Result<Unit> {
        return Result.success(Unit).also { delay(200) }
    }

    override suspend fun exportStudents(): Result<String> {
        val students = studentRepo.getStudents().getOrNull() ?: emptyList()
        val csv = buildString {
            appendLine("id,name,gender,age,group,score,hasAlert")
            students.forEach {
                appendLine("${it.id},${it.name},${it.gender},${it.age},${it.groupName},${it.score},${it.hasAlert}")
            }
        }
        return Result.success(csv)
    }

    // ── Plans ─────────────────────────────────────────────────────────────────

    override suspend fun getPlans(): Result<List<Plan>> =
        planRepo.getPlans()

    override suspend fun generatePlan(goal: String, medicalGroup: String): Result<WorkoutPlan> {
        return planRepo.getWorkoutPlans().map { plans ->
            plans.find { it.targetMedicalGroup.name == medicalGroup.uppercase() }
                ?: plans.first()
        }
    }

    override suspend fun getPlan(id: String): Result<WorkoutPlan?> =
        planRepo.getWorkoutPlan(id)

    override suspend fun getPlanExercises(planId: String): Result<List<WorkoutExercise>> {
        return planRepo.getWorkoutPlan(planId).map { it.exercises }
    }

    override suspend fun submitPlanFeedback(planId: String, rating: Int, comment: String): Result<Unit> {
        val currentUser = authManager.getCurrentUser()
        val feedback = PlanFeedback(
            planId = planId,
            userId = currentUser?.uid ?: "",
            rating = rating,
            comment = comment,
            timestamp = System.currentTimeMillis()
        )
        return planFeedbackRepo.saveFeedback(feedback)
    }

    // ── Exercises ─────────────────────────────────────────────────────────────

    override suspend fun getExercises(): Result<List<Exercise>> {
        return Result.success(MockData.exercises)
    }

    override suspend fun submitExerciseFeedback(exerciseId: String, rating: Int, comment: String): Result<Unit> {
        val currentUser = authManager.getCurrentUser()
        val feedback = ExerciseFeedback(
            exerciseId = exerciseId,
            userId = currentUser?.uid ?: "",
            rating = rating,
            comment = comment,
            timestamp = System.currentTimeMillis()
        )
        return exerciseFeedbackRepo.saveFeedback(feedback)
    }

    // ── AI ────────────────────────────────────────────────────────────────────

    override suspend fun sendChatMessage(message: String): Result<String> {
        delay(600)
        return Result.success("Это ответ ИИ на: $message")
    }

    override suspend fun explainPlan(planId: String): Result<String> {
        delay(800)
        return Result.success("Объяснение плана $planId готово.")
    }

    override suspend fun getRecommendations(): Result<String> {
        delay(500)
        return Result.success("Рекомендация: попробуйте добавить кардио.")
    }

    // ── Activity ──────────────────────────────────────────────────────────────

    override suspend fun saveDailyActivity(activity: DailyActivity): Result<Unit> =
        activityRepo.saveActivity(activity)

    override suspend fun getActivityHistory(days: Int): Result<List<DailyActivity>> {
        val currentUser = authManager.getCurrentUser()?.uid ?: ""
        return activityRepo.getHistory(currentUser, days)
    }

    // ── Health / Trauma ────────────────────────────────────────────────────────

    override suspend fun getTraumas(): Result<List<Injury>> =
        injuryRepo.getInjuries()

    override suspend fun saveTrauma(injury: Injury): Result<Unit> =
        injuryRepo.saveInjury(injury)

    // ── Attendance ────────────────────────────────────────────────────────────

    override suspend fun checkIn(qrCode: String): Result<AttendanceRecord> =
        attendanceRepo.checkIn(qrCode)

    override suspend fun getAttendanceJournal(): Result<List<AttendanceRecord>> =
        attendanceRepo.getJournal()

    // ── Norms & Tests ─────────────────────────────────────────────────────────

    override suspend fun getNorms(): Result<List<Norm>> =
        normRepo.getNorms()

    override suspend fun getTests(): Result<List<PhysicalTest>> {
        val currentUser = authManager.getCurrentUser()?.uid ?: ""
        return normRepo.getTestResults(currentUser)
    }

    override suspend fun saveTestResult(test: PhysicalTest): Result<Unit> =
        normRepo.saveTestResult(test)

    // ── Rating ────────────────────────────────────────────────────────────────

    override suspend fun getLeaderboard(): Result<List<LeaderboardEntry>> =
        ratingRepo.getLeaderboard()

    // ── Achievements ──────────────────────────────────────────────────────────

    override suspend fun getAchievements(): Result<List<Achievement>> =
        achievementRepo.getAchievements()

    override suspend fun unlockAchievement(id: String): Result<Unit> =
        achievementRepo.unlockAchievement(id)

    // ── Config ────────────────────────────────────────────────────────────────

    override suspend fun getRoles(): Result<List<RoleConfig>> =
        configRepo.getRoles()

    override suspend fun getMuscleGroupConfigs(): Result<List<MuscleGroupConfig>> =
        configRepo.getMuscleGroups()

    override suspend fun getExerciseTypeConfigs(): Result<List<ExerciseTypeConfig>> =
        configRepo.getExerciseTypes()

    // ── Teacher / Model ───────────────────────────────────────────────────────

    override suspend fun getTeacherModel(): Result<String> {
        delay(400)
        return Result.success("{\"version\":\"1.0\",\"trainedAt\":\"2026-07-01\"}")
    }

    override suspend fun retrainModel(): Result<Unit> {
        delay(1200)
        return Result.success(Unit)
    }

    override suspend fun forceRetrainModel(): Result<Unit> {
        delay(1500)
        return Result.success(Unit)
    }
}
