package com.example.mobilephone_water.data.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {


    fun scheduleNotifications(intervalHours: Int = 2) {
        val finalInterval = if (intervalHours < 1) 1 else intervalHours // Минимум 1 час

        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            finalInterval.toLong(),
            TimeUnit.HOURS,
            15, // Minimum interval 15 minutes
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "water_notification",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWork
        )
    }


    fun cancelNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork("water_notification")
    }


    fun isNotificationEnabled(): Boolean {
        val workInfo = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork("water_notification")
            .get()
        return workInfo.isNotEmpty() &&
                workInfo[0].state == WorkInfo.State.ENQUEUED
    }
}
