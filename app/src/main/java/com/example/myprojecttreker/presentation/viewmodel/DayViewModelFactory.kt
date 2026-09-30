package com.example.myprojecttreker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myprojecttreker.data.local.DayResultDao
import com.example.myprojecttreker.domain.TaskRepository
import com.example.myprojecttreker.domain.usecase.GetTasksForDay
import com.example.myprojecttreker.data.subscription.SubscriptionManager


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
@Suppress("UNCHECKED_CAST")
class DayViewModelFactory(
    private val getTasksForDay: GetTasksForDay,
    private val repository: TaskRepository,
    private val dayResultDao: DayResultDao,
    private val subscriptionManager: SubscriptionManager
) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {

        // Проверяем, что запрашивается именно DayViewModel
        if (modelClass.isAssignableFrom(DayViewModel::class.java)) {

            // Создаём ViewModel с зависимостями
            return DayViewModel(
                getTasksForDay,
                repository,
                dayResultDao,
                subscriptionManager
            ) as T
        }
        // Ошибка, если запрошена неизвестная ViewModel
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
