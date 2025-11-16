package com.example.mobilephone_water.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mobilephone_water.MainActivity
import com.example.mobilephone_water.R
import com.example.mobilephone_water.data.preferences.AppPreferences

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            showWaterReminder()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun showWaterReminder() {
        val appPreferences = AppPreferences(applicationContext)

        if (!appPreferences.isNotificationEnabled) {
            return
        }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ✅ ПОЛУЧИ ВЫБРАННЫЙ ЗВУК ИЗ НАСТРОЕК (ТОЛЬКОСтв
        val selectedSound = appPreferences.notificationSound
        val soundUri = when (selectedSound) {
            "droplet" -> Uri.parse("android.resource://${applicationContext.packageName}/${R.raw.droplet}")
            "squeak" -> Uri.parse("android.resource://${applicationContext.packageName}/${R.raw.squeak}")
            "bell" -> Uri.parse("android.resource://${applicationContext.packageName}/${R.raw.bell}")
            else -> Uri.parse("android.resource://${applicationContext.packageName}/${R.raw.droplet}")
        }

        createNotificationChannel(notificationManager, soundUri)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("💧 Пора пить воду!")
            .setContentText("Выпейте стакан воды для здоровья")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        soundUri: Uri
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, "💧 Напоминание о воде", importance).apply {
                description = "Напоминания о питье воды"
                setSound(soundUri, android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                    .build())
                enableVibration(true)
                lightColor = android.graphics.Color.CYAN
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "water_reminder_channel"
        private const val NOTIFICATION_ID = 1
    }
}
