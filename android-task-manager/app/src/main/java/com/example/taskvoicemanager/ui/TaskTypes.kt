package com.example.taskvoicemanager.ui

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class TaskUiModel(
    val id: Long,
    val description: String,
    val dueAtEpochMillis: Long?,
    val priority: TaskPriority,
    val reminderEnabled: Boolean,
    val completed: Boolean
)

enum class TaskPriority(val dbValue: Int, val label: String) {
    LOW(dbValue = 0, label = "Baja"),
    MEDIUM(dbValue = 1, label = "Media"),
    HIGH(dbValue = 2, label = "Alta");

    companion object {
        fun fromDbValue(value: Int): TaskPriority {
            return entries.firstOrNull { it.dbValue == value } ?: MEDIUM
        }
    }
}

enum class TaskFilter {
    ALL,
    PENDING,
    TODAY,
    OVERDUE,
    COMPLETED
}

enum class QuickDueOption {
    NONE,
    TODAY,
    TOMORROW,
    NEXT_WEEK
}

data class TaskStats(
    val pending: Int,
    val completed: Int,
    val today: Int,
    val overdue: Int
)

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}
