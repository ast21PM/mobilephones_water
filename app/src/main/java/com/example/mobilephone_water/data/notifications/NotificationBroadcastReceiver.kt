package com.example.mobilephone_water.data.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotificationManagerHelper.DISMISS_ACTION -> {

                Toast.makeText(context, "⏱️ Напоминание отложено", Toast.LENGTH_SHORT).show()


                val notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as android.app.NotificationManager
                notificationManager.cancel(NotificationManagerHelper.NOTIFICATION_ID)
            }
        }
    }
}
