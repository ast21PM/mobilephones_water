package com.example.mobilephone_water.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "water_tracker_preferences"


        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_NOTIFICATION_INTERVAL = "notification_interval"
        private const val KEY_NOTIFICATION_START_TIME = "notification_start_time"
        private const val KEY_NOTIFICATION_END_TIME = "notification_end_time"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    var isNotificationEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_ENABLED, value).apply()

    var notificationInterval: Int
        get() = sharedPreferences.getInt(KEY_NOTIFICATION_INTERVAL, 60)
        set(value) = sharedPreferences.edit().putInt(KEY_NOTIFICATION_INTERVAL, value).apply()

    var notificationStartTime: String
        get() = sharedPreferences.getString(KEY_NOTIFICATION_START_TIME, "08:00") ?: "08:00"
        set(value) = sharedPreferences.edit().putString(KEY_NOTIFICATION_START_TIME, value).apply()

    var notificationEndTime: String
        get() = sharedPreferences.getString(KEY_NOTIFICATION_END_TIME, "22:00") ?: "22:00"
        set(value) = sharedPreferences.edit().putString(KEY_NOTIFICATION_END_TIME, value).apply()

    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()
}
