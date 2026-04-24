package com.example.myprojecttreker.presentation.ui.calendar


import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
 * Экран календаря месяца.
 *
 * Отвечает за:
 * - отображение дней текущего месяца в виде сетки
 * - загрузку задач и результатов за выбранный месяц
 * - навигацию между месяцами (свайпы)
 * - обработку клика по дню
 *
 * Особенности:
 * - формирует календарную сетку с учётом смещения первого дня
 * - использует produceState для асинхронной загрузки данных
 * - не содержит бизнес-логики (только orchestration UI)
 */
@Composable
fun MonthScreen(
    currentDate: LocalDate,
    viewModel: DayViewModel,
    onDateClick: (LocalDate) -> Unit
) {
    // Текущий отображаемый месяц (может меняться свайпом)
    var currentMonth by remember {
        mutableStateOf(YearMonth.from(currentDate))
    }
    // Текущая дата (сегодня)
    val today = LocalDate.now()

    // Первый день месяца
    val firstDay = currentMonth.atDay(1)

    // Количество дней в месяце
    val daysInMonth = currentMonth.lengthOfMonth()

    // День недели первого числа (нужен для смещения)
    val firstDayOfWeek = firstDay.dayOfWeek.value

    // Формируем список дней для отображения в сетке
    val days = remember(currentMonth) {
        buildList<LocalDate?> {

            // Добавляем пустые ячейки до первого дня месяца
            repeat(firstDayOfWeek - 1) {
                add(null)
            }

            // Добавляем все дни текущего месяца
            for (i in 1..daysInMonth) {
                add(currentMonth.atDay(i))
            }
        }
    }
    // Загружаем все задачи (используются для расчёта состояния дней)
    val allTasks by produceState<List<Task>>(
        initialValue = emptyList(),
        key1 = currentMonth
    ) {
        value = viewModel.getAllTasks()
    }
    // Загружаем результаты выполнения задач за месяц
    val monthResults by produceState<List<DayResultEntity>>(
        initialValue = emptyList(),
        key1 = currentMonth
    ) {
        val start = currentMonth.atDay(1)
        val end = currentMonth.atEndOfMonth()
        value = viewModel.getDayResultsForPeriod(start, end)
    }
    // Накопление значения свайпа для определения направления
    var totalDrag by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Обработка горизонтальных свайпов для переключения месяцев
            .pointerInput(Unit) {
                detectHorizontalDragGestures(

                    // Сбрасываем накопление при начале свайпа
                    onDragStart = { totalDrag = 0f },

                    // Накопление смещения
                    onHorizontalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    },
                    // Определяем направление свайпа
                    onDragEnd = {
                        when {
                            // Свайп влево → следующий месяц
                            totalDrag < -100 -> currentMonth = currentMonth.plusMonths(1)
                            // Свайп вправо → предыдущий месяц
                            totalDrag > 100 -> currentMonth = currentMonth.minusMonths(1)
                        }
                    }
                )
            }
    ) {
        // Заголовок: месяц + год
        Text(
            text = "${currentMonth.month} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = days,

                // Ключ для стабильности элементов (важно для Compose)
                key = { index, date ->
                    date?.toEpochDay() ?: -index.toLong()
                }
            ) { _, date ->

                if (date == null) {
                    // Пустая ячейка (смещение начала месяца)
                    Box(modifier = Modifier.aspectRatio(1f))
                } else {
                    // Ячейка конкретного дня
                    DayCell(
                        date = date,
                        today = today,
                        tasks = allTasks,
                        dayResults = monthResults,
                        // Обработка клика по дню
                        onClick = { onDateClick(date) },
                        // В обычном режиме (не компакт)
                        isCompact = false


                    )
                }
            }
        }
    }
}