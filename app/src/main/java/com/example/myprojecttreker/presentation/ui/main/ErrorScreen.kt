package com.example.myprojecttreker.presentation.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Экран отображения ошибки.
 *
 * Используется, когда:
 * - произошла ошибка при загрузке или обработке данных
 *
 * Отвечает за:
 * - отображение текста ошибки
 * - предоставление пользователю возможности повторить действие
 *
 * Особенности:
 * - не содержит бизнес-логики
 * - работает через callback (onRetry)
 * - используется как часть состояния (DayUiState.Error)
 */
@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Отображение текста ошибки
        Text(text = message)

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка повторного выполнения действия
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}
