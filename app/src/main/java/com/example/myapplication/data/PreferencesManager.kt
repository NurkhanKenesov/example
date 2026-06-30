package com.example.myapplication.data

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Top-level delegate — single DataStore instance per file
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

enum class ThemeMode(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system")
}

enum class Language(val value: String, val displayName: String) {
    RU("ru", "Русский"),
    EN("en", "English"),
    KK("kk", "Қазақша")
}

private object SettingsKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val WORKOUT_REMINDERS = booleanPreferencesKey("workout_reminders")
    val WEEKLY_REPORT = booleanPreferencesKey("weekly_report")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val LANGUAGE = stringPreferencesKey("language")
    val HEALTH_CONNECT_ENABLED = booleanPreferencesKey("health_connect_enabled")
}

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        val value = prefs[SettingsKeys.THEME_MODE] ?: ThemeMode.SYSTEM.value
        ThemeMode.entries.firstOrNull { it.value == value } ?: ThemeMode.SYSTEM
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.NOTIFICATIONS_ENABLED] ?: true
    }

    val workoutReminders: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.WORKOUT_REMINDERS] ?: true
    }

    val weeklyReport: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.WEEKLY_REPORT] ?: false
    }

    val userName: Flow<String> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.USER_NAME] ?: ""
    }

    val userEmail: Flow<String> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.USER_EMAIL] ?: ""
    }

    val language: Flow<Language> = dataStore.data.map { prefs ->
        val value = prefs[SettingsKeys.LANGUAGE] ?: Language.RU.value
        Language.entries.firstOrNull { it.value == value } ?: Language.RU
    }

    val healthConnectEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.HEALTH_CONNECT_ENABLED] ?: false
    }

    // Writes
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[SettingsKeys.THEME_MODE] = mode.value }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setWorkoutReminders(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.WORKOUT_REMINDERS] = enabled }
    }

    suspend fun setWeeklyReport(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.WEEKLY_REPORT] = enabled }
    }

    suspend fun setUserName(name: String) {
        dataStore.edit { it[SettingsKeys.USER_NAME] = name }
    }

    suspend fun setUserEmail(email: String) {
        dataStore.edit { it[SettingsKeys.USER_EMAIL] = email }
    }

    suspend fun setLanguage(lang: Language) {
        dataStore.edit { it[SettingsKeys.LANGUAGE] = lang.value }
    }

    suspend fun setHealthConnectEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.HEALTH_CONNECT_ENABLED] = enabled }
    }
}

// CompositionLocal for providing PreferencesManager down the tree
val LocalPreferencesManager = staticCompositionLocalOf<PreferencesManager> {
    error("No PreferencesManager provided. Wrap with CompositionLocalProvider.")
}
