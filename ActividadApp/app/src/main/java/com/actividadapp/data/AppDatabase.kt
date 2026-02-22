package com.actividadapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Actividad::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actividadDao(): ActividadDao
}

object DatabaseProvider {
    fun getDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "actividad_db"
        ).build()
    }
}
