package com.example.myapplication.data.models

/**
 * Fixed set of physical tests a teacher can edit on a student's profile.
 * Backed by the existing [Norm] model so values are stored as [PhysicalTest]s.
 */
object StudentTestNorm {
    const val COOPER = "cooper"
    const val PUSHUPS = "pushups"
    const val PULLUPS = "pullups"
    const val FLEXIBILITY = "flexibility"
    const val ABS = "abs"
    const val JUMP = "jump"
}

val studentPhysicalTestNorms: List<Norm> = listOf(
    Norm(id = StudentTestNorm.COOPER, name = "Тест Купера", category = NormCategory.ENDURANCE, unit = "м"),
    Norm(id = StudentTestNorm.PUSHUPS, name = "Отжимания", category = NormCategory.STRENGTH, unit = "раз"),
    Norm(id = StudentTestNorm.PULLUPS, name = "Подтягивания", category = NormCategory.STRENGTH, unit = "раз"),
    Norm(id = StudentTestNorm.FLEXIBILITY, name = "Гибкость", category = NormCategory.FLEXIBILITY, unit = "см"),
    Norm(id = StudentTestNorm.ABS, name = "Пресс", category = NormCategory.STRENGTH, unit = "раз"),
    Norm(id = StudentTestNorm.JUMP, name = "Прыжок в длину", category = NormCategory.COORDINATION, unit = "см")
)
