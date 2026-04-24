package com.example.myprojecttreker.presentation.ui.taskeditor

import androidx.compose.runtime.Composable
import com.example.myprojecttreker.domain.model.RepeatType

/**
 * Дополнительные поля в зависимости от типа повторения задачи.
 *
 * Используется в редакторе задачи для отображения
 * специфичных настроек:
 * - WEEKLY → выбор дней недели
 * - COURSE → (пока нет дополнительных настроек)
 *
 * Важно:
 * - UI динамически меняется в зависимости от repeatType
 */
@Composable
fun RepeatExtraFields(
    state: TaskEditorState,
    onUpdate: (TaskEditorState) -> Unit
) {
    when (state.repeatType) {
        // Повтор по дням недели
        RepeatType.WEEKLY -> {
            WeeklySelector(
                selected = state.weeklyDays
            ) {
                // обновляем состояние с новыми выбранными днями
                onUpdate(
                    state.copy(
                        weeklyDays = it
                    )
                )
            }
        }
        // Курс (несколько дней подряд)
        RepeatType.COURSE -> {
        }
        // Для остальных типов повторения ничего не отображаем
        else -> {}
    }
}

