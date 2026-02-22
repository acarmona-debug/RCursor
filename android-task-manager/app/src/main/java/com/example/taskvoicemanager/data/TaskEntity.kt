package com.example.taskvoicemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val description: String,
    val dueAtEpochMillis: Long?,
    val completed: Boolean = false,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
