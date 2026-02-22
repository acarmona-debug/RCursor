package com.oytask.app.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("es"))
    private val fullFormat = SimpleDateFormat("EEEE dd 'de' MMMM", Locale("es"))

    fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return "Sin fecha"
        return dateFormat.format(Date(timestamp))
    }

    fun formatFull(timestamp: Long?): String {
        if (timestamp == null) return "Sin fecha"
        return fullFormat.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
    }

    fun getRelativeDate(timestamp: Long?): String {
        if (timestamp == null) return "Sin fecha"

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }

        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val tomorrowStart = (todayStart.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val dayAfterTomorrow = (todayStart.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 2)
        }

        val targetDayStart = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return when {
            targetDayStart.timeInMillis < todayStart.timeInMillis -> {
                val daysAgo = TimeUnit.MILLISECONDS.toDays(
                    todayStart.timeInMillis - targetDayStart.timeInMillis
                )
                "Hace $daysAgo día${if (daysAgo != 1L) "s" else ""} \u26A0\uFE0F"
            }
            targetDayStart == todayStart || (timestamp >= todayStart.timeInMillis && timestamp < tomorrowStart.timeInMillis) -> "Hoy"
            targetDayStart.timeInMillis >= tomorrowStart.timeInMillis && targetDayStart.timeInMillis < dayAfterTomorrow.timeInMillis -> "Mañana"
            else -> {
                val daysUntil = TimeUnit.MILLISECONDS.toDays(
                    targetDayStart.timeInMillis - todayStart.timeInMillis
                )
                if (daysUntil <= 7) {
                    val dayName = SimpleDateFormat("EEEE", Locale("es")).format(Date(timestamp))
                    dayName.replaceFirstChar { it.uppercase() }
                } else {
                    formatDate(timestamp)
                }
            }
        }
    }

    fun isOverdue(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return timestamp < todayStart.timeInMillis
    }

    fun isToday(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }
}
