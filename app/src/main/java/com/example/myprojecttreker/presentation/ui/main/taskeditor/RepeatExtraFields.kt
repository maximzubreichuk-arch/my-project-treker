package com.example.myprojecttreker.presentation.ui.main.taskeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.myprojecttreker.R
import com.example.myprojecttreker.domain.RepeatType

@Composable
fun RepeatExtraFields(
    state: TaskEditorState,
    onUpdate: (TaskEditorState) -> Unit
) {
    when (state.repeatType) {
        RepeatType.WEEKLY -> WeeklySelector(state.weeklyDays) { onUpdate(state.copy(weeklyDays = it)) }
        RepeatType.MONTHLY -> MonthDayField(
            day = state.dayOfMonth ?: state.date.dayOfMonth,
            modifier = Modifier,
            onChange = { onUpdate(state.copy(dayOfMonth = it)) }
        )
        RepeatType.YEARLY -> Text(stringResource(R.string.repeat_date, state.date.dayOfMonth, state.date.monthValue))
        RepeatType.COURSE -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CourseDaysField(days = state.courseDays.coerceIn(1, 99), modifier = Modifier.weight(1f)) {
                onUpdate(state.copy(courseDays = it.coerceIn(1, 99)))
            }
            Text(stringResource(R.string.days_short), modifier = Modifier.weight(1f))
        }
        else -> Unit
    }
}
