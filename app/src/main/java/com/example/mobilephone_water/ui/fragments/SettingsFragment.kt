package com.example.mobilephone_water.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.mobilephone_water.R
import com.example.mobilephone_water.data.notifications.NotificationScheduler
import com.example.mobilephone_water.data.preferences.AppPreferences

class SettingsFragment : Fragment() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var notificationScheduler: NotificationScheduler

    private lateinit var switchNotifications: Switch
    private lateinit var tvNotificationInterval: TextView
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var btnChangeInterval: Button
    private lateinit var btnChangeStartTime: Button
    private lateinit var btnChangeEndTime: Button
    private lateinit var btnResetSettings: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPreferences = AppPreferences(requireContext())
        notificationScheduler = NotificationScheduler(requireContext())

        initViews(view)
        loadSettings()
        setupListeners()
    }

    private fun initViews(view: View) {
        switchNotifications = view.findViewById(R.id.switch_notifications)
        tvNotificationInterval = view.findViewById(R.id.tv_notification_interval)
        tvStartTime = view.findViewById(R.id.tv_start_time)
        tvEndTime = view.findViewById(R.id.tv_end_time)
        btnChangeInterval = view.findViewById(R.id.btn_change_interval)
        btnChangeStartTime = view.findViewById(R.id.btn_change_start_time)
        btnChangeEndTime = view.findViewById(R.id.btn_change_end_time)
        btnResetSettings = view.findViewById(R.id.btn_reset_settings)
    }

    private fun loadSettings() {
        switchNotifications.isChecked = appPreferences.isNotificationEnabled


        val intervalMinutes = appPreferences.notificationInterval
        tvNotificationInterval.text = when (intervalMinutes) {
            30 -> "30 минут"
            60 -> "1 час"
            120 -> "2 часа"
            180 -> "3 часа"
            240 -> "4 часа"
            300 -> "5 часов"
            else -> "$intervalMinutes минут"
        }

        tvStartTime.text = appPreferences.notificationStartTime
        tvEndTime.text = appPreferences.notificationEndTime
    }

    private fun setupListeners() {
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.isNotificationEnabled = isChecked

            if (isChecked) {
                notificationScheduler.scheduleNotifications(appPreferences.notificationInterval / 60)
                Toast.makeText(
                    requireContext(),
                    "✓ Уведомления включены",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                notificationScheduler.cancelNotifications()
                Toast.makeText(
                    requireContext(),
                    "✗ Уведомления отключены",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnChangeInterval.setOnClickListener {
            showIntervalDialog()
        }

        btnChangeStartTime.setOnClickListener {
            showTimePickerDialog(true)
        }

        btnChangeEndTime.setOnClickListener {
            showTimePickerDialog(false)
        }

        btnResetSettings.setOnClickListener {
            showResetConfirmDialog()
        }
    }


    private fun showIntervalDialog() {
        val items = arrayOf("30 минут", "1 час", "2 часа", "3 часа", "4 часа", "5 часов")
        val minuteValues = intArrayOf(30, 60, 120, 180, 240, 300)

        val currentInterval = appPreferences.notificationInterval
        var selectedIndex = when (currentInterval) {
            30 -> 0
            60 -> 1
            120 -> 2
            180 -> 3
            240 -> 4
            300 -> 5
            else -> 2
        }

        AlertDialog.Builder(requireContext())
            .setTitle("⏰ Выбери интервал уведомлений")
            .setSingleChoiceItems(items, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("✓ Выбрать") { _, _ ->
                val intervalMinutes = minuteValues[selectedIndex]
                val intervalHours = intervalMinutes / 60

                appPreferences.notificationInterval = intervalMinutes
                tvNotificationInterval.text = items[selectedIndex]


                if (appPreferences.isNotificationEnabled) {
                    notificationScheduler.cancelNotifications()
                    notificationScheduler.scheduleNotifications(intervalHours)
                }

                Toast.makeText(
                    requireContext(),
                    "✓ Интервал изменен на ${items[selectedIndex]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("✗ Отмена", null)
            .show()
    }

    private fun showTimePickerDialog(isStartTime: Boolean) {
        val currentTime = if (isStartTime) {
            appPreferences.notificationStartTime
        } else {
            appPreferences.notificationEndTime
        }

        val hours = currentTime.split(":")[0].toInt()
        val minutes = currentTime.split(":")[1].toInt()

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.time_picker_dialog, null)

        val hourPicker = view.findViewById<NumberPicker>(R.id.hour_picker).apply {
            minValue = 0
            maxValue = 23
            value = hours
        }

        val minutePicker = view.findViewById<NumberPicker>(R.id.minute_picker).apply {
            minValue = 0
            maxValue = 59
            value = minutes
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (isStartTime) "⏰ Начало уведомлений" else "⏰ Конец уведомлений")
            .setView(view)
            .setPositiveButton("✓ Установить") { _, _ ->
                val selectedHour = String.format("%02d", hourPicker.value)
                val selectedMinute = String.format("%02d", minutePicker.value)
                val selectedTime = "$selectedHour:$selectedMinute"

                if (isStartTime) {
                    appPreferences.notificationStartTime = selectedTime
                    tvStartTime.text = selectedTime
                } else {
                    appPreferences.notificationEndTime = selectedTime
                    tvEndTime.text = selectedTime
                }

                Toast.makeText(
                    requireContext(),
                    "✓ Время изменено на $selectedTime",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("✗ Отмена", null)
            .show()
    }

    private fun showResetConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Сброс настроек")
            .setMessage("Вы уверены? Все настройки вернутся к стандартным значениям.")
            .setPositiveButton("✓ Да") { _, _ ->
                appPreferences.isNotificationEnabled = true
                appPreferences.notificationInterval = 120 // 2 часа в минутах
                appPreferences.notificationStartTime = "08:00"
                appPreferences.notificationEndTime = "22:00"

                loadSettings()
                notificationScheduler.cancelNotifications()
                notificationScheduler.scheduleNotifications(2) // 2 часа

                Toast.makeText(
                    requireContext(),
                    "✓ Настройки сброшены",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("✗ Отмена", null)
            .show()
    }
}
