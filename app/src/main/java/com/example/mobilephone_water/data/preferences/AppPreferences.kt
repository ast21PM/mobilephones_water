package com.example.mobilephone_water.data.preferences

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "water_tracker_preferences"

        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_NOTIFICATION_INTERVAL = "notification_interval"
        private const val KEY_NOTIFICATION_START_TIME = "notification_start_time"
        private const val KEY_NOTIFICATION_END_TIME = "notification_end_time"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"

       
        private const val KEY_GENDER = "gender"
        private const val KEY_WEIGHT = "weight"
        private const val KEY_HEIGHT = "height"
        private const val KEY_AGE = "age"
        private const val KEY_NAME = "name"
        private const val KEY_ACTIVITY = "activity"
        private const val KEY_DAILY_WATER_GOAL = "daily_water_goal"

        private const val KEY_NOTIFICATION_SOUND = "notification_sound"
    }

    var isNotificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, value).apply()

    var notificationInterval: Int
        get() = prefs.getInt(KEY_NOTIFICATION_INTERVAL, 60)
        set(value) = prefs.edit().putInt(KEY_NOTIFICATION_INTERVAL, value).apply()

    var notificationStartTime: String
        get() = prefs.getString(KEY_NOTIFICATION_START_TIME, "08:00") ?: "08:00"
        set(value) = prefs.edit().putString(KEY_NOTIFICATION_START_TIME, value).apply()

    var notificationEndTime: String
        get() = prefs.getString(KEY_NOTIFICATION_END_TIME, "22:00") ?: "22:00"
        set(value) = prefs.edit().putString(KEY_NOTIFICATION_END_TIME, value).apply()

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()

    var lastResetDate: String
        get() = prefs.getString(KEY_LAST_RESET_DATE, getCurrentDate()) ?: getCurrentDate()
        set(value) = prefs.edit().putString(KEY_LAST_RESET_DATE, value).apply()

    var notificationSound: String
        get() = prefs.getString(KEY_NOTIFICATION_SOUND, "default") ?: "default"
        set(value) = prefs.edit().putString(KEY_NOTIFICATION_SOUND, value).apply()

    
    fun saveUserProfile(
        gender: String,
        weight: Int,
        height: Int,
        age: Int,
        name: String,
        activity: String,
        dailyWater: Int
    ) {
        prefs.edit().apply {
            putString(KEY_GENDER, gender)
            putInt(KEY_WEIGHT, weight)
            putInt(KEY_HEIGHT, height)
            putInt(KEY_AGE, age)
            putString(KEY_NAME, name)
            putString(KEY_ACTIVITY, activity)
            putInt(KEY_DAILY_WATER_GOAL, dailyWater)
            apply()
        }
    }

    fun getUserName(): String = prefs.getString(KEY_NAME, "Пользователь") ?: "Пользователь"
    fun getDailyWaterGoal(): Int = prefs.getInt(KEY_DAILY_WATER_GOAL, 2200)
    fun getUserGender(): String = prefs.getString(KEY_GENDER, "Мужской") ?: "Мужской"
    fun getUserWeight(): Int = prefs.getInt(KEY_WEIGHT, 70)
    fun getUserHeight(): Int = prefs.getInt(KEY_HEIGHT, 170)
    fun getUserAge(): Int = prefs.getInt(KEY_AGE, 25)
    fun getUserActivity(): String = prefs.getString(KEY_ACTIVITY, "Регулярно") ?: "Регулярно"

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
