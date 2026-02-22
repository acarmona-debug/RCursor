package com.example.taskvoicemanager.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao
) {
    fun observeTasks(): Flow<List<TaskEntity>> = taskDao.observeAll()

    suspend fun addTask(
        description: String,
        dueAtEpochMillis: Long?
    ) {
        taskDao.insert(
            TaskEntity(
                description = description,
                dueAtEpochMillis = dueAtEpochMillis
            )
        )
    }

    suspend fun setCompleted(
        taskId: Long,
        completed: Boolean
    ) {
        taskDao.updateCompleted(taskId = taskId, completed = completed)
    }

    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteById(taskId)
    }
}
