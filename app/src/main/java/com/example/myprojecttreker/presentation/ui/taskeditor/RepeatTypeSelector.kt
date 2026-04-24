package com.example.myprojecttreker.presentation.ui.taskeditor


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprojecttreker.domain.model.RepeatType

/**
 * Компонент выбора типа повторения задачи.
 *
 * Позволяет пользователю выбрать один из вариантов:
 * - одноразовая задача
 * - ежедневная
 * - еженедельная
 * - ежемесячная
 * - ежегодная
 * - курс (несколько дней подряд)
 */
@Composable
fun RepeatTypeSelector(
    repeatType: RepeatType,
    onChange: (RepeatType) -> Unit
) {
    // список всех доступных типов повторения
    val types = listOf(
        RepeatType.ONCE,
        RepeatType.DAILY,
        RepeatType.WEEKLY,
        RepeatType.MONTHLY,
        RepeatType.YEARLY,
        RepeatType.COURSE
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        // равномерно распределяем элементы по ширине
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // отображаем каждый тип как отдельный вариант выбора
        types.forEach { type ->
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // радиокнопка выбора типа повторения
                RadioButton(
                    modifier = Modifier.size(20.dp),
                    selected = repeatType == type,
                    onClick = {
                        // уведомляем внешний слой о смене типа
                        onChange(type)
                    }
                )
                // текстовое отображение названия типа
                Text(
                    text = type.name,
                    fontSize = 12.sp
                )
            }
        }
    }
}