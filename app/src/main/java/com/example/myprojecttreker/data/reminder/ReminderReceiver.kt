package com.example.myprojecttreker.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.myprojecttreker.data.local.AppDatabase
import com.example.myprojecttreker.data.mapper.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Получает alarm, показывает уведомление и перепланирует ближайшие будущие события задачи.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_REMINDER) return

        val taskId = intent.getLongExtra(EXTRA_TASK_ID, 0L)
        val slotIndex = intent.getIntExtra(EXTRA_SLOT_INDEX, 0)
        if (taskId == 0L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val taskWithSubtasks = db.taskDao().getTaskWithSubTasks(taskId)
                val task = taskWithSubtasks?.toDomain()

                if (task == null) {
                    Log.w(TAG, "Task $taskId no longer exists")
                    return@launch
                }

                ReminderNotificationManager.show(
                    context = context,
                    taskId = taskId,
                    slotIndex = slotIndex,
                    title = task.title,
                    soundUriString = task.soundUri
                )

                ReminderSchedulerImpl(context).schedule(task)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_REMINDER = "com.example.myprojecttreker.action.REMINDER"
        const val EXTRA_TASK_ID = "taskId"
        const val EXTRA_SLOT_INDEX = "slotIndex"
        private const val TAG = "ReminderReceiver"

        fun buildUri(taskId: Long, slotIndex: Int): Uri =
            Uri.parse("treker://reminder/$taskId/$slotIndex")
    }
}
