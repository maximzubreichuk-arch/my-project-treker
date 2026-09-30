package com.example.myprojecttreker.domain.settings

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val languageTag: String = "ru",
    val defaultSoundUri: String? = null,
    val vibrationEnabled: Boolean = true,
    val defaultReminderMinutes: Int = 15,
    val timeFormat24Hour: Boolean = true,
    val firstDayOfWeek: Int = java.time.DayOfWeek.MONDAY.value
)
