package com.oytask.app.util

import com.oytask.app.data.model.Priority
import com.oytask.app.data.model.Task
import java.util.*
import java.util.regex.Pattern

object VoiceParser {

    data class ParsedTask(
        val title: String,
        val dueDate: Long? = null,
        val priority: Priority = Priority.MEDIUM
    )

    fun parse(input: String): ParsedTask {
        val cleaned = input.trim()
        val lowerInput = cleaned.lowercase(Locale("es"))

        val priority = extractPriority(lowerInput)
        val dueDate = extractDate(lowerInput)
        val title = cleanTitle(cleaned, lowerInput)

        return ParsedTask(
            title = title.replaceFirstChar { it.uppercase() },
            dueDate = dueDate,
            priority = priority
        )
    }

    private fun extractPriority(input: String): Priority {
        return when {
            input.contains("urgente") || input.contains("ya mismo") -> Priority.URGENT
            input.contains("importante") || input.contains("alta prioridad") -> Priority.HIGH
            input.contains("baja prioridad") || input.contains("cuando pueda") -> Priority.LOW
            else -> Priority.MEDIUM
        }
    }

    private fun extractDate(input: String): Long? {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)

        return when {
            input.contains("hoy") -> cal.timeInMillis
            input.contains("mañana") || input.contains("manana") -> {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                cal.timeInMillis
            }
            input.contains("pasado mañana") || input.contains("pasado manana") -> {
                cal.add(Calendar.DAY_OF_YEAR, 2)
                cal.timeInMillis
            }
            input.contains("próxima semana") || input.contains("proxima semana") ||
            input.contains("la semana que viene") -> {
                cal.add(Calendar.WEEK_OF_YEAR, 1)
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                cal.timeInMillis
            }
            input.contains("próximo mes") || input.contains("proximo mes") ||
            input.contains("el mes que viene") -> {
                cal.add(Calendar.MONTH, 1)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.timeInMillis
            }
            input.contains("viernes") -> nextDayOfWeek(Calendar.FRIDAY)
            input.contains("lunes") -> nextDayOfWeek(Calendar.MONDAY)
            input.contains("martes") -> nextDayOfWeek(Calendar.TUESDAY)
            input.contains("miércoles") || input.contains("miercoles") -> nextDayOfWeek(Calendar.WEDNESDAY)
            input.contains("jueves") -> nextDayOfWeek(Calendar.THURSDAY)
            input.contains("sábado") || input.contains("sabado") -> nextDayOfWeek(Calendar.SATURDAY)
            input.contains("domingo") -> nextDayOfWeek(Calendar.SUNDAY)
            else -> {
                extractDaysFromNow(input) ?: extractSpecificDate(input)
            }
        }
    }

    private fun nextDayOfWeek(dayOfWeek: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val currentDay = cal.get(Calendar.DAY_OF_WEEK)
        var daysUntil = dayOfWeek - currentDay
        if (daysUntil <= 0) daysUntil += 7
        cal.add(Calendar.DAY_OF_YEAR, daysUntil)
        return cal.timeInMillis
    }

    private fun extractDaysFromNow(input: String): Long? {
        val pattern = Pattern.compile("en (\\d+) d[ií]as?")
        val matcher = pattern.matcher(input)
        if (matcher.find()) {
            val days = matcher.group(1)?.toIntOrNull() ?: return null
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, days)
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            return cal.timeInMillis
        }
        return null
    }

    private fun extractSpecificDate(input: String): Long? {
        val months = mapOf(
            "enero" to Calendar.JANUARY, "febrero" to Calendar.FEBRUARY,
            "marzo" to Calendar.MARCH, "abril" to Calendar.APRIL,
            "mayo" to Calendar.MAY, "junio" to Calendar.JUNE,
            "julio" to Calendar.JULY, "agosto" to Calendar.AUGUST,
            "septiembre" to Calendar.SEPTEMBER, "octubre" to Calendar.OCTOBER,
            "noviembre" to Calendar.NOVEMBER, "diciembre" to Calendar.DECEMBER
        )

        for ((monthName, monthNum) in months) {
            val pattern = Pattern.compile("(\\d{1,2})\\s+de\\s+$monthName")
            val matcher = pattern.matcher(input)
            if (matcher.find()) {
                val day = matcher.group(1)?.toIntOrNull() ?: continue
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, monthNum)
                cal.set(Calendar.DAY_OF_MONTH, day)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                if (cal.timeInMillis < System.currentTimeMillis()) {
                    cal.add(Calendar.YEAR, 1)
                }
                return cal.timeInMillis
            }
        }
        return null
    }

    private fun cleanTitle(original: String, lower: String): String {
        val removals = listOf(
            "para hoy", "para mañana", "para manana", "para pasado mañana",
            "para pasado manana", "para la próxima semana", "para proxima semana",
            "para la semana que viene", "para el próximo mes", "para proximo mes",
            "para el mes que viene", "para el lunes", "para el martes",
            "para el miércoles", "para el miercoles", "para el jueves",
            "para el viernes", "para el sábado", "para el sabado", "para el domingo",
            "urgente", "importante", "alta prioridad", "baja prioridad", "cuando pueda",
            "tengo que", "necesito", "debo", "hay que", "oye task",
            "hacer", "recordar", "para"
        )

        var result = lower
        for (removal in removals.sortedByDescending { it.length }) {
            result = result.replace(removal, "").trim()
        }

        result = result.replace(Regex("en \\d+ d[ií]as?"), "").trim()
        result = result.replace(Regex("\\d{1,2} de \\w+"), "").trim()
        result = result.replace(Regex("\\s+"), " ").trim()

        if (result.isBlank()) {
            return original.replace(Regex("(?i)(oye task|tengo que|necesito|debo|hay que)"), "").trim()
        }

        return result
    }

    fun toTask(parsed: ParsedTask): Task {
        return Task(
            title = parsed.title,
            dueDate = parsed.dueDate,
            priority = parsed.priority
        )
    }
}
