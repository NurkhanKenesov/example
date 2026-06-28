package com.example.myapplication

// Медицинская группа здоровья
enum class MedicalGroup(val displayName: String) {
    BASIC("Основная"),
    PREPARATORY("Подготовительная"),
    SPECIAL("СМГ (спец. группа)")
}

// Пол
enum class Gender(val displayName: String) {
    MALE("Мужской"),
    FEMALE("Женский")
}

// Модель профиля — зеркалит структуру Firestore
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "student",
    val gender: Gender = Gender.MALE,
    val age: Int = 18,
    val heightCm: Int = 170,
    val weightKg: Float = 65f,
    val medicalGroup: MedicalGroup = MedicalGroup.BASIC,
    val studentId: String = "",
    val groupName: String = "",
    val profileComplete: Boolean = false
) {
    val initials: String
        get() = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
            .ifEmpty { "AS" }
    // Авторасчёт ИМТ
    val bmi: Float
        get() {
            val heightM = heightCm / 100f
            return if (heightM > 0) weightKg / (heightM * heightM) else 0f
        }

    val bmiLabel: String
        get() = when {
            bmi < 18.5f -> "Недовес"
            bmi < 25f   -> "Норма"
            bmi < 30f   -> "Избыточный вес"
            else        -> "Ожирение"
        }

    // Конвертация в Map для Firestore
    fun toMap(): Map<String, Any> = mapOf(
        "uid"             to uid,
        "name"            to name,
        "email"           to email,
        "role"            to role,
        "gender"          to gender.name,
        "age"             to age,
        "heightCm"        to heightCm,
        "weightKg"        to weightKg,
        "medicalGroup"    to medicalGroup.name,
        "studentId"       to studentId,
        "groupName"       to groupName,
        "profileComplete" to profileComplete
    )

    companion object {
        fun fromMap(map: Map<String, Any>): UserProfile = UserProfile(
            uid           = map["uid"] as? String ?: "",
            name          = map["name"] as? String ?: "",
            email         = map["email"] as? String ?: "",
            role          = map["role"] as? String ?: "student",
            gender        = Gender.entries.firstOrNull { it.name == map["gender"] } ?: Gender.MALE,
            age           = (map["age"] as? Long)?.toInt() ?: 18,
            heightCm      = (map["heightCm"] as? Long)?.toInt() ?: 170,
            weightKg      = (map["weightKg"] as? Double)?.toFloat() ?: 65f,
            medicalGroup  = MedicalGroup.entries.firstOrNull { it.name == map["medicalGroup"] }
                            ?: MedicalGroup.BASIC,
            studentId     = map["studentId"] as? String ?: "",
            groupName     = map["groupName"] as? String ?: "",
            profileComplete = map["profileComplete"] as? Boolean ?: false
        )
    }
}
