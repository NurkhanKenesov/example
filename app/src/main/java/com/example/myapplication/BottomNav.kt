package com.example.myapplication

data class NavItem(val icon: String, val label: String, val isActive: Boolean = false)

val navItems = listOf(
    NavItem("🏠", "Главная"),
    NavItem("🤖", "ИИ"),
    NavItem("📅", "Планы"),
    NavItem("📚", "Обучение"),
    NavItem("🏆", "Рейтинг"),
    NavItem("💪", "Достижения"),
    NavItem("👤", "Профиль"),
)