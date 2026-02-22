package com.oytask.app.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oytask.app.data.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC, createdAt DESC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC, createdAt DESC")
    fun getPendingTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedTasks(): LiveData<List<Task>>

    @Query("""
        SELECT * FROM tasks 
        WHERE isCompleted = 0 AND dueDate IS NOT NULL 
        AND dueDate >= :startOfDay AND dueDate < :endOfDay 
        ORDER BY dueDate ASC
    """)
    fun getTasksForDay(startOfDay: Long, endOfDay: Long): LiveData<List<Task>>

    @Query("""
        SELECT * FROM tasks 
        WHERE isCompleted = 0 AND dueDate IS NOT NULL AND dueDate < :now 
        ORDER BY dueDate ASC
    """)
    fun getOverdueTasks(now: Long): LiveData<List<Task>>

    @Query("""
        SELECT * FROM tasks 
        WHERE isCompleted = 0 AND dueDate IS NOT NULL 
        AND dueDate >= :startOfWeek AND dueDate < :endOfWeek 
        ORDER BY dueDate ASC
    """)
    fun getTasksForWeek(startOfWeek: Long, endOfWeek: Long): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTasks(query: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0 AND dueDate IS NOT NULL AND dueDate < :now")
    fun getOverdueCount(now: Long): LiveData<Int>
}
