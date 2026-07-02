package com.example.myapplication.data

import androidx.compose.ui.graphics.Color
import com.example.myapplication.*
import com.example.myapplication.data.models.*
import java.util.Date
import java.util.UUID

object MockData {

    private val now = System.currentTimeMillis()
    private val dayMs = 86_400_000L
    private val hourMs = 3_600_000L

    private fun uid(seed: String) = UUID.nameUUIDFromBytes(seed.toByteArray()).toString().take(28)
    private fun id(seed: String) = UUID.nameUUIDFromBytes(seed.toByteArray()).toString().take(12)
    private fun date(daysAgo: Long = 0, hoursAgo: Long = 0): Date =
        Date(now - daysAgo * dayMs - hoursAgo * hourMs)

    val users = listOf(
        LocalUser(uid = uid("amir"), email = "amir@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("zarina"), email = "zarina@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("ruslan"), email = "ruslan@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("dias"), email = "dias@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("aliya"), email = "aliya@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("bekzat"), email = "bekzat@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("gulnara"), email = "gulnara@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("erik"), email = "erik@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("madina"), email = "madina@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("kanat"), email = "kanat@university.kz", passwordHash = "", role = "Student"),
        LocalUser(uid = uid("teacher_ivanova"), email = "ivanova@university.kz", passwordHash = "", role = "Teacher"),
        LocalUser(uid = uid("admin"), email = "admin@university.kz", passwordHash = "", role = "Admin")
    )

    val profileAmir = UserProfile(
        uid = uid("amir"),
        name = "Amir Seitkali",
        email = "amir@university.kz",
        role = "Student",
        gender = Gender.MALE,
        age = 22,
        heightCm = 178,
        weightKg = 72f,
        medicalGroup = MedicalGroup.BASIC,
        studentId = "STU-2024-1247",
        groupName = "basic",
        profileComplete = true,
        photoUrl = null
    )

    val allProfiles = listOf(
        profileAmir,
        UserProfile(uid = uid("zarina"), name = "Zarina Akhmetova", email = "zarina@university.kz", role = "Student", gender = Gender.FEMALE, age = 20, heightCm = 165, weightKg = 58f, medicalGroup = MedicalGroup.PREPARATORY, studentId = "STU-2024-1024", groupName = "prepared", profileComplete = true),
        UserProfile(uid = uid("ruslan"), name = "Ruslan Nurlanov", email = "ruslan@university.kz", role = "Student", gender = Gender.MALE, age = 21, heightCm = 180, weightKg = 75f, medicalGroup = MedicalGroup.BASIC, studentId = "STU-2024-1156", groupName = "basic", profileComplete = true),
        UserProfile(uid = uid("dias"), name = "Dias Issayev", email = "dias@university.kz", role = "Student", gender = Gender.MALE, age = 19, heightCm = 172, weightKg = 68f, medicalGroup = MedicalGroup.SPECIAL, studentId = "STU-2024-1089", groupName = "special", profileComplete = true),
        UserProfile(uid = uid("aliya"), name = "Aliya Kasymova", email = "aliya@university.kz", role = "Student", gender = Gender.FEMALE, age = 20, heightCm = 168, weightKg = 60f, medicalGroup = MedicalGroup.BASIC, studentId = "STU-2024-1203", groupName = "basic", profileComplete = true),
        UserProfile(uid = uid("bekzat"), name = "Bekzat Muratov", email = "bekzat@university.kz", role = "Student", gender = Gender.MALE, age = 22, heightCm = 182, weightKg = 80f, medicalGroup = MedicalGroup.PREPARATORY, studentId = "STU-2024-1045", groupName = "prepared", profileComplete = true),
        UserProfile(uid = uid("teacher_ivanova"), name = "Айгуль Иванова", email = "ivanova@university.kz", role = "Teacher", gender = Gender.FEMALE, age = 35, heightCm = 170, weightKg = 65f, medicalGroup = MedicalGroup.BASIC, studentId = "", groupName = "", profileComplete = true),
        UserProfile(uid = uid("admin"), name = "System Admin", email = "admin@university.kz", role = "Admin", gender = Gender.MALE, age = 30, heightCm = 175, weightKg = 70f, medicalGroup = MedicalGroup.BASIC, studentId = "", groupName = "", profileComplete = true)
    )

    val students = listOf(
        Student(id = "1", initials = "AS", name = "Amir Seitkali", gender = "Male", age = "22", groupName = "basic", score = "3.2/4", hasAlert = false),
        Student(id = "2", initials = "ZA", name = "Zarina Akhmetova", gender = "Female", age = "20", groupName = "prepared", score = "2.5/4", hasAlert = true),
        Student(id = "3", initials = "RN", name = "Ruslan Nurlanov", gender = "Male", age = "21", groupName = "basic", score = "3.8/4", hasAlert = false),
        Student(id = "4", initials = "DI", name = "Dias Issayev", gender = "Male", age = "19", groupName = "special", score = "1.8/4", hasAlert = false),
        Student(id = "5", initials = "AK", name = "Aliya Kasymova", gender = "Female", age = "20", groupName = "basic", score = "3.5/4", hasAlert = false),
        Student(id = "6", initials = "BM", name = "Bekzat Muratov", gender = "Male", age = "22", groupName = "prepared", score = "2.9/4", hasAlert = true),
        Student(id = "7", initials = "GT", name = "Gulnara Tleubek", gender = "Female", age = "21", groupName = "basic", score = "4.0/4", hasAlert = false),
        Student(id = "8", initials = "EA", name = "Erik Akhanov", gender = "Male", age = "23", groupName = "special", score = "2.1/4", hasAlert = false),
        Student(id = "9", initials = "MJ", name = "Madina Jumabek", gender = "Female", age = "20", groupName = "basic", score = "3.7/4", hasAlert = false),
        Student(id = "10", initials = "KS", name = "Kanat Seilov", gender = "Male", age = "21", groupName = "prepared", score = "2.3/4", hasAlert = true)
    )

    val plans = listOf(
        Plan(id = id("plan_1247"), title = "План #1247", description = "Базовый план на неделю", date = date(daysAgo = 2), status = "in_progress"),
        Plan(id = id("plan_1248"), title = "План #1248", description = "Подготовительная тренировка", date = date(daysAgo = 5), status = "completed"),
        Plan(id = id("plan_1249"), title = "План #1249", description = "Специальная программа", date = date(daysAgo = 7), status = "scheduled")
    )

    val workoutPlans = listOf(
        WorkoutPlan(
            id = "basic_plan_01",
            title = "Базовая тренировка",
            description = "Упражнения для основной медицинской группы",
            targetMedicalGroup = MedicalGroup.BASIC,
            exercises = listOf(
                WorkoutExercise(id = "ex_1", name = "Ходьба на месте", description = "Легкая разминка", sets = 1, reps = 60, emoji = "🚶"),
                WorkoutExercise(id = "ex_2", name = "Приседания", description = "Без веса, медленно", sets = 2, reps = 12, emoji = "🦵"),
                WorkoutExercise(id = "ex_3", name = "Отжимания от стены", description = "Для начала", sets = 2, reps = 10, emoji = "💪"),
                WorkoutExercise(id = "ex_4", name = "Планка", description = "На коленях, на выдохе", sets = 2, reps = 30, emoji = "🧘")
            )
        ),
        WorkoutPlan(
            id = "preparatory_plan_01",
            title = "Подготовительная тренировка",
            description = "Умеренная физическая нагрузка",
            targetMedicalGroup = MedicalGroup.PREPARATORY,
            exercises = listOf(
                WorkoutExercise(id = "ex_1", name = "Быстрая ходьба", description = "На месте 3 минуты", sets = 1, reps = 180, emoji = "🚶‍♂️"),
                WorkoutExercise(id = "ex_2", name = "Приседания", description = "С касанием ягодицами стула", sets = 3, reps = 15, emoji = "🦵"),
                WorkoutExercise(id = "ex_3", name = "Отжимания", description = "От колен, медленно", sets = 3, reps = 12, emoji = "💪"),
                WorkoutExercise(id = "ex_4", name = "Планка", description = "На лодыжках", sets = 3, reps = 45, emoji = "🧘"),
                WorkoutExercise(id = "ex_5", name = "Махи руками", description = "Для плеч", sets = 2, reps = 20, emoji = "🙆")
            )
        ),
        WorkoutPlan(
            id = "special_plan_01",
            title = "СМГ - Интенсив",
            description = "Для специальной медицинской группы",
            targetMedicalGroup = MedicalGroup.SPECIAL,
            exercises = listOf(
                WorkoutExercise(id = "ex_1", name = "Бег на месте", description = "Интенсивно", sets = 2, reps = 120, emoji = "🏃"),
                WorkoutExercise(id = "ex_2", name = "Приседания с прыжком", description = "Высокое поднятие колен", sets = 3, reps = 20, emoji = "🦵"),
                WorkoutExercise(id = "ex_3", name = "Отжимания от пола", description = "Классические", sets = 3, reps = 15, emoji = "💪"),
                WorkoutExercise(id = "ex_4", name = "Планка", description = "На руках", sets = 3, reps = 60, emoji = "🧘"),
                WorkoutExercise(id = "ex_5", name = "Берпи", description = "Сокращённая версия", sets = 2, reps = 10, emoji = "🔥")
            )
        )
    )

    val userWorkoutPlan = workoutPlans.first()

    val exercises = listOf(
        Exercise(emoji = "🏃", name = "Бег", description = "Кардио", sets = 3, reps = 10, score = 4.5, recommendedSets = 3, recommendedReps = 10),
        Exercise(emoji = "💪", name = "Отжимания", description = "Классические", sets = 4, reps = 15, score = 4.8, recommendedSets = 4, recommendedReps = 15),
        Exercise(emoji = "🦵", name = "Приседания", description = "С весом тела", sets = 4, reps = 20, score = 4.6, recommendedSets = 4, recommendedReps = 20),
        Exercise(emoji = "🧘", name = "Планка", description = "На руках", sets = 3, reps = 60, score = 4.2, recommendedSets = 3, recommendedReps = 60),
        Exercise(emoji = "🤸", name = "Прыжки", description = "Jumping Jacks", sets = 3, reps = 30, score = 4.0, recommendedSets = 3, recommendedReps = 30),
        Exercise(emoji = "🚴", name = "Велосипед", description = "Кардио низкая", sets = 3, reps = 15, score = 4.3, recommendedSets = 3, recommendedReps = 15),
        Exercise(emoji = "🏊", name = "Плавание", description = "Кроль 50м", sets = 4, reps = 8, score = 4.7, recommendedSets = 4, recommendedReps = 8),
        Exercise(emoji = "🤸‍♂️", name = "Растяжка", description = "Гибкость", sets = 2, reps = 30, score = 3.9, recommendedSets = 2, recommendedReps = 30)
    )

    val injuries = listOf(
        Injury(id = id("inj_knee"), type = "Спортивная травма", bodyPart = "Голень", startDate = date(daysAgo = 14), endDate = date(daysAgo = -1), severity = "medium"),
        Injury(id = id("inj_shoulder"), type = "Перегрузка", bodyPart = "Плечо", startDate = date(daysAgo = 5), endDate = null, severity = "low")
    )

    val traumas = listOf(
        TraumaResponse(id = "knee_sprain", name = "Растяжение колена", bodyPart = "Колено", severity = "medium", description = "Умеренная травма, ограниченная нагрузка", recommendedDaysOff = 14),
        TraumaResponse(id = "shoulder_impingement", name = "Импиджмент плеча", bodyPart = "Плечо", severity = "low", description = "Перегрузка мышц плечевого пояса", recommendedDaysOff = 7)
    )

    data class TraumaResponse(
        val id: String = "",
        val name: String,
        val bodyPart: String,
        val severity: String,
        val description: String,
        val recommendedDaysOff: Int
    )

    val dailyActivities = listOf(
        DailyActivity(id = id("act_today"), userId = uid("amir"), date = "2026-07-02", steps = 8432, calories = 420, activeMinutes = 65, distanceKm = 5.8f),
        DailyActivity(id = id("act_yest"), userId = uid("amir"), date = "2026-07-01", steps = 6210, calories = 310, activeMinutes = 45, distanceKm = 4.2f),
        DailyActivity(id = id("act_2d"), userId = uid("amir"), date = "2026-06-30", steps = 10500, calories = 520, activeMinutes = 80, distanceKm = 7.5f),
        DailyActivity(id = id("act_3d"), userId = uid("amir"), date = "2026-06-29", steps = 4300, calories = 210, activeMinutes = 30, distanceKm = 2.9f),
        DailyActivity(id = id("act_4d"), userId = uid("amir"), date = "2026-06-28", steps = 7800, calories = 390, activeMinutes = 55, distanceKm = 5.3f),
        DailyActivity(id = id("act_5d"), userId = uid("amir"), date = "2026-06-27", steps = 9100, calories = 455, activeMinutes = 70, distanceKm = 6.4f),
        DailyActivity(id = id("act_6d"), userId = uid("amir"), date = "2026-06-26", steps = 5400, calories = 270, activeMinutes = 40, distanceKm = 3.7f)
    )

    val muscleRecoveryGroups = listOf(
        MuscleGroup(name = "Грудные мышцы", hoursRemaining = "2ч", recoveryPercent = 85, totalHours = 48, dotColor = Color(0xFF4ADE80), barColor = Color(0xFF4ADE80)),
        MuscleGroup(name = "Спина (широчайшие)", hoursRemaining = "5ч", recoveryPercent = 60, totalHours = 72, dotColor = Color(0xFFFBBF24), barColor = Color(0xFFFBBF24)),
        MuscleGroup(name = "Бицепсы", hoursRemaining = "12ч", recoveryPercent = 30, totalHours = 48, dotColor = Color(0xFFF87171), barColor = Color(0xFFF87171)),
        MuscleGroup(name = "Трицепсы", hoursRemaining = "8ч", recoveryPercent = 50, totalHours = 48, dotColor = Color(0xFFFBBF24), barColor = Color(0xFFFBBF24)),
        MuscleGroup(name = "Квадрицепсы", hoursRemaining = "24ч", recoveryPercent = 15, totalHours = 72, dotColor = Color(0xFFF87171), barColor = Color(0xFFF87171)),
        MuscleGroup(name = "Икры", hoursRemaining = "6ч", recoveryPercent = 55, totalHours = 48, dotColor = Color(0xFFFBBF24), barColor = Color(0xFFFBBF24)),
        MuscleGroup(name = "Пресс", hoursRemaining = "3ч", recoveryPercent = 75, totalHours = 48, dotColor = Color(0xFF4ADE80), barColor = Color(0xFF4ADE80)),
        MuscleGroup(name = "Ягодицы", hoursRemaining = "18ч", recoveryPercent = 25, totalHours = 72, dotColor = Color(0xFFF87171), barColor = Color(0xFFF87171))
    )

    val recoveredMuscles = listOf("Предплечья", "Шея", "Мышцы кора")

    val achievements = listOf(
        Achievement(id = id("ach_1"), emoji = "🌅", name = "Ранняя пташка", description = "Тренировка до 8:00 утра", unlocked = false),
        Achievement(id = id("ach_2"), emoji = "🏃", name = "Марафонец", description = "10 км за одну неделю", unlocked = false),
        Achievement(id = id("ach_3"), emoji = "🔥", name = "Железная воля", description = "Тренировки 5 дней подряд", unlocked = false),
        Achievement(id = id("ach_4"), emoji = "💪", name = "Атлет 1 уровня", description = "Сдать все нормативы на 5", unlocked = true),
        Achievement(id = id("ach_5"), emoji = "🏆", name = "Чемпион кубка", description = "Победить в внутривузовском турнире", unlocked = true),
        Achievement(id = id("ach_6"), emoji = "📚", name = "Теоретик", description = "Пройти все тесты по теории", unlocked = false),
        Achievement(id = id("ach_7"), emoji = "🎯", name = "Снайпер", description = "100% посещаемость за месяц", unlocked = false),
        Achievement(id = id("ach_8"), emoji = "🧘", name = "Йог", description = "30 дней растяжки подряд", unlocked = false)
    )

    val attendanceSessions = listOf(
        AttendanceSession(id = id("sess_1"), teacherId = uid("teacher_ivanova"), subject = "Физвоспитание", qrCode = "QR-A-20260702-001", startTime = date(hoursAgo = -2).time, endTime = date(hoursAgo = 1).time, location = "Спортивный зал №3"),
        AttendanceSession(id = id("sess_2"), teacherId = uid("teacher_ivanova"), subject = "Физвоспитание", qrCode = "QR-A-20260701-001", startTime = date(daysAgo = 1, hoursAgo = -2).time, endTime = date(daysAgo = 1, hoursAgo = 1).time, location = "Спортивный зал №3"),
        AttendanceSession(id = id("sess_3"), teacherId = uid("teacher_ivanova"), subject = "Легкая атлетика", qrCode = "QR-A-20260630-001", startTime = date(daysAgo = 2, hoursAgo = -1).time, endTime = date(daysAgo = 2, hoursAgo = 2).time, location = "Стадион")
    )

    val attendanceJournal = listOf(
        AttendanceRecord(id = id("att_1"), userId = uid("amir"), sessionId = id("sess_1"), qrCode = "QR-A-20260702-001", timestamp = date(hoursAgo = -1).time, status = AttendanceStatus.CHECKED_IN, createdAt = date(hoursAgo = -1).time),
        AttendanceRecord(id = id("att_2"), userId = uid("zarina"), sessionId = id("sess_1"), qrCode = "QR-A-20260702-001", timestamp = date(hoursAgo = -1).time, status = AttendanceStatus.CHECKED_IN, createdAt = date(hoursAgo = -1).time),
        AttendanceRecord(id = id("att_3"), userId = uid("ruslan"), sessionId = id("sess_1"), qrCode = "QR-A-20260702-001", timestamp = date(hoursAgo = -1).time, status = AttendanceStatus.LATE, createdAt = date(hoursAgo = -1).time),
        AttendanceRecord(id = id("att_4"), userId = uid("dias"), sessionId = id("sess_1"), qrCode = "QR-A-20260702-001", timestamp = 0L, status = AttendanceStatus.ABSENT, createdAt = date(hoursAgo = 1).time),
        AttendanceRecord(id = id("att_5"), userId = uid("amir"), sessionId = id("sess_2"), qrCode = "QR-A-20260701-001", timestamp = date(daysAgo = 1, hoursAgo = -1).time, status = AttendanceStatus.CHECKED_IN, createdAt = date(daysAgo = 1, hoursAgo = -1).time),
        AttendanceRecord(id = id("att_6"), userId = uid("aliya"), sessionId = id("sess_2"), qrCode = "QR-A-20260701-001", timestamp = date(daysAgo = 1, hoursAgo = -1).time, status = AttendanceStatus.CHECKED_IN, createdAt = date(daysAgo = 1, hoursAgo = -1).time),
        AttendanceRecord(id = id("att_7"), userId = uid("bekzat"), sessionId = id("sess_2"), qrCode = "QR-A-20260701-001", timestamp = 0L, status = AttendanceStatus.ABSENT, createdAt = date(daysAgo = 1, hoursAgo = 1).time),
        AttendanceRecord(id = id("att_8"), userId = uid("amir"), sessionId = id("sess_3"), qrCode = "QR-A-20260630-001", timestamp = date(daysAgo = 2, hoursAgo = -1).time, status = AttendanceStatus.CHECKED_IN, createdAt = date(daysAgo = 2, hoursAgo = -1).time)
    )

    val norms = listOf(
        Norm(id = id("norm_1"), name = "Бег 100м", category = NormCategory.SPEED, unit = "сек", gender = Gender.MALE, minAge = 18, maxAge = 25, standards = listOf(
            StandardEntry(age = 18, excellent = "<12.0", good = "12.0-13.5", satisfactory = "13.5-15.0", pass = "15.0-16.0"),
            StandardEntry(age = 25, excellent = "<12.5", good = "12.5-14.0", satisfactory = "14.0-15.5", pass = "15.5-16.5")
        )),
        Norm(id = id("norm_2"), name = "Бег 100м", category = NormCategory.SPEED, unit = "сек", gender = Gender.FEMALE, minAge = 18, maxAge = 25, standards = listOf(
            StandardEntry(age = 18, excellent = "<14.0", good = "14.0-15.5", satisfactory = "15.5-17.0", pass = "17.0-18.0"),
            StandardEntry(age = 25, excellent = "<14.5", good = "14.5-16.0", satisfactory = "16.0-17.5", pass = "17.5-18.5")
        )),
        Norm(id = id("norm_3"), name = "Подтягивания", category = NormCategory.STRENGTH, unit = "раз", gender = Gender.MALE, minAge = 18, maxAge = 25, standards = listOf(
            StandardEntry(age = 18, excellent = ">15", good = "12-15", satisfactory = "8-12", pass = "5-8"),
            StandardEntry(age = 25, excellent = ">13", good = "10-13", satisfactory = "7-10", pass = "4-7")
        )),
        Norm(id = id("norm_4"), name = "Отжимания", category = NormCategory.STRENGTH, unit = "раз", gender = Gender.FEMALE, minAge = 18, maxAge = 25, standards = listOf(
            StandardEntry(age = 18, excellent = ">20", good = "15-20", satisfactory = "10-15", pass = "5-10"),
            StandardEntry(age = 25, excellent = ">18", good = "13-18", satisfactory = "8-13", pass = "4-8")
        )),
        Norm(id = id("norm_5"), name = "Плавание 50м", category = NormCategory.ENDURANCE, unit = "сек", gender = Gender.MALE, minAge = 18, maxAge = 25, standards = listOf(
            StandardEntry(age = 18, excellent = "<40", good = "40-50", satisfactory = "50-60", pass = "60-70"),
            StandardEntry(age = 25, excellent = "<42", good = "42-52", satisfactory = "52-62", pass = "62-72")
        )),
        Norm(id = id("norm_6"), name = "Плавание 50м", category = NormCategory.ENDURANCE, unit = "сек", gender = Gender.FEMALE, minAge = 18, maxAge = 25, standards = listOf(
            StandardEntry(age = 18, excellent = "<45", good = "45-55", satisfactory = "55-65", pass = "65-75"),
            StandardEntry(age = 25, excellent = "<47", good = "47-57", satisfactory = "57-67", pass = "67-77")
        )),
        Norm(id = id("norm_7"), name = "Наклон вперед", category = NormCategory.FLEXIBILITY, unit = "см", gender = Gender.MALE, minAge = 18, maxAge = 25, standards = listOf(
            StandardEntry(age = 18, excellent = ">15", good = "10-15", satisfactory = "5-10", pass = "0-5"),
            StandardEntry(age = 25, excellent = ">12", good = "7-12", satisfactory = "2-7", pass = "-3-2")
        )),
        Norm(id = id("norm_8"), name = "Челночный бег 3x10м", category = NormCategory.COORDINATION, unit = "сек", gender = Gender.MALE, minAge = 18, maxAge = 25, standards = listOf(
            StandardEntry(age = 18, excellent = "<7.5", good = "7.5-8.0", satisfactory = "8.0-8.5", pass = "8.5-9.0"),
            StandardEntry(age = 25, excellent = "<7.8", good = "7.8-8.3", satisfactory = "8.3-8.8", pass = "8.8-9.3")
        ))
    )

    val physicalTests = listOf(
        PhysicalTest(id = id("test_1"), userId = uid("amir"), normId = id("norm_1"), value = "13.2", unit = "сек", date = date(daysAgo = 3).time, score = "good"),
        PhysicalTest(id = id("test_2"), userId = uid("amir"), normId = id("norm_3"), value = "14", unit = "раз", date = date(daysAgo = 3).time, score = "good"),
        PhysicalTest(id = id("test_3"), userId = uid("amir"), normId = id("norm_4"), value = "25", unit = "раз", date = date(daysAgo = 3).time, score = "excellent"),
        PhysicalTest(id = id("test_4"), userId = uid("amir"), normId = id("norm_7"), value = "12", unit = "см", date = date(daysAgo = 3).time, score = "good"),
        PhysicalTest(id = id("test_5"), userId = uid("zarina"), normId = id("norm_2"), value = "15.1", unit = "сек", date = date(daysAgo = 5).time, score = "satisfactory"),
        PhysicalTest(id = id("test_6"), userId = uid("ruslan"), normId = id("norm_1"), value = "12.8", unit = "сек", date = date(daysAgo = 2).time, score = "good")
    )

    val exerciseFeedbacks = listOf(
        ExerciseFeedback(id = id("ef_1"), exerciseId = "ex_1", userId = uid("amir"), rating = 5, comment = "Отличное упражнение для разминки", timestamp = date(daysAgo = 1).time),
        ExerciseFeedback(id = id("ef_2"), exerciseId = "ex_2", userId = uid("amir"), rating = 4, comment = "Тяжело на пустом желудке", timestamp = date(daysAgo = 2).time),
        ExerciseFeedback(id = id("ef_3"), exerciseId = "ex_3", userId = uid("zarina"), rating = 4, comment = "Нравится, но устаю быстро", timestamp = date(daysAgo = 3).time),
        ExerciseFeedback(id = id("ef_4"), exerciseId = "ex_4", userId = uid("ruslan"), rating = 5, comment = "Супер для core", timestamp = date(daysAgo = 1).time),
        ExerciseFeedback(id = id("ef_5"), exerciseId = "ex_1", userId = uid("bekzat"), rating = 3, comment = "Слишком долго", timestamp = date(daysAgo = 4).time)
    )

    val planFeedbacks = listOf(
        PlanFeedback(id = id("pf_1"), planId = id("plan_1247"), userId = uid("amir"), rating = 5, comment = "Отличный план, все упражнения понятны", timestamp = date(daysAgo = 1).time),
        PlanFeedback(id = id("pf_2"), planId = id("plan_1248"), userId = uid("zarina"), rating = 3, comment = "Слишком интенсивно для подготовительной группы", timestamp = date(daysAgo = 4).time),
        PlanFeedback(id = id("pf_3"), planId = id("plan_1249"), userId = uid("ruslan"), rating = 4, comment = "Хороший баланс нагрузок", timestamp = date(daysAgo = 6).time)
    )

    val leaderboard = listOf(
        LeaderboardEntry(rank = 1, studentId = uid("gulnara"), name = "Gulnara Tleubek", score = "4.0/4", trend = TrendDirection.STABLE),
        LeaderboardEntry(rank = 2, studentId = uid("ruslan"), name = "Ruslan Nurlanov", score = "3.8/4", trend = TrendDirection.UP),
        LeaderboardEntry(rank = 3, studentId = uid("madina"), name = "Madina Jumabek", score = "3.7/4", trend = TrendDirection.STABLE),
        LeaderboardEntry(rank = 4, studentId = uid("amir"), name = "Amir Seitkali", score = "3.2/4", trend = TrendDirection.UP),
        LeaderboardEntry(rank = 5, studentId = uid("aliya"), name = "Aliya Kasymova", score = "3.5/4", trend = TrendDirection.DOWN),
        LeaderboardEntry(rank = 6, studentId = uid("bekzat"), name = "Bekzat Muratov", score = "2.9/4", trend = TrendDirection.STABLE),
        LeaderboardEntry(rank = 7, studentId = uid("zarina"), name = "Zarina Akhmetova", score = "2.5/4", trend = TrendDirection.DOWN),
        LeaderboardEntry(rank = 8, studentId = uid("kanat"), name = "Kanat Seilov", score = "2.3/4", trend = TrendDirection.STABLE),
        LeaderboardEntry(rank = 9, studentId = uid("erik"), name = "Erik Akhanov", score = "2.1/4", trend = TrendDirection.UP),
        LeaderboardEntry(rank = 10, studentId = uid("dias"), name = "Dias Issayev", score = "1.8/4", trend = TrendDirection.STABLE)
    )

    val roles = listOf(
        RoleConfig(id = "student", name = "Student", permissions = listOf("read_own_profile", "read_plans", "submit_feedback", "scan_qr", "view_achievements"), description = "Студент"),
        RoleConfig(id = "teacher", name = "Teacher", permissions = listOf("read_all_profiles", "manage_plans", "view_attendance", "manage_students", "retrain_model"), description = "Преподаватель"),
        RoleConfig(id = "admin", name = "Admin", permissions = listOf("*"), description = "Администратор")
    )

    val muscleGroupsConfig = listOf(
        MuscleGroupConfig(id = "chest", name = "Грудные мышцы", emoji = "🫁", description = "Большие и малые грудные мышцы"),
        MuscleGroupConfig(id = "back", name = "Спина", emoji = "🔙", description = "Широчайшие мышцы, трапеции"),
        MuscleGroupConfig(id = "legs", name = "Ноги", emoji = "🦵", description = "Квадрицепсы, бицепсы бедра, икры"),
        MuscleGroupConfig(id = "shoulders", name = "Плечи", emoji = "💪", description = "Дельтовидные мышцы"),
        MuscleGroupConfig(id = "arms", name = "Руки", emoji = "💪", description = "Бицепсы, трицепсы"),
        MuscleGroupConfig(id = "core", name = "Пресс", emoji = "🧘", description = "Мышцы кора, прямые мышцы живота"),
        MuscleGroupConfig(id = "glutes", name = "Ягодицы", emoji = "🍑", description = "Грушевидные мышцы, ягодичные"),
        MuscleGroupConfig(id = "cardio", name = "Кардио", emoji = "❤️", description = "Сердечно-сосудистая система")
    )

    val exerciseTypesConfig = listOf(
        ExerciseTypeConfig(id = "strength", name = "Силовые", emoji = "🏋️", description = "Упражнения с сопротивлением"),
        ExerciseTypeConfig(id = "cardio", name = "Кардио", emoji = "🏃", description = "Выносливые аэробные нагрузки"),
        ExerciseTypeConfig(id = "flexibility", name = "Гибкость", emoji = "🧘", description = "Растягивающие упражнения"),
        ExerciseTypeConfig(id = "balance", name = "Баланс", emoji = "⚖️", description = "Упражнения на равновесие"),
        ExerciseTypeConfig(id = "coordination", name = "Координация", emoji = "🎯", description = "Упражнения на ловкость")
    )

    val quizScores = listOf(
        QuizScore(id = id("qs_1"), quizId = "health_basics", score = 7, totalQuestions = 10, timestamp = date(daysAgo = 7)),
        QuizScore(id = id("qs_2"), quizId = "health_basics", score = 8, totalQuestions = 10, timestamp = date(daysAgo = 3)),
        QuizScore(id = id("qs_3"), quizId = "health_basics", score = 9, totalQuestions = 10, timestamp = date())
    )

    val dashboardStats = DashboardStats(
        todaySteps = 8432,
        weeklyGoalProgress = 68,
        attendanceRate = 92,
        averageScore = "3.2/4",
        upcomingSessions = 3,
        pendingTasks = 2
    )

    val statsCards = listOf(
        StatCard("68%", "Выполнение", Color(0xFF6C63FF), Color(0x146C63FF)),
        StatCard("3.2/4", "Средний балл", Color(0xFF4ADE80), Color(0x124ADE80)),
        StatCard("92%", "Посещаемость", Color(0xFFFBBF24), Color(0x12FBBF24))
    )

    val exerciseStats = listOf(
        ExerciseStat("🏃", Color(0x26FBBF24), "Бег", "12", "85%", "Basic", Color(0xFF4ADE80)),
        ExerciseStat("💪", Color(0x266C63FF), "Отжимания", "24", "70%", "Prepared", Color(0xFF6C63FF)),
        ExerciseStat("🧘", Color(0x264ADE80), "Планка", "18", "60%", "Basic", Color(0xFF4ADE80))
    )

    val retrainHistory = listOf(
        RetrainHistoryEntry(id = id("retrain_1"), status = RetrainStatus.REPLACED, date = "2026-06-08 14:32", aucBefore = 0.847, aucAfter = 0.862),
        RetrainHistoryEntry(id = id("retrain_2"), status = RetrainStatus.REPLACED, date = "2026-06-01 10:15", aucBefore = 0.831, aucAfter = 0.847),
        RetrainHistoryEntry(id = id("retrain_3"), status = RetrainStatus.SKIPPED, date = "2026-05-25 09:00", aucBefore = 0.831, aucAfter = 0.831)
    )

    val modelInfo = ModelInfo(
        fileName = "fitness_ranker.pkl",
        algorithm = "XGBoost",
        isActive = true,
        retrainCount = 3,
        newDataCount = 145,
        threshold = 200,
        history = retrainHistory
    )
}
