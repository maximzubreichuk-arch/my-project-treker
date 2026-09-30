package com.example.myprojecttreker.presentation.ui.main.taskeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myprojecttreker.domain.DaysOfWeek
import java.time.DayOfWeek
import java.time.format.TextStyle

/**
 * Выбор дней недели в одну ровную строку из 7 одинаковых ячеек.
 * Подписи локализуются через текущую locale приложения.
 */
@Composable
fun WeeklySelector(
    selected: Set<DaysOfWeek>,
    onChange: (Set<DaysOfWeek>) -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DaysOfWeek.values().forEach { day ->
            val isSelected = day in selected
            val label = dayShortLabel(day, locale)

            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .clickable {
                        val newSet = selected.toMutableSet()
                        if (isSelected) newSet.remove(day) else newSet.add(day)
                        onChange(newSet)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

private fun dayShortLabel(day: DaysOfWeek, locale: java.util.Locale): String {
    val value = DayOfWeek.valueOf(day.name)
        .getDisplayName(TextStyle.SHORT, locale)
        .replace(".", "")
        .trim()
    return value.take(2).replaceFirstChar { it.titlecase(locale) }
}
