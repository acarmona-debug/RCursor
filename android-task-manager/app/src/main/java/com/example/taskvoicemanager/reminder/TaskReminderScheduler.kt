package com.example.taskvoicemanager.reminder

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import kotlin.math.max

object TaskReminderScheduler {
    fun schedule(
        context: Context,
        taskId: Long,
        taskDescription: String,
        dueAtEpochMillis: Long
    ) {
        val now = System.currentTimeMillis()
        val reminderAt = max(dueAtEpochMillis - TimeUnit.HOURS.toMillis(2), now + 15_000L)
        val delayMillis = max(reminderAt - now, 0L)

        val data = Data.Builder()
            .putLong(TaskReminderWorker.KEY_TASK_ID, taskId)
            .putString(TaskReminderWorker.KEY_TASK_DESCRIPTION, taskDescription)
            .build()

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueName(taskId),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(context: Context, taskId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueName(taskId))
    }

    private fun uniqueName(taskId: Long): String = "task_reminder_$taskId"
}
