package com.actividadapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades")
data class Actividad(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String = "",
    val fechaLimite: Long, // timestamp en milisegundos
    val completada: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)
