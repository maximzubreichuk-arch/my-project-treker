package com.example.myprojecttreker.presentation.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.LocalDate

/**
 * Экран пустого состояния (Empty state) для выбранного дня.
 *
 * Отображается, когда:
 * - на выбранную дату отсутствуют задачи
 *
 * Отвечает за:
 * - информирование пользователя об отсутствии данных
 *
 * Особенности:
 * - не содержит логики
 * - используется как часть UI-состояния (DayUiState.Empty)
 */
@Composable
fun EmptyScreen(date: LocalDate) {
    Box(
        modifier = Modifier.fillMaxSize(),

        // Центрируем содержимое по экрану
        contentAlignment = Alignment.Center
    ) {
        // Сообщение об отсутствии задач
        Text(text = "На этот день задач нет")
    }
}
