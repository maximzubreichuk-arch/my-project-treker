package com.example.myprojecttreker.data.settings

import android.content.Context
import com.example.myprojecttreker.domain.settings.AppSettings
import com.example.myprojecttreker.domain.settings.AppThemeMode
import com.example.myprojecttreker.data.audio.AppSounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(load())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun load(): AppSettings = AppSettings(
        themeMode = runCatching { AppThemeMode.valueOf(prefs.getString(KEY_THEME, AppThemeMode.SYSTEM.name)!!) }
            .getOrDefault(AppThemeMode.SYSTEM),
        languageTag = prefs.getString(KEY_LANGUAGE, "ru") ?: "ru",
        defaultSoundUri = prefs.getString(KEY_SOUND, AppSounds.uri(appContext.packageName, AppSounds.all.first())),
        vibrationEnabled = prefs.getBoolean(KEY_VIBRATION, true),
        defaultReminderMinutes = prefs.getInt(KEY_DEFAULT_REMINDER, 15),
        timeFormat24Hour = prefs.getBoolean(KEY_24H, true),
        firstDayOfWeek = prefs.getInt(KEY_FIRST_DAY, java.time.DayOfWeek.MONDAY.value)
    )

    private fun update(transform: (AppSettings) -> AppSettings) {
        val updated = transform(_settings.value)
        _settings.value = updated
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
        update { it.copy(themeMode = mode) }
    }

    fun setLanguageTag(tag: String) {
        prefs.edit().putString(KEY_LANGUAGE, tag).apply()
        update { it.copy(languageTag = tag) }
    }

    fun saveDefaultSound(uri: String?) {
        prefs.edit().putString(KEY_SOUND, uri).apply()
        update { it.copy(defaultSoundUri = uri) }
    }

    fun getDefaultSound(): String? = _settings.value.defaultSoundUri

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
        update { it.copy(vibrationEnabled = enabled) }
    }

    fun setDefaultReminderMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_DEFAULT_REMINDER, minutes.coerceAtLeast(0)).apply()
        update { it.copy(defaultReminderMinutes = minutes.coerceAtLeast(0)) }
    }

    fun setTimeFormat24Hour(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_24H, enabled).apply()
        update { it.copy(timeFormat24Hour = enabled) }
    }

    fun setFirstDayOfWeek(day: Int) {
        prefs.edit().putInt(KEY_FIRST_DAY, day.coerceIn(1, 7)).apply()
        update { it.copy(firstDayOfWeek = day.coerceIn(1, 7)) }
    }

    companion object {
        private const val KEY_THEME = "theme_mode"
        private const val KEY_LANGUAGE = "language_tag"
        private const val KEY_SOUND = "default_sound"
        private const val KEY_VIBRATION = "vibration_enabled"
        private const val KEY_DEFAULT_REMINDER = "default_reminder_minutes"
        private const val KEY_24H = "time_format_24h"
        private const val KEY_FIRST_DAY = "first_day_of_week"
    }
}
