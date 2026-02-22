package com.actividadapp.ui

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.actividadapp.data.Actividad
import com.actividadapp.voice.VoiceRecognizer
import com.actividadapp.viewmodel.ActividadViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadScreen(
    viewModel: ActividadViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val app = LocalContext.current.applicationContext as Application
                @Suppress("UNCHECKED_CAST")
                return ActividadViewModel(app) as T
            }
        }
    )
) {
    val actividades by viewModel.actividades.collectAsState()
    val mensajeVoz by viewModel.mensajeVoz.collectAsState()
    val errorVoz by viewModel.errorVoz.collectAsState()
    val context = LocalContext.current

    var voiceRecognizer by remember { mutableStateOf<VoiceRecognizer?>(null) }
    var escuchando by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            voiceRecognizer = VoiceRecognizer(context).apply {
                onResult = { viewModel.agregarDesdeVoz(it) }
                onError = { viewModel.mostrarErrorVoz(it) }
                onListeningChanged = { escuchando = it }
            }
            voiceRecognizer?.iniciarEscucha()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.limpiarMensajes()
    }

    LaunchedEffect(mensajeVoz, errorVoz) {
        if (mensajeVoz != null || errorVoz != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.limpiarMensajes()
        }
    }

    DisposableEffect(Unit) {
        onDispose { voiceRecognizer?.detener() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Mis Actividades",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "Gestiona tus tareas con voz",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            // Mensajes de feedback
            AnimatedVisibility(
                visible = mensajeVoz != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                mensajeVoz?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = errorVoz != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                errorVoz?.let { err ->
                    Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    err,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                }
            }

            // Lista de actividades
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = actividades,
                    key = { it.id }
                ) { actividad ->
                    ActividadItem(
                        actividad = actividad,
                        onCompletada = { viewModel.marcarCompletada(actividad.id, it) },
                        onEliminar = { viewModel.eliminar(actividad) }
                    )
                }
            }
        }

        // FAB de voz
        val scale by animateFloatAsState(
            targetValue = if (escuchando) 1.1f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (escuchando) {
                    Text(
                        "Escuchando...",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                FloatingActionButton(
                    onClick = {
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                if (voiceRecognizer == null) {
                                    voiceRecognizer = VoiceRecognizer(context).apply {
                                        onResult = { viewModel.agregarDesdeVoz(it) }
                                        onError = { viewModel.mostrarErrorVoz(it) }
                                        onListeningChanged = { escuchando = it }
                                    }
                                }
                                voiceRecognizer?.iniciarEscucha()
                            }
                            else -> permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    modifier = Modifier.scale(scale),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (escuchando) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Hablar"
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Toca para añadir con voz",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ActividadItem(
    actividad: Actividad,
    onCompletada: (Boolean) -> Unit,
    onEliminar: () -> Unit
) {
    var showDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (actividad.completada)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = actividad.completada,
                onCheckedChange = onCompletada
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatearFecha(actividad.fechaLimite),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = { showDelete = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Eliminar \"${actividad.titulo}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar()
                    showDelete = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun formatearFecha(timestamp: Long): String {
    val cal = Calendar.getInstance()
    val hoy = cal.get(Calendar.DAY_OF_YEAR)
    val año = cal.get(Calendar.YEAR)
    cal.timeInMillis = timestamp
    val díaTask = cal.get(Calendar.DAY_OF_YEAR)
    val añoTask = cal.get(Calendar.YEAR)

    return when {
        díaTask == hoy && añoTask == año -> "Hoy"
        díaTask == hoy + 1 && añoTask == año -> "Mañana"
        díaTask < hoy + 7 && añoTask == año -> SimpleDateFormat("EEEE", Locale("es")).format(Date(timestamp))
        else -> SimpleDateFormat("d MMM", Locale("es")).format(Date(timestamp))
    }
}
