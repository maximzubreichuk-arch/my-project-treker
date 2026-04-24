package com.example.myprojecttreker.system.worker

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myprojecttreker.data.settings.SettingsManager

/**
 * Worker для показа уведомления напоминания.
 *
 * Используется как альтернатива AlarmManager (через WorkManager).
 *
 * Отвечает за:
 * - получение данных из inputData
 * - создание NotificationChannel
 * - показ уведомления
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        // Менеджер настроек (для получения дефолтного звука)
        val settings = SettingsManager(applicationContext)

        // Заголовок уведомления
        val title = inputData.getString("title") ?: "Задача"

        // Звук из inputData
        val soundStr = inputData.getString("soundUri")

        // Дефолтный звук из настроек
        val defaultSound = settings.getDefaultSound()

        // Итоговый звук (приоритет: task → default)
        val finalSound = soundStr ?: defaultSound

        // Преобразуем строку в Uri
        val soundUri = finalSound?.let { Uri.parse(it) }

        // NotificationManager для показа уведомления
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        val channelId = "reminder_channel"

        // Создание NotificationChannel (обязательно для Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Напоминания",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(
                    soundUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }
            manager.createNotificationChannel(channel)
        }

        // Создание уведомления
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText("Пора выполнить задачу")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Показ уведомления (уникальный ID, чтобы не перезаписывались)
        manager.notify(System.currentTimeMillis().toInt(), notification)

        return Result.success()
    }
}