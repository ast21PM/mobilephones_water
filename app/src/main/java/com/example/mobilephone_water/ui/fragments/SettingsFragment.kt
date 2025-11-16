package com.example.mobilephone_water.ui.fragments

import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.mobilephone_water.data.notifications.NotificationScheduler
import com.example.mobilephone_water.data.preferences.AppPreferences
import com.example.mobilephone_water.R

class SettingsFragment : Fragment() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var notificationScheduler: NotificationScheduler

    private lateinit var switchNotifications: Switch
    private lateinit var tvNotificationInterval: TextView
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var spinnerSound: Spinner
    private lateinit var btnChangeInterval: Button
    private lateinit var btnChangeStartTime: Button
    private lateinit var btnChangeEndTime: Button
    private lateinit var btnResetSettings: Button
    private lateinit var btnPrivacyPolicy: Button

    private var mediaPlayer: MediaPlayer? = null
    private var isInitialLoad = true

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
        spinnerSound = view.findViewById(R.id.spinner_sound)
        btnChangeInterval = view.findViewById(R.id.btn_change_interval)
        btnChangeStartTime = view.findViewById(R.id.btn_change_start_time)
        btnChangeEndTime = view.findViewById(R.id.btn_change_end_time)
        btnResetSettings = view.findViewById(R.id.btn_reset_settings)
        btnPrivacyPolicy = view.findViewById(R.id.btn_privacy_policy)
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

        setupSoundSpinner()
    }

    private fun setupSoundSpinner() {
        val soundOptions = arrayOf(
            "🔊 Капля",
            "🔊 Треск",
            "🔊 Писк"
        )

        val soundValues = arrayOf("droplet", "squeak", "bell")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, soundOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSound.adapter = adapter

        val currentSound = appPreferences.notificationSound
        val currentIndex = soundValues.indexOf(currentSound).coerceAtLeast(0)
        spinnerSound.setSelection(currentIndex)

        // ✅ СЛУШАТЕЛЬ УСТАНАВЛИВАЕТСЯ ПОСЛЕ setSelection()
        spinnerSound.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                // ✅ Пропускаем первую инициализацию (при открытии вкладки)
                if (isInitialLoad) {
                    isInitialLoad = false
                    return
                }

                // ✅ Сохраняем выбранный звук
                appPreferences.notificationSound = soundValues[position]

                // ✅ Сразу воспроизводим звук при выборе
                playTestSound(soundValues[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupListeners() {
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.isNotificationEnabled = isChecked

            if (isChecked) {
                // ✅ ВКЛЮЧЕНИЕ УВЕДОМЛЕНИЙ
                notificationScheduler.scheduleNotifications(appPreferences.notificationInterval / 60)

                // ✅ СОЗДАЁМ КАНАЛ УВЕДОМЛЕНИЙ ЕСЛИ ЕГО НЕТ
                createNotificationChannel()

                Toast.makeText(
                    requireContext(),
                    "✅ Уведомления включены",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // ✅ ОТКЛЮЧЕНИЕ УВЕДОМЛЕНИЙ
                notificationScheduler.cancelNotifications()

                // ✅ УДАЛЯЕМ КАНАЛ УВЕДОМЛЕНИЙ ИЗ СИСТЕМЫ
                deleteNotificationChannel()

                Toast.makeText(
                    requireContext(),
                    "⛔ Уведомления отключены",
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

        btnPrivacyPolicy.setOnClickListener {
            showPrivacyPolicy()
        }
    }

    // ✅ СОЗДАНИЕ КАНАЛА УВЕДОМЛЕНИЙ
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = android.app.NotificationChannel(
                "water_reminder",
                "💧 Напоминание пить воду",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Регулярные напоминания о питье воды"

            notificationManager.createNotificationChannel(channel)
        }
    }

    // ✅ УДАЛЕНИЕ КАНАЛА УВЕДОМЛЕНИЙ
    private fun deleteNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.deleteNotificationChannel("water_reminder")
        }
    }

    // ✅ ФУНКЦИЯ ВОСПРОИЗВЕДЕНИЯ ЗВУКА
    private fun playTestSound(soundType: String) {
        // Останови предыдущий звук если он играет
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }

        try {
            val soundResId = when (soundType) {
                "droplet" -> R.raw.droplet
                "squeak" -> R.raw.squeak
                "bell" -> R.raw.bell
                else -> R.raw.droplet
            }

            mediaPlayer = MediaPlayer.create(requireContext(), soundResId)
            mediaPlayer?.apply {
                setVolume(0.5f, 0.5f)
                isLooping = false
                start()

                // Останови через 1 секунду
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isPlaying) {
                        stop()
                        release()
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "❌ Ошибка воспроизведения", Toast.LENGTH_SHORT).show()
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
                    // ✅ ДОБАВЬ ПЕРЕСОЗДАНИЕ КАНАЛА
                    createNotificationChannel()

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

    private fun showPrivacyPolicy() {
        val privacyText = """
            📜 Политика конфиденциальности приложений для Android
            
            Мы создавали приложения с уважением к конфиденциальности пользователей. Наши приложения не собирают никаких данных или информации от телефона и пользователя. Но чтобы приложения работали должным образом, иногда мы запрашиваем некоторые разрешения для:
            
            🔔 С приложением напоминания о питье воды: Это разрешение используется только для доступа к звуковому сигналу вашего телефона в качестве напоминания.
            
            ✅ Мы гарантируем безопасность ваших данных!
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("📋 Политика конфиденциальности")
            .setMessage(privacyText)
            .setPositiveButton("✓ Закрыть") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showResetConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Сброс настроек")
            .setMessage("Вы уверены? Все настройки вернутся к стандартным значениям.")
            .setPositiveButton("✓ Да") { _, _ ->
                appPreferences.isNotificationEnabled = true
                appPreferences.notificationInterval = 120
                appPreferences.notificationStartTime = "08:00"
                appPreferences.notificationEndTime = "22:00"
                appPreferences.notificationSound = "droplet"

                // ✅ СБРАСЫВАЕМ флаг чтобы не было Toast при загрузке
                isInitialLoad = true

                loadSettings()

                // ✅ СОЗДАЁМ КАНАЛ ЕСЛИ УВЕДОМЛЕНИЯ ВКЛЮЧЕНЫ
                createNotificationChannel()

                notificationScheduler.cancelNotifications()
                notificationScheduler.scheduleNotifications(2)

                Toast.makeText(
                    requireContext(),
                    "✓ Настройки сброшены",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("✗ Отмена", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
            }
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }
}
