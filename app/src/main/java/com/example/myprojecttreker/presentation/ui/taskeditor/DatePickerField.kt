package com.example.myprojecttreker.presentation.ui.taskeditor

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myprojecttreker.domain.model.RepeatType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Поле выбора даты / дня месяца.
 *
 * Поведение зависит от типа повторения:
 * - WEEKLY → дата не используется (поле отключено)
 * - MONTHLY → выбирается только день месяца (1–31)
 * - остальные → обычный DatePicker
 *
 * Особенности:
 * - поле только для отображения (readOnly)
 * - клик открывает диалог выбора
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    state: TaskEditorState,
    repeatType: RepeatType = state.repeatType,
    modifier: Modifier = Modifier,
    onChange: (LocalDate) -> Unit,
    onDayOfMonthChange: (Int) -> Unit
) {

    // Флаг открытия диалога выбора
    var openDialog by remember { mutableStateOf(false) }

    // Определяем тип поведения
    val isWeekly = repeatType == RepeatType.WEEKLY
    val isMonthly = repeatType == RepeatType.MONTHLY

    Box(
        modifier = modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = when {
                // Для MONTHLY показываем только день месяца
                isMonthly -> state.dayOfMonth.toString()
                // В остальных случаях — полную дату
                else -> state.date.toString()
            },
            onValueChange = {},
            label = {
                Text(
                    when {
                        // WEEKLY — дата не используется
                        isWeekly -> "Дата не используется"
                        // MONTHLY — выбор дня месяца
                        isMonthly -> "День месяца"
                        // Остальные — обычная дата
                        else -> "Дата"
                    }
                )
            },
            // пользователь не вводит вручную
            readOnly = true,
            // отключаем для WEEKLY
            enabled = !isWeekly,
            modifier = Modifier.fillMaxWidth()
        )

        // Перекрывающий слой для обработки клика по всему полю
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = !isWeekly) {
                    openDialog = true
                }
        )
    }

    if (openDialog) {

        // MONTHLY режим → выбор дня (1–31)
        if (isMonthly) {
            AlertDialog(
                onDismissRequest = { openDialog = false },
                title = { Text("Выберите день") },
                text = {
                    LazyColumn {
                        items(31) { index ->
                            val day = index + 1

                            Text(
                                text = day.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // передаём выбранный день
                                        onDayOfMonthChange(day)
                                        // закрываем диалог
                                        openDialog = false
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                },
                confirmButton = {}
            )

        } else {

            // Обычный DatePicker (для всех остальных типов)
            val pickerState = rememberDatePickerState(
                initialSelectedDateMillis =
                    state.date
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
            )

            DatePickerDialog(
                onDismissRequest = { openDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Если дата выбрана — конвертируем millis → LocalDate
                            pickerState.selectedDateMillis?.let { millis ->

                                val date =
                                    Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()

                                onChange(date)
                            }
                            openDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = { openDialog = false }
                    ) {
                        Text("Отмена")
                    }
                }

            ) {
                DatePicker(state = pickerState)
            }
        }
    }
}