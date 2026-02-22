package com.oyetask.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.oyetask.OyeTaskApp
import com.oyetask.data.TaskEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as OyeTaskApp).taskRepository

    private val _filter = MutableStateFlow(TaskFilter.Pending)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    private val allTasks = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = combine(allTasks, _filter) { list, f ->
        when (f) {
            TaskFilter.Pending -> list.filter { !it.isDone }
            TaskFilter.Done -> list.filter { it.isDone }
            TaskFilter.All -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _snackbar = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbar = _snackbar.asSharedFlow()

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    fun addTask(title: String, dueDateEpochDay: Long?) {
        val clean = title.trim()
        if (clean.isBlank()) {
            _snackbar.tryEmit("Escribe una actividad primero.")
            return
        }
        viewModelScope.launch {
            repo.add(clean, dueDateEpochDay)
        }
    }

    fun toggleDone(task: TaskEntity) {
        viewModelScope.launch {
            repo.setDone(task, !task.isDone)
        }
    }

    fun delete(task: TaskEntity) {
        viewModelScope.launch {
            repo.delete(task.id)
            _snackbar.tryEmit("Actividad eliminada.")
        }
    }
}

