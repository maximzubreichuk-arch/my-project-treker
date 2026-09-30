package com.example.myprojecttreker.presentation.ui.main.taskeditor

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.myprojecttreker.presentation.ui.taskicon.TaskIconCatalog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskIconPicker(selectedId: String, onSelected: (String) -> Unit) {
    FlowRow {
        TaskIconCatalog.options.forEach { option ->
            Icon(
                option.icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
                    .border(
                        if (option.id == selectedId) 2.dp else 0.dp,
                        if (option.id == selectedId) MaterialTheme.colorScheme.primary else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelected(option.id) }
                    .padding(6.dp)
            )
        }
    }
}
