package com.example.myprojecttreker.presentation.ui.main.taskeditor


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.myprojecttreker.R
import com.example.myprojecttreker.data.settings.SettingsManager
import java.time.LocalTime

/**
 * Поле выбора времени для задачи.
 *
 * Отвечает за:
 * - отображение текущего времени задачи
 * - открытие диалога выбора времени (TimePicker)
 * - передачу выбранного значения наружу
 *
 * Особенности:
 * - не позволяет ввод вручную (только через UI)
 * - поддерживает состояние "без времени"
 * - использует текущее время как дефолт при первом выборе
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    state: TaskEditorState,
    modifier: Modifier = Modifier,
    onChange: (LocalTime?) -> Unit
) {
    // Состояние открытия диалога выбора времени
    var openDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val settings by settingsManager.settings.collectAsState()

    // Отображаемое значение:
    // либо выбранное время, либо заглушка "Без времени"
    val formatter = java.time.format.DateTimeFormatter.ofPattern(if (settings.timeFormat24Hour) "HH:mm" else "hh:mm a")
    val text = state.time?.format(formatter) ?: stringResource(R.string.no_time)

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Поле отображает текущее значение времени (не редактируется вручную)
        OutlinedTextField(
            value = text,
            onValueChange = {},
            label = { Text(stringResource(R.string.time)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Прозрачный слой поверх поля для обработки клика
        // Открытие диалога выбора времени
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { openDialog = true }
        )
    }
    // Показываем диалог выбора времени
    if (openDialog) {

        // Начальное значение:
        // если время уже выбрано — используем его,
        // иначе — текущее время
        val now = state.time ?: LocalTime.now()

        // Состояние TimePicker
        val timeState = rememberTimePickerState(
            initialHour = now.hour,
            initialMinute = now.minute,
            is24Hour = settings.timeFormat24Hour
        )

        AlertDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {

                        // Формируем выбранное время из состояния пикера
                        val time = LocalTime.of(
                            timeState.hour,
                            timeState.minute
                        )
                        // Передаём выбранное время наружу
                        onChange(time)
                        // Закрываем диалог
                        openDialog = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        // Закрываем диалог без изменений
                        openDialog = false
                    }
                ) {
                    Text(stringResource(R.string.no_time))
                }
            },

            text = {
                // Компонент выбора времени (часы/минуты)
                TimePicker(
                    state = timeState
                )
            }
        )
    }
}