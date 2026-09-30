package com.example.myprojecttreker.presentation.ui.main.taskeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.myprojecttreker.R
import java.time.LocalTime
import com.example.myprojecttreker.data.settings.SettingsManager
import androidx.compose.runtime.collectAsState
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExtraTimesBlock(
    times: List<LocalTime>,
    canAddMore: Boolean,
    onLimitReached: () -> Unit,
    onChange: (List<LocalTime>) -> Unit
) {
    var editIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val settings by settingsManager.settings.collectAsState()
    var addNew by remember { mutableStateOf(false) }
    Column {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            times.forEachIndexed { index, time ->
                OutlinedButton(onClick = { editIndex = index }) {
                    Text(time.format(DateTimeFormatter.ofPattern(if (settings.timeFormat24Hour) "HH:mm" else "hh:mm a")))
                }
            }
            OutlinedButton(onClick = { if (canAddMore) addNew = true else onLimitReached() }) { Text("+") }
        }
    }
    editIndex?.let { index ->
        val current = times[index]
        val picker = rememberTimePickerState(initialHour = current.hour, initialMinute = current.minute, is24Hour = settings.timeFormat24Hour)
        AlertDialog(
            onDismissRequest = { editIndex = null },
            confirmButton = {
                TextButton(onClick = {
                    val updated = times.toMutableList(); updated[index] = LocalTime.of(picker.hour, picker.minute)
                    onChange(updated.sorted()); editIndex = null
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    val updated = times.toMutableList(); updated.removeAt(index); onChange(updated); editIndex = null
                }) { Text(stringResource(R.string.remove_time)) }
            },
            text = { TimePicker(state = picker) }
        )
    }
    if (addNew) {
        val now = LocalTime.now()
        val picker = rememberTimePickerState(initialHour = now.hour, initialMinute = now.minute, is24Hour = settings.timeFormat24Hour)
        AlertDialog(
            onDismissRequest = { addNew = false },
            confirmButton = {
                TextButton(onClick = {
                    onChange((times + LocalTime.of(picker.hour, picker.minute)).distinct().sorted()); addNew = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = { TextButton(onClick = { addNew = false }) { Text(stringResource(R.string.cancel)) } },
            text = { TimePicker(state = picker) }
        )
    }
}
