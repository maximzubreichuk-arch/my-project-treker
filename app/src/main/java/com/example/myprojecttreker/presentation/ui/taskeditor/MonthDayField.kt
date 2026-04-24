package com.example.myprojecttreker.presentation.ui.taskeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Поле выбора дня месяца.
 *
 * Используется для типа повторения MONTHLY.
 *
 * Особенности:
 * - отображает выбранный день
 * - при нажатии открывает список (1–31)
 * - пользователь не вводит значение вручную
 */
@Composable
fun MonthDayField(
    day: Int,
    modifier: Modifier = Modifier,
    onChange: (Int) -> Unit
) {
    // Флаг открытия диалога выбора дня
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = day.toString(),
        onValueChange = {},
        label = { Text("День месяца") },
        // запрещаем ручной ввод
        readOnly = true,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                // открываем диалог выбора
                showDialog = true
            }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Выберите день") },
            text = {

                LazyColumn {
                    items(31) { index ->
                        val value = index + 1

                        Text(
                            text = value.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // передаём выбранный день
                                    onChange(value)
                                    // закрываем диалог
                                    showDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}