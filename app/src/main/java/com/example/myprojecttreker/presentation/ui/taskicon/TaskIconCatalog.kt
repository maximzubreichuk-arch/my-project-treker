package com.example.myprojecttreker.presentation.ui.taskicon
import com.example.myprojecttreker.domain.model.TaskIconId

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myprojecttreker.R

object TaskIconCatalog {
    val options = listOf(
        TaskIconOption(TaskIconId.DEFAULT, Icons.AutoMirrored.Filled.Assignment, R.string.icon_default),
        TaskIconOption(TaskIconId.WORK, Icons.Filled.Work, R.string.icon_work),
        TaskIconOption(TaskIconId.LAPTOP, Icons.Filled.LaptopMac, R.string.icon_laptop),
        TaskIconOption(TaskIconId.BRIEFCASE, Icons.Filled.BusinessCenter, R.string.icon_briefcase),
        TaskIconOption(TaskIconId.SHOVEL, Icons.Filled.Build, R.string.icon_shovel),
        TaskIconOption(TaskIconId.HEALTH, Icons.Filled.Favorite, R.string.icon_health),
        TaskIconOption(TaskIconId.MEDICATION, Icons.Filled.LocalHospital, R.string.icon_medication),
        TaskIconOption(TaskIconId.SPORT, Icons.Filled.FitnessCenter, R.string.icon_sport),
        TaskIconOption(TaskIconId.SHOPPING, Icons.Filled.ShoppingCart, R.string.icon_shopping),
        TaskIconOption(TaskIconId.HOME, Icons.Filled.Home, R.string.icon_home),
        TaskIconOption(TaskIconId.STUDY, Icons.Filled.School, R.string.icon_study),
        TaskIconOption(TaskIconId.BOOK, Icons.Filled.MenuBook, R.string.icon_book),
        TaskIconOption(TaskIconId.TRAVEL, Icons.Filled.Flight, R.string.icon_travel),
        TaskIconOption(TaskIconId.FOOD, Icons.Filled.Restaurant, R.string.icon_food),
        TaskIconOption(TaskIconId.WATER, Icons.Filled.LocalDrink, R.string.icon_water),
        TaskIconOption(TaskIconId.FINANCE, Icons.Filled.AccountBalance, R.string.icon_finance),
        TaskIconOption(TaskIconId.MUSIC, Icons.Filled.MusicNote, R.string.icon_music),
        TaskIconOption(TaskIconId.FAMILY, Icons.Filled.People, R.string.icon_family)
    )

    fun find(id: String): TaskIconOption = options.firstOrNull { it.id == id } ?: options.first()
}
