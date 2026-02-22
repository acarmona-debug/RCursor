package com.oytask.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: String = ""
)

enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}
