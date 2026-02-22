package com.oytask.app.data.db

import androidx.room.TypeConverter
import com.oytask.app.data.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)
}
