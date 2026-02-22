package com.example.taskvoicemanager.integrations

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.example.taskvoicemanager.ui.TaskUiModel
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

object GoogleIntentGateway {
    fun openGoogleCalendarEvent(context: Context, task: TaskUiModel): Boolean {
        val eventStartMillis = task.dueAtEpochMillis?.let { dueAt ->
            Instant.ofEpochMilli(dueAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atTime(LocalTime.of(9, 0))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } ?: System.currentTimeMillis() + 3_600_000L
        val eventEndMillis = eventStartMillis + 3_600_000L

        val baseIntent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, task.description)
            putExtra(
                CalendarContract.Events.DESCRIPTION,
                "Actividad creada desde Task Voice Manager. Prioridad: ${task.priority.label}"
            )
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventStartMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEndMillis)
        }

        val googleCalendarIntent = Intent(baseIntent).apply {
            setPackage("com.google.android.calendar")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val genericCalendarIntent = Intent(baseIntent).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return startIntentSafely(context, googleCalendarIntent) ||
            startIntentSafely(context, genericCalendarIntent)
    }

    fun sendToGoogleTasks(context: Context, task: TaskUiModel): Boolean {
        val text = buildString {
            append(task.description)
            task.dueAtEpochMillis?.let {
                append("\nVence: ")
                append(
                    Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                )
            }
            append("\nPrioridad: ${task.priority.label}")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, task.description)
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val googleTasksIntent = Intent(sendIntent).apply {
            setPackage("com.google.android.apps.tasks")
        }
        val chooserIntent = Intent.createChooser(sendIntent, "Enviar a Google Tasks").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return startIntentSafely(context, googleTasksIntent) ||
            startIntentSafely(context, chooserIntent)
    }

    private fun startIntentSafely(context: Context, intent: Intent): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }
}
