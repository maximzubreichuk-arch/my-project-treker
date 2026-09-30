package com.example.myprojecttreker.data.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.example.myprojecttreker.data.audio.AppSounds
import com.example.myprojecttreker.data.settings.SettingsManager
import com.example.myprojecttreker.R

object ReminderNotificationManager {
    private const val CHANNEL_PREFIX = "reminder_channel_"

    fun show(
        context: Context,
        taskId: Long,
        slotIndex: Int,
        title: String,
        soundUriString: String?
    ) {
        val appContext = context.applicationContext
        val settings = SettingsManager(appContext)
        val finalSound = soundUriString ?: settings.getDefaultSound() ?: AppSounds.uri(appContext.packageName, AppSounds.all.first())
        val soundUri = Uri.parse(finalSound)
        val manager = appContext.getSystemService(NotificationManager::class.java)
        val channelId = channelId(soundUri.toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existing = manager.getNotificationChannel(channelId)
            if (existing == null) {
                val channel = NotificationChannel(channelId, "Напоминания", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Напоминания о задачах"
                    setSound(soundUri, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
                    enableVibration(false)
                }
                manager.createNotificationChannel(channel)
            }
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setContentTitle(title)
            .setContentText("Пора выполнить задачу")
            .setSmallIcon(R.drawable.ic_notification)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(stableNotificationId(taskId, slotIndex), notification)

        if (settings.settings.value.vibrationEnabled) {
            val vibrator = appContext.getSystemService(Vibrator::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(220L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(220L)
            }
        }
    }

    private fun channelId(soundKey: String): String = CHANNEL_PREFIX + soundKey.hashCode().toUInt().toString(16)
    private fun stableNotificationId(taskId: Long, slotIndex: Int): Int = (31L * taskId + slotIndex).hashCode()
}
