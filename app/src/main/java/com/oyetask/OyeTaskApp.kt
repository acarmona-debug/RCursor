package com.oyetask

import android.app.Application
import androidx.room.Room
import com.oyetask.data.AppDatabase
import com.oyetask.data.TaskRepository

class OyeTaskApp : Application() {
    val db: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "oyetask.db",
        ).build()
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepository(db.taskDao())
    }
}

