package com.actividadapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades ORDER BY fechaLimite ASC, fechaCreacion ASC")
    fun obtenerTodas(): Flow<List<Actividad>>

    @Query("SELECT * FROM actividades WHERE completada = 0 ORDER BY fechaLimite ASC")
    fun obtenerPendientes(): Flow<List<Actividad>>

    @Query("SELECT * FROM actividades WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Actividad?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(actividad: Actividad): Long

    @Update
    suspend fun actualizar(actividad: Actividad)

    @Delete
    suspend fun eliminar(actividad: Actividad)

    @Query("UPDATE actividades SET completada = :completada WHERE id = :id")
    suspend fun marcarCompletada(id: Long, completada: Boolean)
}
