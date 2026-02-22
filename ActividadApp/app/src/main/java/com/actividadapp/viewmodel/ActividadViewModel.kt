package com.actividadapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actividadapp.data.Actividad
import com.actividadapp.data.ActividadDao
import com.actividadapp.util.VoiceParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActividadViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: ActividadDao = (application as com.actividadapp.ActividadApp).database.actividadDao()

    val actividades: StateFlow<List<Actividad>> = dao.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensajeVoz = MutableStateFlow<String?>(null)
    val mensajeVoz: StateFlow<String?> = _mensajeVoz.asStateFlow()

    private val _errorVoz = MutableStateFlow<String?>(null)
    val errorVoz: StateFlow<String?> = _errorVoz.asStateFlow()

    fun agregarActividad(titulo: String, fechaLimite: Long) {
        viewModelScope.launch {
            dao.insertar(Actividad(titulo = titulo, fechaLimite = fechaLimite))
        }
    }

    fun agregarDesdeVoz(texto: String) {
        viewModelScope.launch {
            _mensajeVoz.value = null
            _errorVoz.value = null

            val resultado = VoiceParser.parsear(texto)
            if (resultado != null) {
                val actividad = Actividad(
                    titulo = resultado.titulo,
                    fechaLimite = resultado.fechaLimite
                )
                dao.insertar(actividad)
                _mensajeVoz.value = "✓ Tarea añadida: ${resultado.titulo}"
            } else {
                _errorVoz.value = "No entendí. Di: \"Tengo que hacer [actividad] para [mañana/el lunes/15 de marzo]\""
            }
        }
    }

    fun marcarCompletada(id: Long, completada: Boolean) {
        viewModelScope.launch {
            dao.marcarCompletada(id, completada)
        }
    }

    fun eliminar(actividad: Actividad) {
        viewModelScope.launch {
            dao.eliminar(actividad)
        }
    }

    fun mostrarErrorVoz(mensaje: String) {
        _errorVoz.value = mensaje
    }

    fun limpiarMensajes() {
        _mensajeVoz.value = null
        _errorVoz.value = null
    }
}
