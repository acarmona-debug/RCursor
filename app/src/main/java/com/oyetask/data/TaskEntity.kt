package com.oyetask.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dueDateEpochDay: Long?,
    val isDone: Boolean,
    val createdAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
)

