package com.example.myprojecttreker.data.settings

import android.content.Context

/**
 * Менеджер настроек приложения.
 *
 * Отвечает за работу с SharedPreferences.
 *
 * В текущей реализации:
 * - сохраняет дефолтный звук уведомлений
 * - предоставляет доступ к нему
 */
class SettingsManager(context: Context) {

    // SharedPreferences для хранения настроек приложения
    private val prefs =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    // Сохраняет выбранный пользователем звук по умолчанию
    fun saveDefaultSound(uri: String?) {
        prefs.edit().putString("default_sound", uri).apply()
    }

    // Возвращает сохранённый звук по умолчанию (если есть)
    fun getDefaultSound(): String? {
        return prefs.getString("default_sound", null)
    }
}