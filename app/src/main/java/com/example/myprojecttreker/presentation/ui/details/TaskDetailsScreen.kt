package com.example.myprojecttreker.presentation.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myprojecttreker.R
import com.example.myprojecttreker.domain.RepeatType
import com.example.myprojecttreker.domain.Task
import com.example.myprojecttreker.presentation.ui.main.TaskIconView
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(task: Task, navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(task.title) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        TaskIconView(task.iconId)
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(task.title, style = MaterialTheme.typography.titleLarge)
                            if (task.description.isNotBlank()) Text(task.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            item { DetailRow(stringResource(R.string.date), task.date.toString()) }
            item { DetailRow(stringResource(R.string.time), task.time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: stringResource(R.string.no_time)) }
            item { DetailRow(stringResource(R.string.repeat), repeatLabel(task.repeatType)) }
            if (task.reminderOffset != null) {
                item { DetailRow(stringResource(R.string.reminder), "${task.reminderOffset.minutes} мин / ${task.reminderOffset.hours} ч / ${task.reminderOffset.days} д") }
            }
            if (task.extraTimes.isNotEmpty()) {
                item { DetailRow(stringResource(R.string.extra_times), task.extraTimes.joinToString(", ") { it.toString() }) }
            }
            if (task.subtasks.isNotEmpty()) {
                item { Text(stringResource(R.string.subtasks), style = MaterialTheme.typography.titleMedium) }
                items(task.subtasks) { sub ->
                    Text("${if (sub.isDone) "☑" else "☐"} ${sub.title}")
                }
            }
            item {
                Button(onClick = { navController.navigate("editor/${task.id}") }, Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Edit, null)
                    Text(stringResource(R.string.edit), Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp)) {
            Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun repeatLabel(type: RepeatType): String = when (type) {
    RepeatType.ONCE -> stringResource(R.string.once)
    RepeatType.DAILY -> stringResource(R.string.daily)
    RepeatType.WEEKLY -> stringResource(R.string.weekly)
    RepeatType.MONTHLY -> stringResource(R.string.monthly)
    RepeatType.YEARLY -> stringResource(R.string.yearly)
    RepeatType.COURSE -> stringResource(R.string.course)
}
