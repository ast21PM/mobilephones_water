package com.example.mobilephone_water.data

import android.content.Context
import com.example.mobilephone_water.data.preferences.AppPreferences
import java.text.SimpleDateFormat
import java.util.*

object DailyResetManager {

    fun shouldResetDailyData(context: Context): Boolean {
        val prefs = AppPreferences(context)
        val lastResetDate = getLastResetDate(context)
        val currentDate = getCurrentDate()

        return lastResetDate != currentDate
    }

    fun resetDailyData(context: Context) {
        val prefs = AppPreferences(context)
        prefs.lastResetDate = getCurrentDate()
    }

    private fun getLastResetDate(context: Context): String {
        val prefs = AppPreferences(context)
        return prefs.lastResetDate
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
