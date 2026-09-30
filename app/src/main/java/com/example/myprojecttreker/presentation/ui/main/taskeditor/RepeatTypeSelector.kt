package com.example.myprojecttreker.presentation.ui.main.taskeditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.myprojecttreker.R
import com.example.myprojecttreker.domain.RepeatType

/**
 * Компактный выбор типа повторения.
 * Все варианты находятся в одном выпадающем списке, поэтому редактор
 * не раздувается на несколько строк.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatTypeSelector(
    repeatType: RepeatType,
    onChange: (RepeatType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val types = listOf(
        RepeatType.ONCE,
        RepeatType.DAILY,
        RepeatType.WEEKLY,
        RepeatType.MONTHLY,
        RepeatType.YEARLY,
        RepeatType.COURSE
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = repeatTypeTitle(repeatType),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text(stringResource(R.string.repeat)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(repeatTypeTitle(type)) },
                    onClick = {
                        expanded = false
                        onChange(type)
                    }
                )
            }
        }
    }
}

@Composable
private fun repeatTypeTitle(type: RepeatType): String = when (type) {
    RepeatType.ONCE -> stringResource(R.string.once)
    RepeatType.DAILY -> stringResource(R.string.daily)
    RepeatType.WEEKLY -> stringResource(R.string.weekly)
    RepeatType.MONTHLY -> stringResource(R.string.monthly)
    RepeatType.YEARLY -> stringResource(R.string.yearly)
    RepeatType.COURSE -> stringResource(R.string.course)
}
