package com.example.mobilephone_water.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mobilephone_water.MainActivity
import com.example.mobilephone_water.R
import kotlin.random.Random

class NotificationManagerHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "water_tracker_channel"
        const val CHANNEL_NAME = "Напоминания о воде"
        const val NOTIFICATION_ID = 1
        const val DISMISS_ACTION = "dismiss_action"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Уведомления для напоминания о питье воды"
                enableLights(true)
                lightColor = android.graphics.Color.CYAN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showWaterReminderNotification() {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 🔴 ТОЛЬКО КНОПКА "ПОЗЖЕ" (для закрытия уведомления)
        val dismissIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
            action = DISMISS_ACTION
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 2, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 🔴 ВАРИАНТЫ ТЕКСТОВ
        val titles = arrayOf(
            "💧 Пора пить воду!",
            "💧 Гидратация - ключ к здоровью!",
            "💧 Не забудь про воду!",
            "💧 Время позаботиться о себе!",
            "💧 Твоему организму нужна вода!"
        )

        val messages = arrayOf(
            "Выпей стакан воды для здоровья 🥤",
            "Помни: регулярный режим питья - твой лучший помощник 💪",
            "Вода - источник жизни. Пей больше! 🌊",
            "Даже небольшой глоток поможет тебе почувствовать себя лучше 😊",
            "Обезвоживание снижает производительность. Пей воду! ⚡",
            "Здоровье начинается с простого - с воды! 💚",
            "Твой организм нуждается в воде каждый день 🌟",
            "Не жди жажды - пей воду прямо сейчас! 🚀",
            "Каждый глоток воды - шаг к лучшему здоровью 🏃",
            "Вода - лучший напиток для спортсменов и активных людей 💯"
        )

        val bigTexts = arrayOf(
            "Выпивайте 8-10 стаканов воды в день для оптимального здоровья!",
            "Вода помогает улучшить концентрацию и энергию. Не забывай пить!",
            "Правильное увлажнение улучшает кожу и метаболизм. Пей воду! 💧",
            "Помни: 70% твоего тела состоит из воды. Поддерживай баланс!",
            "Регулярное питье воды снижает риск заболеваний и улучшает самочувствие."
        )

        val randomTitle = titles[Random.nextInt(titles.size)]
        val randomMessage = messages[Random.nextInt(messages.size)]
        val randomBigText = bigTexts[Random.nextInt(bigTexts.size)]

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(randomTitle)
            .setContentText(randomMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(randomBigText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

            // 🔴 ТОЛЬКО ОДНА КНОПКА - "ПОЗЖЕ"
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "⏱️ Позже",
                dismissPendingIntent
            )

            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setLights(android.graphics.Color.CYAN, 1000, 1000)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun showSuccessNotification(message: String) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("✓ Успешно!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
