package com.oyetask.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Dates {
    private val localeEs = Locale("es", "ES")
    private val formatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy", localeEs)

    fun epochDayFromUtcMillis(millis: Long): Long {
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        return date.toEpochDay()
    }

    fun formatEpochDay(epochDay: Long, today: LocalDate = LocalDate.now()): String {
        val date = LocalDate.ofEpochDay(epochDay)
        return when (date) {
            today -> "Hoy"
            today.plusDays(1) -> "Mañana"
            else -> date.format(formatter).replaceFirstChar { it.titlecase(localeEs) }
        }
    }
}

