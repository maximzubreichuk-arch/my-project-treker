package com.example.myprojecttreker.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myprojecttreker.data.local.dao.DayResultDao
import com.example.myprojecttreker.domain.repository.TaskRepository
import com.example.myprojecttreker.domain.usecase.GetTasksForDay

/**
 * Фабрика для создания DayViewModel.
 *
 * Используется для передачи зависимостей во ViewModel:
 * - use case (GetTasksForDay)
 * - repository
 * - dao (DayResultDao)
 *
 * Нужна, потому что ViewModelProvider по умолчанию
 * не умеет создавать ViewModel с параметрами.
 */
@RequiresApi(Build.VERSION_CODES.O)
class DayViewModelFactory(
    private val getTasksForDay: GetTasksForDay,
    private val repository: TaskRepository,
    private val dayResultDao: DayResultDao
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Проверяем, что запрашивается именно DayViewModel
        if (modelClass.isAssignableFrom(DayViewModel::class.java)) {

            // Создаём ViewModel с зависимостями
            return DayViewModel(
                getTasksForDay,
                repository,
                dayResultDao
            ) as T
        }
        // Ошибка, если запрошена неизвестная ViewModel
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}