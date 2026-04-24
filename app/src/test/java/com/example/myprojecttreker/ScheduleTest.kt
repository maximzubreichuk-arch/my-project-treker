package com.example.myprojecttreker
import com.example.myprojecttreker.domain.model.Task
import com.example.myprojecttreker.domain.repository.TaskRepository
import kotlinx.coroutines.delay
import java.time.LocalDate

class ScheduleTest {

    //  @Test
//    fun onceSchedule_activeOnlyOnExactDate() {
//        val schedule = Schedule(
//            repeatType = RepeatType.ONCE,
//            time = LocalTime.NOON,
//            date = LocalDate.of(2026, 1, 15)
//        )
//
//        val result = schedule.isActiveOn(LocalDate.of(2026, 1, 15))
//
//        assertTrue(result)
//    }
//
//    @Test
//    fun onceSchedule_inactiveOnOtherDate() {
//        val schedule = Schedule(
//            repeatType = RepeatType.ONCE,
//            time = LocalTime.NOON,
//            date = LocalDate.of(2026, 1, 15)
//        )
//
//        val result = schedule.isActiveOn(LocalDate.of(2026, 1, 16))
//
//        assertFalse(result)
//    }
    //////////////////////////
//    @Test
//    fun weeklySchedule_activeOnMatchingDayOfWeek() {
//        val schedule = Schedule(
//            repeatType = RepeatType.WEEKLY,
//            daysOfWeek = setOf(
//                DayOfWeek.TUESDAY,
//                DayOfWeek.THURSDAY
//            ),
//            time = LocalTime.of(10, 0)
//        )
//
//        // 2026-01-13 — это вторник
//        val dateToCheck = LocalDate.of(2026, 1, 13)
//
//        val result = schedule.isActiveOn(dateToCheck)
//
//        assertTrue(result)
//    }
//    @Test
//    fun weeklySchedule_notActiveOnNonMatchingDayOfWeek() {
//        val schedule = Schedule(
//            repeatType = RepeatType.WEEKLY,
//            daysOfWeek = setOf(
//                DayOfWeek.TUESDAY,
//                DayOfWeek.THURSDAY
//            ),
//            time = LocalTime.of(10, 0)
//        )
//
//        // 2026-01-14 — это среда
//        val dateToCheck = LocalDate.of(2026, 1, 14)
//
//        val result = schedule.isActiveOn(dateToCheck)
//
//        assertFalse(result)
//    }

//    @Test
//    fun weeklySchedule_activeOnIncludedDay() {
//        val schedule = Schedule(
//            repeatType = RepeatType.WEEKLY,
//            daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
//            time = LocalTime.NOON
//        )
//
//        val result = schedule.isActiveOn(LocalDate.of(2026, 1, 14)) // среда
//
//        assertTrue(result)
//    }
//    @Test
//    fun weeklySchedule_notActiveOnOtherDay() {
//        val schedule = Schedule(
//            repeatType = RepeatType.WEEKLY,
//            daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
//            time = LocalTime.NOON
//        )
//
//        val result = schedule.isActiveOn(LocalDate.of(2026, 1, 15)) // четверг
//
//        assertFalse(result)
//    }
//    @Test
//    fun weeklySchedule_emptyDays_isNeverActive() {
//        val schedule = Schedule(
//            repeatType = RepeatType.WEEKLY,
//            daysOfWeek = emptySet(),
//            time = LocalTime.NOON
//        )
//
//        val result = schedule.isActiveOn(LocalDate.of(2026, 1, 12))
//
//        assertFalse(result)
//    }
/////////////////////////

