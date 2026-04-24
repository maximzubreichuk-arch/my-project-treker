package com.example.myprojecttreker.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Цветовые схемы приложения (тёмная и светлая темы).
 *
 * Определяют основные цвета интерфейса:
 * - primary (основной цвет)
 * - secondary (вторичный цвет)
 * - tertiary (дополнительный акцент)
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * Основная тема приложения.
 *
 * Отвечает за:
 * - выбор цветовой схемы (светлая / тёмная / динамическая)
 * - применение MaterialTheme ко всему приложению
 *
 * Особенности:
 * - поддерживает dynamic color (Android 12+)
 * - автоматически подстраивается под системную тему
 */
@Composable
fun MyProjectTrekerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Выбор цветовой схемы в зависимости от условий
    val colorScheme = when {
        // Используем динамические цвета (Material You) если доступно
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current

            // Выбор тёмной или светлой динамической темы
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Принудительно тёмная тема
        darkTheme -> DarkColorScheme

        // Светлая тема по умолчанию
        else -> LightColorScheme
    }

    // Применяем тему ко всему UI
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}