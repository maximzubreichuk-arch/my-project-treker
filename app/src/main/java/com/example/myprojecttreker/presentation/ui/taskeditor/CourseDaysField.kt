package com.example.myprojecttreker.presentation.ui.taskeditor

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * Поле ввода количества дней (для типа повторения COURSE).
 *
 * Позволяет пользователю задать длительность курса в днях.
 *
 * Особенности:
 * - хранит ввод как текст (для корректной работы TextField)
 * - при вводе пытается преобразовать значение в Int
 * - вызывает onChange только если ввод корректный
 */
@Composable
fun CourseDaysField(
    days: Int,
    modifier: Modifier = Modifier,
    onChange: (Int) -> Unit
) {
    // Локальное состояние текста (String нужен для TextField)
    var text by remember {
        mutableStateOf(days.toString())
    }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it

            // Пытаемся преобразовать ввод в число
            // Если не получилось (например, пусто или буквы) — игнорируем
            it.toIntOrNull()?.let { value ->
                onChange(value)
            }
        },
        label = { Text("дни") },
        modifier = modifier
    )
}