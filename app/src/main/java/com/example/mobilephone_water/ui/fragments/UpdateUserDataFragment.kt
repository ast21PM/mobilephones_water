package com.example.mobilephone_water.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobilephone_water.R
import com.example.mobilephone_water.data.preferences.AppPreferences
import com.example.mobilephone_water.ui.viewmodel.WaterViewModel

class UpdateUserDataFragment : Fragment() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var viewModel: WaterViewModel

    private lateinit var npWeight: NumberPicker
    private lateinit var npHeight: NumberPicker
    private lateinit var npAge: NumberPicker
    private lateinit var btnActivityLow: Button
    private lateinit var btnActivityMed: Button
    private lateinit var btnActivityHigh: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var selectedWeight = 70
    private var selectedHeight = 170
    private var selectedAge = 25
    private var selectedActivity = "Регулярно"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_user_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPreferences = AppPreferences(requireContext())
        viewModel = ViewModelProvider(this)[WaterViewModel::class.java]

        initViews(view)
        setupNumberPickers()
        setupActivityButtons()
        setupListeners()
    }

    private fun initViews(view: View) {
        npWeight = view.findViewById(R.id.np_weight)
        npHeight = view.findViewById(R.id.np_height)
        npAge = view.findViewById(R.id.np_age)
        btnActivityLow = view.findViewById(R.id.btn_activity_low)
        btnActivityMed = view.findViewById(R.id.btn_activity_med)
        btnActivityHigh = view.findViewById(R.id.btn_activity_high)
        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)

        // ✅ ПРИНУДИТЕЛЬНО УСТАНАВЛИВАЕМ БЕЛЫЙ ТЕКСТ ДЛЯ NUMBERPICKER
        setNumberPickerTextColor(npWeight, android.graphics.Color.WHITE)
        setNumberPickerTextColor(npHeight, android.graphics.Color.WHITE)
        setNumberPickerTextColor(npAge, android.graphics.Color.WHITE)
    }

    private fun setupNumberPickers() {
        npWeight.apply {
            minValue = 40
            maxValue = 150
            value = selectedWeight
            setOnValueChangedListener { _, _, newVal ->
                selectedWeight = newVal
            }
        }

        npHeight.apply {
            minValue = 140
            maxValue = 220
            value = selectedHeight
            setOnValueChangedListener { _, _, newVal ->
                selectedHeight = newVal
            }
        }

        npAge.apply {
            minValue = 10
            maxValue = 100
            value = selectedAge
            setOnValueChangedListener { _, _, newVal ->
                selectedAge = newVal
            }
        }
    }

    // ✅ МЕТОД ДЛЯ УСТАНОВКИ БЕЛОГО ТЕКСТА НЕЗАВИСИМО ОТ ТЕМЫ
    private fun setNumberPickerTextColor(picker: NumberPicker, color: Int) {
        try {
            // ВСЕ ПОЛЯ, КОТОРЫЕ МОГУТ ВЛИЯТЬ НА ЦВЕТ
            val fields = arrayOf(
                "mSelectorWheelPaint",
                "mSelectedText",
                "mIncrementButton",
                "mDecrementButton"
            )

            for (fieldName in fields) {
                try {
                    val field = NumberPicker::class.java.getDeclaredField(fieldName)
                    field.isAccessible = true
                    val obj = field.get(picker)

                    if (obj is android.graphics.Paint) {
                        obj.color = color
                    }
                } catch (e: Exception) {
                    // Некоторые поля могут не существовать, игнорируем
                }
            }

            // ✅ ПРИНУДИТЕЛЬНО УСТАНАВЛИВАЕМ ЦВЕТ ТЕКСТА
            picker.setTextColor(color)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupActivityButtons() {
        btnActivityLow.setOnClickListener {
            selectedActivity = "Редко"
            updateActivityButtons()
        }

        btnActivityMed.setOnClickListener {
            selectedActivity = "Регулярно"
            updateActivityButtons()
        }

        btnActivityHigh.setOnClickListener {
            selectedActivity = "Часто"
            updateActivityButtons()
        }
    }

    private fun updateActivityButtons() {
        listOf(btnActivityLow, btnActivityMed, btnActivityHigh).forEach { btn ->
            btn.alpha = 0.5f
        }

        when (selectedActivity) {
            "Редко" -> btnActivityLow.alpha = 1f
            "Регулярно" -> btnActivityMed.alpha = 1f
            "Часто" -> btnActivityHigh.alpha = 1f
        }
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveAndReturn()
        }

        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun saveAndReturn() {
        val dailyWater = calculateDailyWater()

        val gender = "Мужской"
        val name = appPreferences.getUserName()

        appPreferences.saveUserProfile(
            gender,
            selectedWeight,
            selectedHeight,
            selectedAge,
            name,
            selectedActivity,
            dailyWater
        )

        viewModel.setDailyGoal(dailyWater)

        Toast.makeText(
            requireContext(),
            "✅ Данные обновлены! Дневная норма: $dailyWater мл",
            Toast.LENGTH_SHORT
        ).show()

        findNavController().navigateUp()
    }

    private fun calculateDailyWater(): Int {
        val gender = "Мужской"

        val bmr = if (gender == "Мужской") {
            88.362 + (13.397 * selectedWeight) + (4.799 * selectedHeight) - (5.677 * selectedAge)
        } else {
            447.593 + (9.247 * selectedWeight) + (3.098 * selectedHeight) - (4.330 * selectedAge)
        }

        val activityMultiplier = when (selectedActivity) {
            "Редко" -> 1.2
            "Регулярно" -> 1.55
            "Часто" -> 1.725
            else -> 1.55
        }

        val tdee = (bmr * activityMultiplier).toInt()
        val waterFromCalories = (tdee * 0.5).toInt()
        val waterFromWeight = selectedWeight * 35
        val averageWater = (waterFromCalories + waterFromWeight) / 2

        val extraWaterForActivity = when (selectedActivity) {
            "Редко" -> 0
            "Регулярно" -> 300
            "Часто" -> 500
            else -> 300
        }

        val totalWater = averageWater + extraWaterForActivity

        val finalWater = if (gender == "Мужской") {
            (totalWater * 1.12).toInt()
        } else {
            totalWater
        }

        return finalWater.coerceIn(1500, 4000)
    }
}
