package com.example.myprojecttreker.presentation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprojecttreker.data.local.entity.DayResultEntity
import com.example.myprojecttreker.domain.model.Task
import com.example.myprojecttreker.presentation.ui.utils.calculateDayState
import com.example.myprojecttreker.presentation.ui.calendar.DayVisualState
import java.time.LocalDate

/**
 * Ячейка одного дня в календаре.
 *
 * Отвечает за:
 * - отображение даты
 * - визуальное состояние (цвет)
 * - обработку клика
 */
@Composable
fun DayCell(
    date: LocalDate,
    today: LocalDate,
    tasks: List<Task>,
    dayResults: List<DayResultEntity>,
    onClick: () -> Unit,
    // ключ: компактный режим (год) или обычный (месяц)
    isCompact: Boolean = false
) {

    // Определяем состояние дня (completed / today / future / past / empty)
    val state =
        calculateDayState(
            date = date,
            today = today,
            tasks = tasks,
            dayResults = dayResults
        )

    // Выбираем цвет фона в зависимости от состояния
    val backgroundColor = when (state) {
        DayVisualState.Completed -> Color(0xFF81C784)
        DayVisualState.Today -> Color(0xFF42A5F5)
        DayVisualState.Future -> Color(0xFFFFB74D)
        DayVisualState.Past -> Color(0xFF424242)
        DayVisualState.Empty -> Color.Transparent
    }

    // Цвет текста:
    // для тёмного фона используем белый, иначе чёрны
    val textColor =
        if (backgroundColor == Color(0xFF424242))
            Color.White
        else
            Color.Black

    Box(
        modifier = Modifier
            .then(
                if (isCompact)
                    Modifier.fillMaxSize().padding(1.dp)
                else
                    Modifier
                        .fillMaxWidth()
                        // квадратная ячейка
                        .aspectRatio(1f)
                        .padding(4.dp)
            )
            // Обработка клика по дню
            .clickable { onClick() }

            // Фон с закруглением
            .background(
                backgroundColor,
                shape = RoundedCornerShape(
                    if (isCompact) 4.dp else 8.dp
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Отображаем номер дня
        Text(
            text = date.dayOfMonth.toString(),

            // Размер текста зависит от режима
            fontSize = if (isCompact) 10.sp else 14.sp,
            color = textColor
        )
    }
}
