package com.example.myprojecttreker.presentation.ui.calendar

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.myprojecttreker.data.local.entity.DayResultEntity
import com.example.myprojecttreker.domain.model.Task
import com.example.myprojecttreker.presentation.viewmodel.DayViewModel
import java.time.LocalDate
import java.time.YearMonth

/**
 * Экран года.
 *
 * Показывает:
 * - текущий год
 * - сетку из 12 месяцев
 * - позволяет переключаться между годами свайпом
 */
@Composable
fun YearScreen(
    currentDate: LocalDate,
    dayResults: List<DayResultEntity>,
    viewModel: DayViewModel,
    onDateClick: (LocalDate) -> Unit
) {

    // Текущий выбранный год (можно менять свайпом)
    var currentYear by remember {
        mutableStateOf(currentDate.year)
    }

    // Сегодняшняя дата (для подсветки)
    val today = LocalDate.now()

    // Ключ для перезапуска загрузки задач при смене года
    val trigger = currentYear // 👈 ключ

    /**
     * Загружаем все задачи.
     * Перезапускается при изменении trigger (т.е. года)
     */
    val allTasks by produceState<List<Task>>(
        initialValue = emptyList(),
        key1 = trigger
    ) {
        value = viewModel.getAllTasks()
    }

    /**
     * Загружаем результаты выполнения задач за выбранный год
     */
    val yearResults by produceState<List<DayResultEntity>>(
        initialValue = emptyList(),
        key1 = currentYear
    ) {
        value = viewModel.getDayResultsForYear(currentYear)
    }

    // Суммарное смещение пальца по горизонтали (для свайпа)
    var totalDrag by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()

            // Обработка свайпа для переключения года
            .pointerInput(Unit) {
                detectHorizontalDragGestures(

                    // Начало свайпа — обнуляем накопление
                    onDragStart = { totalDrag = 0f },

                    // Во время свайпа накапливаем смещение
                    onHorizontalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    },

                    // Конец свайпа — определяем направление
                    onDragEnd = {

                        // Свайп влево → следующий год
                        if (totalDrag < -100) {
                            currentYear++
                        } else if (totalDrag > 100) {
                            currentYear--
                        }
                    }
                )
            }
    ) {

        // Заголовок года
        Text(
            text = currentYear.toString(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            // 3 месяца в ряд
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f)
        ) {

            // Отрисовываем 12 месяцев
            items(12) { monthIndex ->

                // Создаём объект месяца
                val month = YearMonth.of(currentYear, monthIndex + 1)

                // Мини-календарь месяца
                MonthMini(
                    month = month,
                    today = today,
                    tasks = allTasks,
                    dayResults = yearResults,
                    onDateClick = onDateClick
                )
            }
        }
    }
}