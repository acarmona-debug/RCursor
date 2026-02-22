package com.oyetask.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val dao: TaskDao,
) {
    fun observeAll(): Flow<List<TaskEntity>> = dao.observeAll()

    suspend fun add(title: String, dueDateEpochDay: Long?) {
        val now = System.currentTimeMillis()
        dao.insert(
            TaskEntity(
                title = title.trim(),
                dueDateEpochDay = dueDateEpochDay,
                isDone = false,
                createdAtEpochMillis = now,
                completedAtEpochMillis = null,
            ),
        )
    }

    suspend fun setDone(task: TaskEntity, done: Boolean) {
        dao.update(
            task.copy(
                isDone = done,
                completedAtEpochMillis = if (done) System.currentTimeMillis() else null,
            ),
        )
    }

    suspend fun delete(taskId: Long) {
        dao.deleteById(taskId)
    }
}