    //  @Test
//  fun courseSchedule_activeInsideCourse() {
//      val schedule = Schedule(
//          repeatType = RepeatType.COURSE,
//          startDate = LocalDate.of(2026, 2, 10),
//          time = LocalTime.NOON,
//          durationDays = 5
//      )
//
//      val result = schedule.isActiveOn(LocalDate.of(2026, 2, 12))
//
//      assertTrue(result)
//  }
//
//    @Test
//    fun courseSchedule_notActiveBeforeStart() {
//        val schedule = Schedule(
//            repeatType = RepeatType.COURSE,
//            startDate = LocalDate.of(2026, 2, 10),
//            time = LocalTime.NOON,
//            durationDays = 5
//        )
//
//        val result = schedule.isActiveOn(LocalDate.of(2026, 2, 9))
//
//        assertFalse(result)
//    }
//
//    @Test
//    fun courseSchedule_notActiveAfterEnd() {
//        val schedule = Schedule(
//            repeatType = RepeatType.COURSE,
//            startDate = LocalDate.of(2026, 2, 10),
//            time = LocalTime.NOON,
//            durationDays = 5
//        )
//
//        val result = schedule.isActiveOn(LocalDate.of(2026, 2, 16))
//
//        assertFalse(result)
//    }
//}
//    class FakeTaskRepository(
//        private val tasks: MutableList<Task> = mutableListOf(),
//        private val throwError: Boolean = false
//    ) : TaskRepository {
//
//        override suspend fun getAllTasks(): List<Task> {
//            if (throwError) error("Test error")
//            delay(100) // имитация сети
//            return tasks
//        }
//
//        override suspend fun updateTask(task: Task) {
//            val index = tasks.indexOfFirst { it == task }
//            if (index != -1) {
//                tasks[index] = task
//            }
//        }
//    }


//    @Test
//    fun whenNoTasksForDay_thenStateIsEmpty() {
//        val titleTask = "Test task"
//        val isEnabled = true
//        val supportEnabled = false
//        Schedule(
//          repeatType = RepeatType.COURSE,
//          startDate = LocalDate.of(2026, 2, 10),
//          time = LocalTime.NOON,
//          durationDays = 5
//      )
//        val isDone = false
//        // GIVEN
//        val repository = FakeTaskRepository(tasks = emptyList())
//        val useCase = GetTasksForDay(repository)
//        val viewModel = DayViewModel(
//            useCase,repository)
//
//        val date = LocalDate.of(2024, 1, 1)
//
//        // WHEN
//        viewModel.loadTasksFor(date)
//
//        // THEN
//        val state = viewModel.uiState
//        assertTrue(state is DayUiState.Empty)
//    }


//@Test
//fun whenTasksExist_thenStateIsContent() {
//
//    // GIVEN
//    val schedule = Schedule(
//        repeatType = RepeatType.COURSE,
//        startDate = LocalDate.of(2024, 1, 1),
//        time = LocalTime.NOON,
//        durationDays = 5
//    )
//
//    val task = Task(
//        titleTask = "Test task",
//        isEnabled = true,
//        supportEnabled = false,
//        schedule = schedule,
//        isDone = false
//    )
//
//    val repository = FakeTaskRepository(
//        tasks = listOf(task)
//    )
//
//    val useCase = GetTasksForDay(repository)
//
//    val viewModel = DayViewModel(
//        getTasksForDay = useCase,
//        taskRepository = repository
//    )
//
//    val date = LocalDate.of(2024, 1, 1)
//
//    // WHEN
//    viewModel.loadTasksFor(date)
//
//    // THEN
//    val state = viewModel.uiState
//
//    assertTrue(state is DayUiState.Content)
//}


//@Test
//fun whenRepositoryThrowsError_thenStateIsError() {
//
//    // GIVEN
//    val repository = FakeTaskRepository(
//        throwError = true
//    )
//
//    val useCase = GetTasksForDay(repository)
//    val viewModel = DayViewModel(
//        getTasksForDay = useCase,
//        taskRepository = repository
//    )
//
//    val date = LocalDate.of(2024, 1, 1)
//
//    // WHEN
//    viewModel.loadTasksFor(date)
//
//    // THEN
//    val state = viewModel.uiState
//
//    assertTrue(state is DayUiState.Error)
//}


//@Test
//fun whenRetryEvent_thenReloadsData() {
//
//    // GIVEN
//    val repository = FakeTaskRepository(
//        throwError = false,
//        tasks = emptyList()
//    )
//
//    val useCase = GetTasksForDay(repository)
//    val viewModel = DayViewModel(
//        getTasksForDay = useCase,
//        taskRepository = repository
//    )
//
//    val date = LocalDate.of(2024, 1, 1)
//
//    viewModel.loadTasksFor(date)
//
//    // WHEN
//    viewModel.onEvent(DayEvent.Retry)
//
//    // THEN
//    val state = viewModel.uiState
//
//    assertTrue(state is DayUiState.Empty)
//}


//@Test
//fun whenNextDayEvent_thenDateIncreases() {
//
//    // GIVEN
//    val repository = FakeTaskRepository()
//    val useCase = GetTasksForDay(repository)
//        val viewModel = DayViewModel(
//        getTasksForDay = useCase,
//        taskRepository = repository
//    )
//
//    val date = LocalDate.of(2024, 1, 1)
//
//    viewModel.loadTasksFor(date)
//
//    // WHEN
//    viewModel.onEvent(DayEvent.NextDay)
//
//    // THEN
//    val state = viewModel.uiState
//
//    val newDate = when (state) {
//        is DayUiState.Empty -> state.date
//        else -> null
//    }
//
//    assertTrue(newDate == date.plusDays(1))
//}

//    @Test
//    fun whenTaskChecked_thenTaskIsUpdated() {
//
//        val schedule = Schedule(
//            repeatType = RepeatType.COURSE,
//            startDate = LocalDate.of(2024, 1, 1),
//            time = LocalTime.NOON,
//            durationDays = 5
//        )
//
//        val task = Task(
//            titleTask = "Test",
//            isEnabled = true,
//            supportEnabled = false,
//            schedule = schedule,
//            isDone = false
//        )
//
//        val repository = FakeTaskRepository(
//            tasks = listOf(task)
//        )
//
//        val useCase = GetTasksForDay(repository)
//        val viewModel = DayViewModel(
//        getTasksForDay = useCase,
//        taskRepository = repository
//    )
//
//        val date = LocalDate.of(2024, 1, 1)
//
//        viewModel.loadTasksFor(date)
//
//        // WHEN
//        viewModel.onEvent(DayEvent.TaskChecked(task, true))
//
//        // THEN
//        val state = viewModel.uiState as DayUiState.Content
//
//        assertTrue(state.tasks.first().isDone)
//    }
//
private class FakeTaskRepository : TaskRepository {

    private val tasks = mutableListOf<Task>()

    override suspend fun getTasksForDay(date: LocalDate): List<Task> {
        return tasks.filter { it.date == date }
    }

    override suspend fun insertTask(task: Task) {
        tasks.add(task)
    }

    override suspend fun insertAll(tasks: List<Task>) {
        this.tasks.addAll(tasks)
    }

    override suspend fun updateTask(task: Task) {

        val index = tasks.indexOfFirst { it.id == task.id }

        if (index != -1) {

            tasks[index] = task

        }
    }

    override suspend fun deleteTask(task: Task) {

        tasks.removeIf { it.id == task.id }

    }

    override suspend fun getAllTasks(): List<Task> {

        return tasks

    }
}


}

