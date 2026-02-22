package com.oytask.app.data.repository

import androidx.lifecycle.LiveData
import com.oytask.app.data.db.TaskDao
import com.oytask.app.data.model.Task
import java.util.*

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()
    val pendingTasks: LiveData<List<Task>> = taskDao.getPendingTasks()
    val completedTasks: LiveData<List<Task>> = taskDao.getCompletedTasks()
    val pendingCount: LiveData<Int> = taskDao.getPendingCount()

    fun getOverdueCount(): LiveData<Int> = taskDao.getOverdueCount(System.currentTimeMillis())

    fun getTodayTasks(): LiveData<List<Task>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val endOfDay = cal.timeInMillis
        return taskDao.getTasksForDay(startOfDay, endOfDay)
    }

    fun getOverdueTasks(): LiveData<List<Task>> {
        return taskDao.getOverdueTasks(System.currentTimeMillis())
    }

    fun getThisWeekTasks(): LiveData<List<Task>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        val startOfWeek = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, 1)
        val endOfWeek = cal.timeInMillis
        return taskDao.getTasksForWeek(startOfWeek, endOfWeek)
    }

    fun searchTasks(query: String): LiveData<List<Task>> = taskDao.searchTasks(query)

    suspend fun insert(task: Task): Long = taskDao.insert(task)

    suspend fun update(task: Task) = taskDao.update(task)

    suspend fun delete(task: Task) = taskDao.delete(task)

    suspend fun toggleComplete(task: Task) {
        taskDao.update(task.copy(isCompleted = !task.isCompleted))
    }

    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)
}
