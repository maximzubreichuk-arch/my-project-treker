package com.example.myprojecttreker.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myprojecttreker.data.local.dao.DayResultDao
import com.example.myprojecttreker.data.local.entity.DayResultEntity
import com.example.myprojecttreker.data.mapper.toJson
import com.example.myprojecttreker.domain.model.Task
import com.example.myprojecttreker.domain.repository.TaskRepository
import com.example.myprojecttreker.domain.usecase.GetTasksForDay
import com.example.myprojecttreker.presentation.viewmodel.DayIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.collections.plus

/**
 * ViewModel для экрана дня.
 *
 * Отвечает за:
 * - хранение состояния UI
 * - обработку пользовательских действий (Intent)
 * - загрузку задач и результатов выполнения
 */
@RequiresApi(Build.VERSION_CODES.O)
class DayViewModel(
    private val getTasksForDay: GetTasksForDay,
    private val taskRepository: TaskRepository,
    private val dayResultDao: DayResultDao
) : ViewModel() {

    // Начальная дата (при запуске приложения)
    private val initialDate = LocalDate.now()


    // Сегодняшняя дата (может использоваться для логики UI)
    private val today = LocalDate.now()

    // Текущий месяц (для экранов Month/Year)
    private var currentMonth = YearMonth.now()

    // Текущая выбранная дата
    private var currentDate = initialDate

    // Внутреннее состояние UI
    private val _uiState =
        MutableStateFlow<DayUiState>(DayUiState.Loading(initialDate))

    // Публичное состояние UI (immutable)
    val uiState: StateFlow<DayUiState> = _uiState

    init {
        // Загружаем задачи при создании ViewModel
        loadTasksFor(initialDate)
    }

    /**
     * Возвращает задачу по ID из текущего состояния
     */
    fun getTaskById(id: Long): Task? {
        val current = _uiState.value
        return if (current is DayUiState.Content) {
            current.tasks.find { it.id == id }
        } else null
    }

    /**
     * Главная точка обработки пользовательских действий (Intent)
     */
    fun processIntent(intent: DayIntent) {
        when (intent) {
            // Инициализация
            DayIntent.Init ->
                loadTasksFor(currentDate)

            // Переход на следующий день
            DayIntent.NextDay ->
                nextDay()

            // Переход на предыдущий день
            DayIntent.PreviousDay ->
                previousDay()

            // Повторная загрузка
            DayIntent.Retry ->
                loadTasksFor(currentDate)

            // Изменение состояния задачи (выполнена/не выполнена)
            is DayIntent.TaskChecked ->
                updateTaskDone(intent.task, intent.isDone)

            // Добавление новой задачи
            is DayIntent.AddTask ->
                addTask(intent.task)

            // Удаление задачи
            is DayIntent.DeleteTask ->
                deleteTask(intent.task)

            // Обновление задачи
            is DayIntent.UpdateTask ->
                updateTask(intent.task)

            // Раскрытие/сворачивание задачи
            is DayIntent.ToggleExpand ->
                toggleExpand(intent.task)

            // Не реализовано
            DayIntent.UndoDelete -> {}

            // Переключение выполнения задачи на конкретный день/время
            is DayIntent.ToggleDayResult -> {
                viewModelScope.launch {

                    // Получаем задачу из текущего состояния (если есть)
                    val task = (_uiState.value as? DayUiState.Content)
                        ?.tasks?.find { it.id == intent.taskId }

                    // Сохраняем результат выполнения задачи в БД
                    dayResultDao.insert(
                        DayResultEntity(
                            taskId = intent.taskId,
                            date = intent.date,
                            time = intent.time,
                            isDone = intent.isDone,
                            subtasksJson = intent.subtasks.toJson()
                        )
                    )
                    val current = _uiState.value
                    if (current is DayUiState.Content) {

                        // Обновляем список результатов:
                        // удаляем старую запись и добавляем новую
                        val updatedResults = current.dayResults
                            .filterNot {
                                it.taskId == intent.taskId &&
                                        it.date == intent.date&&
                                        it.time == intent.time
                            } + DayResultEntity(
                            taskId = intent.taskId,
                            date = intent.date,
                            time = intent.time,
                            isDone = intent.isDone,
                            subtasksJson = intent.subtasks.toJson()
                        )
                        _uiState.value = current.copy(
                            dayResults = updatedResults
                        )
                    }
                }
            }

            // Переход на следующий месяц
            is DayIntent.NextMonth -> {
                currentMonth = currentMonth.plusMonths(1)
                loadTasksFor(currentMonth.atDay(1))
            }

            // Переход на предыдущий месяц
            is DayIntent.PreviousMonth -> {
                currentMonth = currentMonth.minusMonths(1)
                loadTasksFor(currentMonth.atDay(1))
            }

            // Выбор конкретной даты
            is DayIntent.SelectDate -> {
                loadTasksFor(intent.date)
            }

        }
    }

    /**
     * Загружает задачи и результаты выполнения для выбранной даты
     */
    private fun loadTasksFor(date: LocalDate) {

        // Обновляем текущую дату
        currentDate = date
        viewModelScope.launch {

            // Показываем состояние загрузки
            _uiState.value =
                DayUiState.Loading(date)

            // Получаем задачи через UseCase
            val tasks =
                getTasksForDay(date)
                    // Сортируем: сначала без времени, потом по времени
                    .sortedWith(
                        compareBy<Task> { it.time == null }
                            .thenBy { it.time }
                    )


            // Определяем период (весь месяц)
            val start = currentDate.withDayOfMonth(1)
            val end = currentDate.withDayOfMonth(currentDate.lengthOfMonth())

            // Получаем результаты выполнения задач за месяц
            val dayResults = dayResultDao.getForPeriod(start, end)

            // Обновляем UI состояние
            _uiState.value =
                if (tasks.isEmpty())
                    DayUiState.Empty(date)
                else
                    DayUiState.Content(
                        date,
                        tasks,
                        dayResults
                    )

        }
    }

    /**
     * Переход к следующему дню
     */
    private fun nextDay() {
        loadTasksFor(
            currentDate.plusDays(1)
        )
    }

    /**
     * Переход к предыдущему дню
     */
    private fun previousDay() {
        loadTasksFor(
            currentDate.minusDays(1)
        )
    }

    /**
     * Добавляет новую задачу
     */
    private fun addTask(task: Task) {
        viewModelScope.launch {

            // Сохраняем задачу
            taskRepository.insertTask(task)

            // Перезагружаем список
            loadTasksFor(currentDate)
        }
    }

    /**
     * Удаляет задачу
     */
    private fun deleteTask(task: Task) {
        viewModelScope.launch {
            // Удаляем задачу из БД
            taskRepository.deleteTask(task)
            // Обновляем экран
            loadTasksFor(currentDate)
        }
    }

    /**
     * Обновляет задачу
     */
    private fun updateTask(task: Task) {
        viewModelScope.launch {
            val current = _uiState.value

            // Обновляем задачу в БД
            taskRepository.updateTask(task)
            if (current is DayUiState.Content) {

                // Обновляем список задач в UI (сохраняем состояние expanded)
                val updatedTasks = current.tasks.map {
                    if (it.id == task.id)
                        task.copy(
                            isExpanded = it.isExpanded
                        )
                    else it
                }
                _uiState.value =
                    DayUiState.Content(
                        current.date,
                        updatedTasks,
                        current.dayResults
                    )
            }
        }
    }

    /**
     * Массово обновляет статус задачи и всех подзадач
     */
    private fun updateTaskDone(
        task: Task,
        isDone: Boolean
    ) {
        viewModelScope.launch {

            // Обновляем задачу и все её подзадачи
            val updated =
                task.copy(
                    isDone = isDone,
                    subtasks =
                        task.subtasks.map {
                            it.copy(isDone = isDone)
                        }
                )
            taskRepository.updateTask(updated)

            // Перезагружаем данные
            loadTasksFor(currentDate)
        }
    }

    /**
     * Переключает состояние раскрытия задачи (expand/collapse)
     */
    private fun toggleExpand(task: Task) {
        val current = _uiState.value
        if (current is DayUiState.Content) {
            val updated =
                current.tasks.map {
                    if (it.id == task.id)
                        it.copy(
                            isExpanded =
                                !it.isExpanded
                        )
                    else it
                }
            _uiState.value =
                DayUiState.Content(
                    current.date,
                    updated,
                    dayResults = current.dayResults
                )
        }
    }

    /**
     * Возвращает все задачи
     */
    suspend fun getAllTasks(): List<Task> {
        return taskRepository.getAllTasks()
    }

    /**
     * Возвращает результаты за год
     */
    suspend fun getDayResultsForYear(year: Int): List<DayResultEntity> {
        val start = LocalDate.of(year, 1, 1)
        val end = LocalDate.of(year, 12, 31)
        return dayResultDao.getForPeriod(start, end)
    }

    /**
     * Возвращает результаты за произвольный период
     */
    suspend fun getDayResultsForPeriod(
        start: LocalDate,
        end: LocalDate
    ): List<DayResultEntity> {
        return dayResultDao.getForPeriod(start, end)
    }

    /**
     * Возвращает все задачи за месяц (сейчас без фильтрации)
     */
    suspend fun getTasksForMonth(month: YearMonth): List<Task> {
        return taskRepository.getAllTasks()
    }
}