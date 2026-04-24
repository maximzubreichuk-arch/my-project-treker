package com.example.myprojecttreker.presentation.ui.taskeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Блок управления дополнительными временами задачи.
 *
 * Позволяет:
 * - отображать список дополнительных времен
 * - редактировать существующее время
 * - удалять время
 * - добавлять новое время
 *
 * Особенности:
 * - каждое время — отдельная кнопка
 * - редактирование и добавление происходят через TimePicker
 */
@OptIn(ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class)
@Composable
fun ExtraTimesBlock(
    times: List<LocalTime>,
    onChange: (List<LocalTime>) -> Unit
) {
    // Индекс редактируемого времени (null — не редактируем)
    var editIndex by remember { mutableStateOf<Int?>(null) }
    // Флаг добавления нового времени
    var addNew by remember { mutableStateOf(false) }

    Column {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Отображаем все существующие времена
            times.forEachIndexed { index, time ->
                OutlinedButton(
                    onClick = {
                        // открываем редактирование конкретного времени
                        editIndex = index
                    }
                ) {
                    Text(
                        text = time.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        )
                    )
                }
            }
            // Кнопка добавления нового времени
            OutlinedButton(
                onClick = { addNew = true }
            ) {
                Text("+")
            }
        }

    }
    // Редактирование существующего времени
    editIndex?.let { index ->
        val current = times[index]
        // Инициализируем TimePicker текущим значением
        val state = rememberTimePickerState(
            initialHour = current.hour,
            initialMinute = current.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { editIndex = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Обновляем список с новым временем
                        val updated = times.toMutableList()
                        updated[index] = LocalTime.of(state.hour, state.minute)

                        onChange(updated)
                        editIndex = null
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // Удаляем выбранное время
                        val updated = times.toMutableList()
                        updated.removeAt(index)

                        onChange(updated)
                        editIndex = null
                    }
                ) {
                    Text("Убрать время")
                }
            },
            text = {
                TimePicker(state = state)
            }
        )
    }
    // Добавление нового времени
    if (addNew) {
        val now = LocalTime.now()
        // Начальное значение — текущее время
        val state = rememberTimePickerState(
            initialHour = now.hour,
            initialMinute = now.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { addNew = false },

            confirmButton = {
                TextButton(
                    onClick = {
                        // Добавляем новое время в список
                        onChange(times + LocalTime.of(state.hour, state.minute))
                        addNew = false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { addNew = false }
                ) {
                    Text("Отмена")
                }
            },

            text = {
                TimePicker(state = state)
            }
        )
    }
}
