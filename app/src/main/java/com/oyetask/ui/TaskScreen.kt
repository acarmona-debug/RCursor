package com.oyetask.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetask.data.TaskEntity
import com.oyetask.util.Dates
import com.oyetask.voice.VoiceCommandParser
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.snackbar.collectLatest { snackbarHostState.showSnackbar(it) }
    }

    var newTitle by remember { mutableStateOf("") }
    var dueEpochDay by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val spoken = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
            ?.trim()
            .orEmpty()
        if (spoken.isBlank()) return@rememberLauncherForActivityResult

        val parsed = VoiceCommandParser.parse(spoken)
        if (parsed != null) {
            viewModel.addTask(parsed.title, parsed.dueDateEpochDay)
            scope.launch { snackbarHostState.showSnackbar("Agregado por voz.") }
        } else {
            newTitle = spoken
            scope.launch { snackbarHostState.showSnackbar("No entendí la fecha; dejé el texto para que lo ajustes.") }
        }
    }

    fun launchSpeech() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Di: Oye task, tengo que… para…")
        }
        speechLauncher.launch(intent)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            launchSpeech()
        } else {
            scope.launch { snackbarHostState.showSnackbar("Necesito permiso de micrófono para dictado.") }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                IconButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        dueEpochDay = millis?.let { Dates.epochDayFromUtcMillis(it) }
                        showDatePicker = false
                    },
                ) {
                    Icon(Icons.Default.Event, contentDescription = "Confirmar fecha")
                }
            },
            dismissButton = {
                IconButton(
                    onClick = {
                        dueEpochDay = null
                        showDatePicker = false
                    },
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Quitar fecha")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OyeTask") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            val granted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO,
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) launchSpeech() else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Dictar actividad")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilterRow(
                selected = filter,
                onSelected = viewModel::setFilter,
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggleDone = { viewModel.toggleDone(task) },
                        onDelete = { viewModel.delete(task) },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    placeholder = { Text("Nueva actividad…") },
                    singleLine = true,
                )

                FilledIconButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(Icons.Default.Event, contentDescription = "Elegir fecha")
                }

                FilledIconButton(
                    onClick = {
                        viewModel.addTask(newTitle, dueEpochDay)
                        newTitle = ""
                        dueEpochDay = null
                    },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }

            if (dueEpochDay != null) {
                Text(
                    text = "Fecha: ${Dates.formatEpochDay(dueEpochDay!!)}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun FilterRow(
    selected: TaskFilter,
    onSelected: (TaskFilter) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            label = "Pendientes",
            selected = selected == TaskFilter.Pending,
            onClick = { onSelected(TaskFilter.Pending) },
        )
        FilterChip(
            label = "Hechas",
            selected = selected == TaskFilter.Done,
            onClick = { onSelected(TaskFilter.Done) },
        )
        FilterChip(
            label = "Todas",
            selected = selected == TaskFilter.All,
            onClick = { onSelected(TaskFilter.All) },
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            labelColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

@Composable
private fun TaskRow(
    task: TaskEntity,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { onToggleDone() },
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
            )
            if (task.dueDateEpochDay != null) {
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = Dates.formatEpochDay(task.dueDateEpochDay),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
        }
    }
}

