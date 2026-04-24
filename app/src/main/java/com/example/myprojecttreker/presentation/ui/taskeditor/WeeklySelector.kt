package com.example.myprojecttreker.presentation.ui.taskeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprojecttreker.domain.model.DaysOfWeek

/**
 * Компонент выбора дней недели для повторяющейся задачи (WEEKLY).
 *
 * Отвечает за:
 * - отображение всех дней недели
 * - выделение выбранных дней
 * - добавление/удаление дня из выбранного набора
 *
 * Особенности:
 * - работает с immutable Set (обновление через копию)
 * - позволяет выбирать несколько дней одновременно
 */
@Composable
fun WeeklySelector(
    selected: Set<DaysOfWeek>,
    onChange: (Set<DaysOfWeek>) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        // Расстояние между элементами (днями)
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Проходим по всем дням недели
        DaysOfWeek.values().forEach { day ->
            // Проверяем, выбран ли текущий день
            val isSelected = selected.contains(day)
            Box(
                modifier = Modifier
                    // Размер ячейки дня
                    .size(width = 40.dp, height = 32.dp)
                    // Скругление углов
                    .clip(RoundedCornerShape(8.dp))

                    // Цвет зависит от состояния выбора
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    // Обработка выбора/снятия выбора дня
                    .clickable {
                        // Создаём копию множества (immutable подход)
                        val newSet = selected.toMutableSet()
                        // Убираем день, если уже выбран
                        if (isSelected)
                            newSet.remove(day)
                        else
                        // Добавляем день, если не выбран
                            newSet.add(day)
                        // Передаём обновлённое состояние наружу
                        onChange(newSet)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    // Показываем сокращённое название дня (2 символа)
                    text = day.name.take(2),
                    fontSize = 12.sp,
                    // Цвет текста зависит от состояния выбора
                    color =
                        if (isSelected)
                            Color.White
                        else
                            MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}