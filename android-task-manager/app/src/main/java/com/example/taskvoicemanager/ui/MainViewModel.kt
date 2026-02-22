package com.example.taskvoicemanager.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskvoicemanager.data.AppDatabase
import com.example.taskvoicemanager.data.TaskEntity
import com.example.taskvoicemanager.data.TaskRepository
import com.example.taskvoicemanager.voice.VoiceTaskParser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TaskUiModel(
    val id: Long,
    val description: String,
    val dueAtEpochMillis: Long?,
    val completed: Boolean
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(
        taskDao = AppDatabase.getInstance(application).taskDao()
    )

    val tasks = repository.observeTasks()
        .map { entities -> entities.map { it.toUiModel() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    var draftTaskText by mutableStateOf("")
        private set

    var lastRecognizedText by mutableStateOf<String?>(null)
        private set

    fun onDraftChanged(value: String) {
        draftTaskText = value
    }

    fun addTaskFromDraft() {
        val description = draftTaskText.trim()
        if (description.isBlank()) return

        viewModelScope.launch {
            repository.addTask(
                description = description,
                dueAtEpochMillis = null
            )
            draftTaskText = ""
        }
    }

    fun addTaskFromVoice(rawText: String) {
        lastRecognizedText = rawText
        val parsed = VoiceTaskParser.parse(rawText = rawText)

        if (parsed.description.isBlank()) {
            draftTaskText = rawText
            return
        }

        viewModelScope.launch {
            repository.addTask(
                description = parsed.description,
                dueAtEpochMillis = parsed.dueAtEpochMillis
            )
            draftTaskText = ""
        }
    }

    fun setTaskCompleted(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.setCompleted(taskId = taskId, completed = completed)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId = taskId)
        }
    }

    private fun TaskEntity.toUiModel(): TaskUiModel {
        return TaskUiModel(
            id = id,
            description = description,
            dueAtEpochMillis = dueAtEpochMillis,
            completed = completed
        )
    }
}
