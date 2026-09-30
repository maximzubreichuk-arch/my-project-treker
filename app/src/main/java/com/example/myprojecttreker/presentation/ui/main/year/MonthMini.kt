package com.example.myprojecttreker.presentation.ui.main.year

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.myprojecttreker.data.settings.SettingsManager
import com.example.myprojecttreker.data.local.DayResultEntity
import com.example.myprojecttreker.domain.Task
import com.example.myprojecttreker.presentation.ui.main.DayCell
import java.time.LocalDate
import java.time.YearMonth

/**
 * Мини-отображение месяца (используется на экране года).
 *
 * Показывает:
 * - название месяца
 * - сетку дней (как календарь)
 * - состояние каждого дня (через DayCell)
 */
@Composable
fun MonthMini(
    month: YearMonth,
    today: LocalDate,
    tasks: List<Task>,
    dayResults: List<DayResultEntity>,
    onDateClick: (LocalDate) -> Unit
) {

    // Первый день месяца
    val firstDay = month.atDay(1)

    // Количество дней в месяце
    val daysInMonth = month.lengthOfMonth()

    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val settings by settingsManager.settings.collectAsState()
    val firstDayOfWeek = ((firstDay.dayOfWeek.value - settings.firstDayOfWeek) + 7) % 7

    // Список дней с учётом "пустых" ячеек в начале
    val days = mutableListOf<LocalDate?>()

    // Добавляем пустые ячейки до первого дня месяца
    repeat(firstDayOfWeek) {
        days.add(null)
    }

    // Добавляем все дни месяца
    for (i in 1..daysInMonth) {
        days.add(month.atDay(i))
    }

    Column(
        modifier = Modifier.padding(4.dp)
    ) {


        // Название месяца
        Text(
            text = month.atDay(1).format(java.time.format.DateTimeFormatter.ofPattern("LLL", java.util.Locale.getDefault())),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(4.dp)
        )

        Column {

            // Количество строк (недель) в календаре
            val weeks = (days.size + 6) / 7 // округление вверх

            // Проходим по неделям
            for (weekIndex in 0 until weeks) {

                Row(modifier = Modifier.fillMaxWidth()) {

                    // В каждой неделе 7 дней
                    for (dayIndex in 0 until 7) {


                        // Индекс дня в общем списке
                        val index = weekIndex * 7 + dayIndex
                        val date = days.getOrNull(index)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                // делаем квадратные ячейки
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {

                            // Если это реальный день (не null)
                            if (date != null) {

                                // Отрисовываем день календаря
                                DayCell(
                                    date = date,
                                    today = today,
                                    tasks = tasks,
                                    dayResults = dayResults,
                                    onClick = { onDateClick(date) },
                                    // компактный режим (для YearScreen)
                                    isCompact = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}