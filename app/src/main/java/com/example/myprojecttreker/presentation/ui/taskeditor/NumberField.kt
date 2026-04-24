package com.example.myprojecttreker.presentation.ui.taskeditor

import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Универсальное текстовое поле для ввода числовых значений.
 *
 * Используется в формах, где требуется простой ввод числа:
 * - дни курса
 * - значения напоминания (часы / минуты)
 *
 * Особенности:
 * - не валидирует ввод (это делает внешний слой)
 * - работает через строку (String), чтобы избежать ошибок парсинга
 */
@Composable
fun NumberField(
    value: String,
    label: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        // передаём новое значение наружу
        onValueChange = {
            onChange(it)
        },
        // фиксированная ширина для компактного UI
        modifier = Modifier.width(80.dp),
        label = {
            // отображаем название поля (например: Min / Hour / Day)
            Text(label)
        },
        // запрещаем перенос строк
        singleLine = true

    )

}