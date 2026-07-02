package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.*
import com.example.myapplication.data.LocalAuthManager
import com.example.myapplication.data.LocalUser
import com.example.myapplication.data.models.ModelInfo
import com.example.myapplication.data.models.PhysicalTest
import com.example.myapplication.data.models.RetrainHistoryEntry
import com.example.myapplication.data.models.RetrainStatus
import com.example.myapplication.data.models.StudentTestNorm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

val Context.profileDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "profile_store")
val Context.plansDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "plans_store")
val Context.quizDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "quiz_store")
val Context.injuriesDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "injuries_store")
val Context.studentsDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "students_store")

class LocalUserProfileRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : UserProfileRepository {

    private val profileKey = stringPreferencesKey("current_profile")

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            loadProfileFromStore()
        }
    }

    private suspend fun loadProfileFromStore() {
        val prefs = context.profileDataStore.data.first()
        val json = prefs[profileKey]
        if (json != null) {
            try {
                val obj = JSONObject(json)
                _profile.value = UserProfile(
                    uid = obj.getString("uid"),
                    name = obj.getString("name"),
                    email = obj.getString("email"),
                    role = obj.optString("role", "Student"),
                    gender = Gender.valueOf(obj.optString("gender", "MALE")),
                    age = obj.optInt("age", 18),
                    heightCm = obj.optInt("heightCm", 170),
                    weightKg = obj.optDouble("weightKg", 65.0).toFloat(),
                    medicalGroup = MedicalGroup.valueOf(obj.optString("medicalGroup", "BASIC")),
                    studentId = obj.getString("studentId"),
                    groupName = obj.getString("groupName"),
                    profileComplete = obj.optBoolean("profileComplete", false),
                    photoUrl = obj.optString("photoUrl").takeIf { it.isNotEmpty() }
                )
                android.util.Log.d("LocalUserProfileRepo", "Profile loaded from store: uid=${_profile.value?.uid}, role=${_profile.value?.role}")
            } catch (e: Exception) {
                android.util.Log.e("LocalUserProfileRepo", "Failed to parse profile from store: ${e.message}")
                _profile.value = null
            }
        } else {
            android.util.Log.d("LocalUserProfileRepo", "No profile in store")
            _profile.value = null
        }
    }

    private suspend fun saveProfileToStore(profile: UserProfile) {
        val obj = JSONObject().apply {
            put("uid", profile.uid)
            put("name", profile.name)
            put("email", profile.email)
            put("role", profile.role)
            put("gender", profile.gender.name)
            put("age", profile.age)
            put("heightCm", profile.heightCm)
            put("weightKg", profile.weightKg.toDouble())
            put("medicalGroup", profile.medicalGroup.name)
            put("studentId", profile.studentId)
            put("groupName", profile.groupName)
            put("profileComplete", profile.profileComplete)
            put("photoUrl", profile.photoUrl ?: "")
        }
        context.profileDataStore.edit { it[profileKey] = obj.toString() }
    }

    override suspend fun getProfile(): Result<UserProfile?> = try {
        if (_profile.value == null) {
            loadProfileFromStore()
        }
        android.util.Log.d("LocalUserProfileRepo", "getProfile: uid=${_profile.value?.uid}, role=${_profile.value?.role}")
        Result.success(_profile.value)
    } catch (e: Exception) {
        android.util.Log.e("LocalUserProfileRepo", "getProfile failed: ${e.message}")
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun saveProfile(profile: UserProfile): Result<Unit> = try {
        val uid = localAuthManager.getCurrentUser()?.uid ?: ""
        val existingProfile = _profile.value
        val finalProfile = profile.copy(
            uid = uid,
            email = localAuthManager.getCurrentUser()?.email ?: profile.email,
            role = existingProfile?.role ?: profile.role,
            profileComplete = true
        )
        _profile.value = finalProfile
        saveProfileToStore(finalProfile)
        android.util.Log.d("LocalUserProfileRepo", "Profile saved: uid=$uid, role=${finalProfile.role}, complete=${finalProfile.profileComplete}")
        Result.success(Unit)
    } catch (e: Exception) {
        android.util.Log.e("LocalUserProfileRepo", "Save profile failed: ${e.message}")
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun updateField(key: String, value: Any): Result<Unit> = try {
        val current = _profile.value ?: UserProfile()
        val updated = when (key) {
            "name" -> current.copy(name = value as String)
            "email" -> current.copy(email = value as String)
            "role" -> current.copy(role = value as String)
            "gender" -> current.copy(gender = Gender.valueOf(value as String))
            "age" -> current.copy(age = (value as Number).toInt())
            "heightCm" -> current.copy(heightCm = (value as Number).toInt())
            "weightKg" -> current.copy(weightKg = (value as Number).toFloat())
            "medicalGroup" -> current.copy(medicalGroup = MedicalGroup.valueOf(value as String))
            "studentId" -> current.copy(studentId = value as String)
            "groupName" -> current.copy(groupName = value as String)
            "profileComplete" -> current.copy(profileComplete = value as Boolean)
            "photoUrl" -> current.copy(photoUrl = value as String?)
            else -> current
        }
        _profile.value = updated
        saveProfileToStore(updated)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun register(email: String, password: String, name: String, role: String): Result<LocalUser> {
        val trimmedEmail = email.trim()
        val trimmedName = name.trim()

        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(Exception("Введите корректный email"))
        }
        if (trimmedName.isBlank()) {
            return Result.failure(Exception("Введите ваше имя"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Пароль должен быть не менее 6 символов"))
        }

        return localAuthManager.register(trimmedEmail, password, role).fold(
            onSuccess = { user ->
                val profile = UserProfile(
                    uid = user.uid,
                    name = trimmedName,
                    email = user.email,
                    role = role,
                    profileComplete = false
                )
                _profile.value = profile
                saveProfileToStore(profile)
                android.util.Log.d("LocalUserProfileRepo", "Registered user: uid=${user.uid}, email=${user.email}, role=$role")
                Result.success(user)
            },
            onFailure = { e ->
                android.util.Log.e("LocalUserProfileRepo", "Registration failed: ${e.message}")
                when {
                    e.message?.contains("уже зарегистрирован", ignoreCase = true) == true ->
                        Result.failure(Exception("Этот email уже занят"))
                    e.message?.contains("короткий", ignoreCase = true) == true ->
                        Result.failure(Exception("Слабый пароль"))
                    else -> Result.failure(Exception("Ошибка регистрации: ${e.message}"))
                }
            }
        )
    }

    override suspend fun login(email: String, password: String): Result<LocalUser> {
        return localAuthManager.login(email, password).map { user ->
            _profile.value = null
            android.util.Log.d("LocalUserProfileRepo", "Login: uid=${user.uid}, email=${user.email}, role=${user.role}")
            user
        }
    }

    override suspend fun logout(): Result<Unit> = try {
        localAuthManager.logout()
        _profile.value = null
        context.profileDataStore.edit { it.remove(profileKey) }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getCurrentUser(): LocalUser? {
        return localAuthManager.getCurrentUser()
    }
}

class LocalPlanRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : PlanRepository {

    private val plansKey = stringPreferencesKey("plans")
    private val workoutPlansKey = stringPreferencesKey("workout_plans")
    private val userWorkoutPlanKey = stringPreferencesKey("user_workout_plan")

    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    val plans: StateFlow<List<Plan>> = _plans.asStateFlow()

    private val _workoutPlans = MutableStateFlow<List<WorkoutPlan>>(emptyList())
    val workoutPlans: StateFlow<List<WorkoutPlan>> = _workoutPlans.asStateFlow()

    private val _userWorkoutPlan = MutableStateFlow<WorkoutPlan?>(null)
    val userWorkoutPlan: StateFlow<WorkoutPlan?> = _userWorkoutPlan.asStateFlow()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            loadFromStore()
            if (_workoutPlans.value.isEmpty()) {
                seedWorkoutPlans()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.plansDataStore.data.first()
        _plans.value = parsePlanList(prefs[plansKey])
        _workoutPlans.value = parseWorkoutPlanList(prefs[workoutPlansKey])
        _userWorkoutPlan.value = parseWorkoutPlan(prefs[userWorkoutPlanKey])
    }

    private suspend fun savePlansToStore() {
        context.plansDataStore.edit { prefs ->
            prefs[plansKey] = JSONArray(_plans.value.map { it.toMap() }.map { JSONObject(it).toString() }).toString()
        }
    }

    private suspend fun saveWorkoutPlansToStore() {
        context.plansDataStore.edit { prefs ->
            prefs[workoutPlansKey] = JSONArray(_workoutPlans.value.map { it.toMap() }.map { JSONObject(it).toString() }).toString()
        }
    }

    private suspend fun saveUserWorkoutPlanToStore() {
        context.plansDataStore.edit { prefs ->
            prefs[userWorkoutPlanKey] = _userWorkoutPlan.value?.let { JSONObject(it.toMap()).toString() } ?: ""
        }
    }

    private fun parsePlanList(json: String?): List<Plan> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Plan(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    description = obj.getString("description"),
                    date = obj.optString("date").takeIf { it.isNotEmpty() }?.let { java.util.Date(it.toLong()) },
                    status = obj.optString("status", "pending")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseWorkoutPlanList(json: String?): List<WorkoutPlan> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                val exercisesArray = obj.optJSONArray("exercises") ?: JSONArray()
                val exercises = (0 until exercisesArray.length()).map { j ->
                    val ex = exercisesArray.getJSONObject(j)
                    WorkoutExercise(
                        id = ex.optString("id", "ex_$j"),
                        name = ex.optString("name", ""),
                        description = ex.optString("description", ""),
                        sets = ex.optInt("sets", 0),
                        reps = ex.optInt("reps", 0),
                        emoji = ex.optString("emoji", "🏃‍♂️"),
                        videoUrl = ex.optString("videoUrl").takeIf { it.isNotEmpty() }
                    )
                }
                WorkoutPlan(
                    id = obj.optString("id"),
                    title = obj.optString("title", ""),
                    description = obj.optString("description", ""),
                    targetMedicalGroup = MedicalGroup.valueOf(obj.optString("targetMedicalGroup", "BASIC")),
                    exercises = exercises
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseWorkoutPlan(json: String?): WorkoutPlan? {
        if (json == null) return null
        return try {
            val obj = JSONObject(json)
            val exercisesArray = obj.optJSONArray("exercises") ?: JSONArray()
            val exercises = (0 until exercisesArray.length()).map { j ->
                val ex = exercisesArray.getJSONObject(j)
                WorkoutExercise(
                    id = ex.optString("id", "ex_$j"),
                    name = ex.optString("name", ""),
                    description = ex.optString("description", ""),
                    sets = ex.optInt("sets", 0),
                    reps = ex.optInt("reps", 0),
                    emoji = ex.optString("emoji", "🏃‍♂️"),
                    videoUrl = ex.optString("videoUrl").takeIf { it.isNotEmpty() }
                )
            }
            WorkoutPlan(
                id = obj.optString("id"),
                title = obj.optString("title", ""),
                description = obj.optString("description", ""),
                targetMedicalGroup = MedicalGroup.valueOf(obj.optString("targetMedicalGroup", "BASIC")),
                exercises = exercises
            )
        } catch (e: Exception) { null }
    }

    override suspend fun savePlan(plan: Plan): Result<Unit> = try {
        val current = _plans.value.toMutableList()
        val newPlan = if (plan.id.isBlank()) plan.copy(id = generateId()) else plan
        current.add(newPlan)
        _plans.value = current
        savePlansToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getPlans(): Result<List<Plan>> = try {
        Result.success(_plans.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun updatePlanStatus(planId: String, status: String): Result<Unit> = try {
        val current = _plans.value.map {
            if (it.id == planId) it.copy(status = status) else it
        }
        _plans.value = current
        savePlansToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getWorkoutPlans(): Result<List<WorkoutPlan>> = try {
        Result.success(_workoutPlans.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getWorkoutPlan(planId: String): Result<WorkoutPlan> = try {
        val plan = _workoutPlans.value.find { it.id == planId }
        if (plan != null) {
            Result.success(plan)
        } else {
            Result.failure(DataError.NotFound("План не найден"))
        }
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun saveUserWorkoutPlan(plan: WorkoutPlan): Result<Unit> = try {
        _userWorkoutPlan.value = plan
        saveUserWorkoutPlanToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getUserWorkoutPlan(): Result<WorkoutPlan?> = try {
        Result.success(_userWorkoutPlan.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getUserMedicalGroup(): Result<MedicalGroup> = try {
        Result.success(MedicalGroup.BASIC)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun seedWorkoutPlans(): Result<Unit> = try {
        val basicPlan = WorkoutPlan(
            id = "basic_plan_01",
            title = "Базовая тренировка",
            description = "Упражнения для основной медицинской группы",
            targetMedicalGroup = MedicalGroup.BASIC,
            exercises = listOf(
                WorkoutExercise(id = "ex_1", name = "Ходьба на месте", description = "Легкая разминка", sets = 1, reps = 60),
                WorkoutExercise(id = "ex_2", name = "Приседания", description = "Без веса, медленно", sets = 2, reps = 12),
                WorkoutExercise(id = "ex_3", name = "Отжимания от стены", description = "Для начала", sets = 2, reps = 10),
                WorkoutExercise(id = "ex_4", name = "Планка", description = "На коленях, на дыша", sets = 2, reps = 30)
            )
        )
        val preparatoryPlan = WorkoutPlan(
            id = "preparatory_plan_01",
            title = "Подготовительная тренировка",
            description = "Умеренная физическая нагрузка",
            targetMedicalGroup = MedicalGroup.PREPARATORY,
            exercises = listOf(
                WorkoutExercise(id = "ex_1", name = "Быстрая ходьба", description = "На месте 3 минуты", sets = 1, reps = 180),
                WorkoutExercise(id = "ex_2", name = "Приседания", description = "С приседанием до пар", sets = 3, reps = 15),
                WorkoutExercise(id = "ex_3", name = "Отжимания", description = "От колен, медленно", sets = 3, reps = 12),
                WorkoutExercise(id = "ex_4", name = "Планка", description = "На лодыжках", sets = 3, reps = 45),
                WorkoutExercise(id = "ex_5", name = "Махи руками", description = "Для плеч", sets = 2, reps = 20)
            )
        )
        val specialPlan = WorkoutPlan(
            id = "special_plan_01",
            title = "Спецгруппа - Интенсив",
            description = "Для СМГ медицинской группы",
            targetMedicalGroup = MedicalGroup.SPECIAL,
            exercises = listOf(
                WorkoutExercise(id = "ex_1", name = "Бег на месте", description = "Интенсивно", sets = 2, reps = 120),
                WorkoutExercise(id = "ex_2", name = "Приседания с прыжком", description = "Высокое подъём", sets = 3, reps = 20),
                WorkoutExercise(id = "ex_3", name = "Отжимания от пола", description = "Классические", sets = 3, reps = 15),
                WorkoutExercise(id = "ex_4", name = "Планка", description = "На руках", sets = 3, reps = 60),
                WorkoutExercise(id = "ex_5", name = "Берпи", description = "Сокращённая версия", sets = 2, reps = 10)
            )
        )
        _workoutPlans.value = listOf(basicPlan, preparatoryPlan, specialPlan)
        saveWorkoutPlansToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun getUserId(): String =
        localAuthManager.getCurrentUser()?.uid ?: ""

    private fun generateId(): String = System.currentTimeMillis().toString()
}

class LocalQuizRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : QuizScoreRepository {

    private val scoresKey = stringPreferencesKey("quiz_scores")

    private val _scores = MutableStateFlow<List<QuizScore>>(emptyList())
    val scores: StateFlow<List<QuizScore>> = _scores.asStateFlow()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            loadFromStore()
            if (_scores.value.isEmpty()) {
                seedMockScores()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.quizDataStore.data.first()
        val json = prefs[scoresKey]
        if (json != null) {
            try {
                val array = JSONArray(json)
                val list = (0 until array.length()).map { i ->
                    val obj = array.getJSONObject(i)
                    QuizScore(
                        id = obj.optString("id", ""),
                        quizId = obj.optString("quizId", "health_basics"),
                        score = obj.optInt("score", 0),
                        totalQuestions = obj.optInt("totalQuestions", 10),
                        timestamp = obj.optString("timestamp").takeIf { it.isNotEmpty() }?.let { java.util.Date(it.toLong()) }
                    )
                }
                _scores.value = list
            } catch (e: Exception) {
                _scores.value = emptyList()
            }
        } else {
            _scores.value = emptyList()
        }
    }

    private suspend fun saveToStore() {
        val array = JSONArray()
        _scores.value.forEach { score ->
            array.put(JSONObject().apply {
                put("id", score.id)
                put("quizId", score.quizId)
                put("score", score.score)
                put("totalQuestions", score.totalQuestions)
                put("timestamp", score.timestamp?.time ?: 0)
            })
        }
        context.quizDataStore.edit { it[scoresKey] = array.toString() }
    }

    override suspend fun saveQuizScore(score: Int, totalQuestions: Int): Result<Unit> = try {
        val current = _scores.value.toMutableList()
        current.add(
            QuizScore(
                id = generateId(),
                quizId = "health_basics",
                score = score,
                totalQuestions = totalQuestions,
                timestamp = java.util.Date()
            )
        )
        _scores.value = current
        saveToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getQuizScores(): Result<List<QuizScore>> = try {
        Result.success(_scores.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockScores() {
        val mockScores = listOf(
            QuizScore(id = "qs_1", quizId = "health_basics", score = 7, totalQuestions = 10, timestamp = java.util.Date(System.currentTimeMillis() - 86400000 * 7)),
            QuizScore(id = "qs_2", quizId = "health_basics", score = 8, totalQuestions = 10, timestamp = java.util.Date(System.currentTimeMillis() - 86400000 * 3)),
            QuizScore(id = "qs_3", quizId = "health_basics", score = 9, totalQuestions = 10, timestamp = java.util.Date())
        )
        _scores.value = mockScores
        saveToStore()
    }

    private fun generateId(): String = System.currentTimeMillis().toString()
}

class LocalInjuryRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : InjuryRepository {

    private val injuriesKey = stringPreferencesKey("injuries")

    private val _injuries = MutableStateFlow<List<Injury>>(emptyList())
    val injuries: StateFlow<List<Injury>> = _injuries.asStateFlow()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            loadFromStore()
            if (_injuries.value.isEmpty()) {
                seedMockInjuries()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.injuriesDataStore.data.first()
        val json = prefs[injuriesKey]
        if (json != null) {
            try {
                val array = JSONArray(json)
                val list = (0 until array.length()).map { i ->
                    val obj = array.getJSONObject(i)
                    Injury(
                        id = obj.optString("id", ""),
                        type = obj.optString("type", ""),
                        bodyPart = obj.optString("bodyPart", ""),
                        startDate = obj.optString("startDate").takeIf { it.isNotEmpty() }?.let { java.util.Date(it.toLong()) },
                        endDate = obj.optString("endDate").takeIf { it.isNotEmpty() }?.let { java.util.Date(it.toLong()) },
                        severity = obj.optString("severity", "medium")
                    )
                }
                _injuries.value = list
            } catch (e: Exception) {
                _injuries.value = emptyList()
            }
        } else {
            _injuries.value = emptyList()
        }
    }

    private suspend fun saveToStore() {
        val array = JSONArray()
        _injuries.value.forEach { injury ->
            array.put(JSONObject().apply {
                put("id", injury.id)
                put("type", injury.type)
                put("bodyPart", injury.bodyPart)
                put("startDate", injury.startDate?.time ?: 0)
                put("endDate", injury.endDate?.time ?: 0)
                put("severity", injury.severity)
            })
        }
        context.injuriesDataStore.edit { it[injuriesKey] = array.toString() }
    }

    override suspend fun saveInjury(injury: Injury): Result<Unit> = try {
        val current = _injuries.value.toMutableList()
        val newInjury = if (injury.id.isBlank()) injury.copy(id = generateId()) else injury
        current.add(newInjury)
        _injuries.value = current
        saveToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getInjuries(): Result<List<Injury>> = try {
        Result.success(_injuries.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun deleteInjury(injuryId: String): Result<Unit> = try {
        _injuries.value = _injuries.value.filter { it.id != injuryId }
        saveToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockInjuries() {
        val now = System.currentTimeMillis()
        val mockInjuries = listOf(
            Injury(id = "inj_1", type = "Спортивная травма", bodyPart = "Голень", startDate = java.util.Date(now - 86400000 * 14), endDate = java.util.Date(now - 86400000 * 7), severity = "medium"),
            Injury(id = "inj_2", type = "Перегрузка", bodyPart = "Спина", startDate = java.util.Date(now - 86400000 * 5), endDate = null, severity = "low")
        )
        _injuries.value = mockInjuries
        saveToStore()
    }

    private fun generateId(): String = System.currentTimeMillis().toString()
}

class LocalModelRepository : ModelRepository {

    private val _modelInfo = MutableStateFlow(com.example.myapplication.data.MockData.modelInfo)

    override suspend fun getModelInfo(): Result<ModelInfo> = try {
        Result.success(_modelInfo.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun retrainModel(force: Boolean): Result<ModelInfo> = try {
        val current = _modelInfo.value
        val newEntry = RetrainHistoryEntry(
            id = System.currentTimeMillis().toString(),
            status = RetrainStatus.REPLACED,
            date = "2026-06-15 12:00",
            aucBefore = current.history.firstOrNull()?.aucAfter ?: 0.847,
            aucAfter = (current.history.firstOrNull()?.aucAfter ?: 0.847) + 0.01
        )
        val updated = current.copy(
            retrainCount = current.retrainCount + 1,
            newDataCount = 0,
            history = listOf(newEntry) + current.history
        )
        _modelInfo.value = updated
        Result.success(updated)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }
}

class LocalStudentRepository(private val context: Context) : StudentRepository {

    private val studentsKey = stringPreferencesKey("students")
    private val testsKey = stringPreferencesKey("student_physical_tests")
    private val injuriesKey = stringPreferencesKey("student_injuries")

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: Flow<List<Student>> = _students.asStateFlow()
    private val _physicalTests = MutableStateFlow<Map<String, List<PhysicalTest>>>(emptyMap())
    private val _injuries = MutableStateFlow<Map<String, List<Injury>>>(emptyMap())

    init {
        GlobalScope.launch(Dispatchers.IO) {
            loadFromStore()
            if (_students.value.isEmpty()) {
                seedMockData()
            }
        }
    }

    private suspend fun ensureLoaded() {
        if (_students.value.isEmpty()) {
            loadFromStore()
            if (_students.value.isEmpty()) {
                seedMockData()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.studentsDataStore.data.first()
        _students.value = parseStudents(prefs[studentsKey])
        _physicalTests.value = parseTestsMap(prefs[testsKey])
        _injuries.value = parseInjuriesMap(prefs[injuriesKey])
    }

    private fun parseStudents(json: String?): List<Student> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Student(
                    id = obj.optString("id", ""),
                    initials = obj.optString("initials", ""),
                    name = obj.optString("name", ""),
                    gender = obj.optString("gender", ""),
                    age = obj.optString("age", ""),
                    groupName = obj.optString("groupName", "basic"),
                    score = obj.optString("score", ""),
                    hasAlert = obj.optBoolean("hasAlert", false),
                    heightCm = obj.optDouble("heightCm", 170.0).toFloat(),
                    weightKg = obj.optDouble("weightKg", 65.0).toFloat(),
                    medicalGroup = MedicalGroup.entries.firstOrNull { it.name == obj.optString("medicalGroup") }
                        ?: MedicalGroup.BASIC
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseTestsMap(json: String?): Map<String, List<PhysicalTest>> {
        if (json == null) return emptyMap()
        return try {
            val obj = JSONObject(json)
            obj.keys().asSequence().associateWith { studentId ->
                val array = obj.getJSONArray(studentId)
                (0 until array.length()).map { i ->
                    val test = array.getJSONObject(i)
                    PhysicalTest(
                        id = test.optString("id", ""),
                        userId = test.optString("userId", studentId),
                        normId = test.optString("normId", ""),
                        value = test.optString("value", ""),
                        unit = test.optString("unit", ""),
                        date = test.optLong("date", 0L),
                        score = test.optString("score", "")
                    )
                }
            }
        } catch (e: Exception) { emptyMap() }
    }

    private fun parseInjuriesMap(json: String?): Map<String, List<Injury>> {
        if (json == null) return emptyMap()
        return try {
            val obj = JSONObject(json)
            obj.keys().asSequence().associateWith { studentId ->
                val array = obj.getJSONArray(studentId)
                (0 until array.length()).map { i ->
                    val injury = array.getJSONObject(i)
                    Injury(
                        id = injury.optString("id", ""),
                        type = injury.optString("type", ""),
                        bodyPart = injury.optString("bodyPart", ""),
                        startDate = injury.optString("startDate").takeIf { it.isNotEmpty() }?.let { java.util.Date(it.toLong()) },
                        endDate = injury.optString("endDate").takeIf { it.isNotEmpty() }?.let { java.util.Date(it.toLong()) },
                        severity = injury.optString("severity", "medium")
                    )
                }
            }
        } catch (e: Exception) { emptyMap() }
    }

    private suspend fun saveStudentsToStore() {
        val array = JSONArray(_students.value.map { student ->
            JSONObject().apply {
                put("id", student.id)
                put("initials", student.initials)
                put("name", student.name)
                put("gender", student.gender)
                put("age", student.age)
                put("groupName", student.groupName)
                put("score", student.score)
                put("hasAlert", student.hasAlert)
                put("heightCm", student.heightCm.toDouble())
                put("weightKg", student.weightKg.toDouble())
                put("medicalGroup", student.medicalGroup.name)
            }.toString()
        }.map { JSONObject(it) })
        context.studentsDataStore.edit { it[studentsKey] = array.toString() }
    }

    private suspend fun saveTestsToStore() {
        val obj = JSONObject()
        _physicalTests.value.forEach { (studentId, tests) ->
            val array = JSONArray()
            tests.forEach { test ->
                array.put(JSONObject().apply {
                    put("id", test.id)
                    put("userId", test.userId)
                    put("normId", test.normId)
                    put("value", test.value)
                    put("unit", test.unit)
                    put("date", test.date)
                    put("score", test.score)
                })
            }
            obj.put(studentId, array)
        }
        context.studentsDataStore.edit { it[testsKey] = obj.toString() }
    }

    private suspend fun saveInjuriesToStore() {
        val obj = JSONObject()
        _injuries.value.forEach { (studentId, injuries) ->
            val array = JSONArray()
            injuries.forEach { injury ->
                array.put(JSONObject().apply {
                    put("id", injury.id)
                    put("type", injury.type)
                    put("bodyPart", injury.bodyPart)
                    put("startDate", injury.startDate?.time ?: 0)
                    put("endDate", injury.endDate?.time ?: 0)
                    put("severity", injury.severity)
                })
            }
            obj.put(studentId, array)
        }
        context.studentsDataStore.edit { it[injuriesKey] = obj.toString() }
    }

    override fun getStudentsStream(): Flow<List<Student>> = students

    override suspend fun getStudents(): Result<List<Student>> = try {
        ensureLoaded()
        Result.success(_students.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getStudent(id: String): Result<Student?> = try {
        ensureLoaded()
        Result.success(_students.value.find { it.id == id })
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun updateStudent(student: Student): Result<Unit> = try {
        ensureLoaded()
        _students.value = _students.value.map { if (it.id == student.id) student else it }
        saveStudentsToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getPhysicalTests(studentId: String): Result<List<PhysicalTest>> = try {
        ensureLoaded()
        Result.success(_physicalTests.value[studentId] ?: emptyList())
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun updatePhysicalTests(studentId: String, tests: List<PhysicalTest>): Result<Unit> = try {
        ensureLoaded()
        _physicalTests.value = _physicalTests.value + (studentId to tests)
        saveTestsToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getInjuries(studentId: String): Result<List<Injury>> = try {
        ensureLoaded()
        Result.success(_injuries.value[studentId] ?: emptyList())
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockData() {
        _students.value = listOf(
            Student(id="1", initials="AS", name="Amir Seitkali",    gender="Male",   age="22", groupName="basic",    score="3.2/4", hasAlert=false, heightCm=178.5f, weightKg=74.2f, medicalGroup=MedicalGroup.BASIC),
            Student(id="2", initials="ZA", name="Zarina Akhmetova", gender="Female", age="20", groupName="prepared", score="2.5/4", hasAlert=true,  heightCm=165f,   weightKg=58f,   medicalGroup=MedicalGroup.PREPARATORY),
            Student(id="3", initials="RN", name="Ruslan Nurlanov",  gender="Male",   age="21", groupName="basic",    score="3.8/4", hasAlert=false, heightCm=180f,   weightKg=75f,   medicalGroup=MedicalGroup.BASIC),
            Student(id="4", initials="DI", name="Dias Issayev",     gender="Male",   age="19", groupName="special",  score="1.8/4", hasAlert=false, heightCm=172f,   weightKg=68f,   medicalGroup=MedicalGroup.SPECIAL),
            Student(id="5", initials="AK", name="Aliya Kasymova",   gender="Female", age="20", groupName="basic",    score="3.5/4", hasAlert=false, heightCm=168f,   weightKg=60f,   medicalGroup=MedicalGroup.BASIC),
            Student(id="6", initials="BM", name="Bekzat Muratov",   gender="Male",   age="22", groupName="prepared", score="2.9/4", hasAlert=true,  heightCm=182f,   weightKg=80f,   medicalGroup=MedicalGroup.PREPARATORY),
            Student(id="7", initials="GT", name="Gulnara Tleubek",  gender="Female", age="21", groupName="basic",    score="4.0/4", hasAlert=false, heightCm=170f,   weightKg=62f,   medicalGroup=MedicalGroup.BASIC),
            Student(id="8", initials="EA", name="Erik Akhanov",     gender="Male",   age="23", groupName="special",  score="2.1/4", hasAlert=false, heightCm=176f,   weightKg=70f,   medicalGroup=MedicalGroup.SPECIAL),
            Student(id="9", initials="MJ", name="Madina Jumabek",   gender="Female", age="20", groupName="basic",    score="3.7/4", hasAlert=false, heightCm=163f,   weightKg=55f,   medicalGroup=MedicalGroup.BASIC),
            Student(id="10", initials="KS", name="Kanat Seilov",   gender="Male",   age="21", groupName="prepared", score="2.3/4", hasAlert=true,  heightCm=179f,   weightKg=77f,   medicalGroup=MedicalGroup.PREPARATORY)
        )
        _physicalTests.value = mapOf(
            "1" to listOf(
                PhysicalTest(id = "1_${StudentTestNorm.COOPER}", userId = "1", normId = StudentTestNorm.COOPER, value = "2650", unit = "м", score = ""),
                PhysicalTest(id = "1_${StudentTestNorm.PUSHUPS}", userId = "1", normId = StudentTestNorm.PUSHUPS, value = "32", unit = "раз", score = ""),
                PhysicalTest(id = "1_${StudentTestNorm.PULLUPS}", userId = "1", normId = StudentTestNorm.PULLUPS, value = "10", unit = "раз", score = ""),
                PhysicalTest(id = "1_${StudentTestNorm.FLEXIBILITY}", userId = "1", normId = StudentTestNorm.FLEXIBILITY, value = "27.0", unit = "см", score = ""),
                PhysicalTest(id = "1_${StudentTestNorm.ABS}", userId = "1", normId = StudentTestNorm.ABS, value = "28", unit = "раз", score = ""),
                PhysicalTest(id = "1_${StudentTestNorm.JUMP}", userId = "1", normId = StudentTestNorm.JUMP, value = "195", unit = "см", score = "")
            )
        )
        _injuries.value = emptyMap()
        saveStudentsToStore()
        saveTestsToStore()
        saveInjuriesToStore()
    }
}
