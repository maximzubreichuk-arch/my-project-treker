package com.example.myprojecttreker.data.subscription

import android.content.Context
import com.example.myprojecttreker.domain.RepeatType
import com.example.myprojecttreker.domain.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class SubscriptionManager(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("subscription", Context.MODE_PRIVATE)
    private val _isPro = MutableStateFlow(prefs.getBoolean(KEY_PRO, false))
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    fun setProForTesting(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PRO, enabled).apply()
        _isPro.value = enabled
    }

    /**
     * Free plan limit is evaluated across the task's near-term occurrences,
     * not only the currently opened day. One recurring task still counts as one task per day.
     */
    fun canCreateTask(existing: List<Task>, candidate: Task): Boolean {
        if (_isPro.value) return true
        val horizon = when (candidate.repeatType) {
            RepeatType.ONCE -> 0L
            RepeatType.COURSE -> (candidate.courseDays ?: 1).coerceAtMost(366).toLong() - 1L
            else -> 90L
        }.coerceAtLeast(0L)
        var date = candidate.date
        repeat((horizon + 1L).toInt()) {
            val existingCount = existing.count { it.isScheduledFor(date) }
            if (candidate.isScheduledFor(date) && existingCount >= FREE_MAX_TASKS_PER_DAY) return false
            date = date.plusDays(1)
        }
        return true
    }

    fun canAddExtraTime(repeatType: RepeatType, currentExtraTimes: List<java.time.LocalTime>): Boolean =
        _isPro.value || repeatType != RepeatType.COURSE || currentExtraTimes.size < FREE_MAX_COURSE_EXTRA_TIMES

    fun canUseLanguage(languageTag: String): Boolean = _isPro.value || languageTag == "ru" || languageTag == "en"
    fun canUseSound(soundId: String): Boolean = _isPro.value || soundId == "default"

    companion object {
        const val FREE_MAX_TASKS_PER_DAY = 5
        const val FREE_MAX_COURSE_EXTRA_TIMES = 1
        private const val KEY_PRO = "is_pro"
    }
}
