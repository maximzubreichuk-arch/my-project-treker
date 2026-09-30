package com.example.myprojecttreker.data.ads

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("sessions", Context.MODE_PRIVATE)

    fun registerSession(now: Long = System.currentTimeMillis()): Int {
        val last = prefs.getLong(KEY_LAST, 0L)
        val count = prefs.getInt(KEY_COUNT, 0)
        val sessionGap = 30 * 60 * 1000L
        val isNewSession = last == 0L || now - last >= sessionGap
        if (!isNewSession) return count
        val next = count + 1
        prefs.edit().putLong(KEY_LAST, now).putInt(KEY_COUNT, next).apply()
        return next
    }

    fun shouldShowAd(isPro: Boolean): Boolean {
        if (isPro) return false
        return prefs.getInt(KEY_COUNT, 0) % 3 == 0
    }

    companion object {
        private const val KEY_LAST = "last_session_time"
        private const val KEY_COUNT = "session_count"
    }
}
