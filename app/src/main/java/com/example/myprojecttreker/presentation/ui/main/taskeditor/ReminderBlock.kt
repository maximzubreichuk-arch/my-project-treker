package com.example.myprojecttreker.presentation.ui.main.taskeditor


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.myprojecttreker.R
import com.example.myprojecttreker.domain.ReminderOffset

/**
 * Блок настройки напоминания (ReminderOffset).
 *
 * Позволяет задать, за сколько времени до задачи
 * должно сработать напоминание:
 * - минуты
 * - часы
 * - дни
 *
 * Если offset == null → блок скрыт (напоминание выключено).
 */
@Composable
fun ReminderBlock(
    offset: ReminderOffset?,
    onChange: (ReminderOffset?) -> Unit
) {
    // включено ли напоминание
    val enabled = offset != null
    // локальные состояния полей ввода
    var minutes by remember { mutableStateOf(offset?.minutes?.toString() ?: "0") }
    var hours by remember { mutableStateOf(offset?.hours?.toString() ?: "0") }
    var days by remember { mutableStateOf(offset?.days?.toString() ?: "0") }

    // Пересчитывает ReminderOffset при изменении любого поля.
    // Важно:
    // - если ввод некорректный → используем 0
    // - всегда возвращаем валидный объект
    fun update() {
        val m = minutes.toIntOrNull() ?: 0
        val h = hours.toIntOrNull() ?: 0
        val d = days.toIntOrNull() ?: 0
        onChange(ReminderOffset(d, h, m))
    }
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // показываем поля только если напоминание включено
        if (enabled) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // поле минут
                NumberField(value = minutes, label = stringResource(R.string.minute_short)) {
                    minutes = it
                    update()
                }
                // поле часов
                NumberField(value = hours, label = stringResource(R.string.hour_short)) {
                    hours = it
                    update()
                }
                // поле дней
                NumberField(value = days, label = stringResource(R.string.day_short)) {
                    days = it
                    update()
                }
            }
        }
    }
}
