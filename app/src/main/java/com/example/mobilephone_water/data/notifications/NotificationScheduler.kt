package com.example.mobilephone_water.data.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleNotifications(intervalHours: Int = 2) {
        val finalInterval = if (intervalHours < 1) 1 else intervalHours

        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            finalInterval.toLong(),
            TimeUnit.HOURS,
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "water_notification",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWork
        )
    }

    fun cancelNotifications() {
        try {
            
            WorkManager.getInstance(context).cancelUniqueWork("water_notification")

           
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()

          
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationManager.deleteNotificationChannel("water_reminder")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isNotificationEnabled(): Boolean {
        val workInfo = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork("water_notification")
            .get()
        return workInfo.isNotEmpty() &&
                workInfo[0].state == WorkInfo.State.ENQUEUED
    }
}
