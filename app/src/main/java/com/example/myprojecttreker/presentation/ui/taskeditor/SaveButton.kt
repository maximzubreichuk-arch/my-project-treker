package com.example.myprojecttreker.presentation.ui.taskeditor


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Кнопка сохранения задачи.
 *
 * Используется в нижней части экрана редактора задачи.
 * При нажатии вызывает внешний обработчик (onClick),
 * который выполняет валидацию и сохранение данных.
 */
@Composable
fun SaveButton(
    onClick: () -> Unit
) {
    Button(
        // обработчик нажатия передаётся извне (UI не содержит бизнес-логики)
        onClick = onClick,
        // кнопка занимает всю ширину экрана с отступами
        modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp)

    ) {
        // текст кнопки
        Text("Сохранить")
    }
}