package com.example.myprojecttreker.system.service

import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myprojecttreker.data.settings.SettingsManager

/**
 * Сервис для показа уведомления напоминания.
 *
 * Запускается из BroadcastReceiver при срабатывании alarm/worker.
 *
 * Отвечает за:
 * - получение данных (title, sound)
 * - создание NotificationChannel
 * - показ уведомления
 */
class ReminderService : Service() {

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Лог для отладки (проверка срабатывания)
        Log.e("REMINDER_TEST", "SERVICE STARTED")

        // Менеджер настроек (для получения дефолтного звука)
        val settings = SettingsManager(this)

        // Заголовок уведомления (если не передан — используем дефолт)
        val title = intent?.getStringExtra("title") ?: "Задача"

        // Звук из intent
        val soundStr = intent?.getStringExtra("soundUri")

        // Дефолтный звук из настроек
        val defaultSound = settings.getDefaultSound()

        // Итоговый звук (приоритет: task → default)
        val finalSound = soundStr ?: defaultSound

        // Преобразуем строку в Uri
        val soundUri = finalSound?.let { Uri.parse(it) }

        // NotificationManager для работы с уведомлениями
        val manager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel"

        // Создание NotificationChannel (обязательно для Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Напоминания",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                // Устанавливаем звук уведомления
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
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText("Пора выполнить задачу")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        // Запуск как foreground service (Android 8+ требует это)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, notification)
        } else {

            // Для старых версий просто показываем уведомление
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(1, notification)
        }
        // Завершаем сервис после показа уведомления
        stopSelf()

        return START_NOT_STICKY
    }

    // Привязка не используется (service только для запуска)
    override fun onBind(intent: Intent?): IBinder? = null
}