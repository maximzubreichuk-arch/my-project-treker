package com.example.myprojecttreker.presentation.ui.main

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myprojecttreker.presentation.ui.taskicon.TaskIconCatalog

@Composable
fun TaskIconView(iconId: String) {
    Icon(TaskIconCatalog.find(iconId).icon, contentDescription = null, modifier = Modifier)
}
