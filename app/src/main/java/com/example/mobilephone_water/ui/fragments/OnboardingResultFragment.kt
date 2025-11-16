package com.example.mobilephone_water.ui.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobilephone_water.OnboardingActivity
import com.example.mobilephone_water.R

class OnboardingResultFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvWaterAmount: TextView
    private lateinit var tvWaterInfo: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        tvStatus = view.findViewById(R.id.tv_status)
        tvResult = view.findViewById(R.id.tv_result)
        tvWaterAmount = view.findViewById(R.id.tv_water_amount)
        tvWaterInfo = view.findViewById(R.id.tv_water_info)

        // Скрываем результаты изначально
        tvResult.alpha = 0f
        tvWaterAmount.alpha = 0f
        tvWaterInfo.alpha = 0f

        // Запускаем анимацию загрузки
        startLoadingAnimation()
    }

    private fun startLoadingAnimation() {
        val activity = activity as? OnboardingActivity ?: return

        // Симуляция загрузки 3 секунды
        ValueAnimator.ofInt(0, 100).apply {
            duration = 3000
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                progressBar.progress = progress

                when (progress) {
                    25 -> tvStatus.text = "⏳ Загружаю пол..."
                    50 -> tvStatus.text = "⏳ Загружаю вес/рост..."
                    75 -> tvStatus.text = "⏳ Загружаю активность..."
                    100 -> {
                        tvStatus.text = "✅ Расчет завершен!"
                        showResults(activity)
                    }
                }
            }
            start()
        }
    }

    private fun showResults(activity: OnboardingActivity) {
        // Рассчитываем воду
        val dailyWater = calculateDailyWater(activity)

        // Анимация появления результата
        tvResult.animate()
            .alpha(1f)
            .setDuration(500)
            .start()

        tvWaterAmount.animate()
            .alpha(1f)
            .setDuration(700)
            .start()

        tvWaterInfo.animate()
            .alpha(1f)
            .setDuration(900)
            .start()

        // Устанавливаем текст
        tvResult.text = "💧 Ваша ежедневная норма воды:"
        tvWaterAmount.text = "${dailyWater / 1000}.${(dailyWater % 1000) / 100} л"

        val info = buildString {
            append("👤 ${activity.selectedGender}\n")
            append("⚖️ ${activity.selectedWeight} кг • ")
            append("📏 ${activity.selectedHeight} см • ")
            append("🎂 ${activity.selectedAge} лет\n")
            append("🏃 ${activity.selectedActivity}")
        }
        tvWaterInfo.text = info
    }

    private fun calculateDailyWater(activity: OnboardingActivity): Int {
        val selectedGender = activity.selectedGender
        val selectedWeight = activity.selectedWeight
        val selectedHeight = activity.selectedHeight
        val selectedAge = activity.selectedAge
        val selectedActivity = activity.selectedActivity

        // ✅ НАУЧНАЯ ФОРМУЛА Харриса-Бенедикта
        val bmr = if (selectedGender == "Мужской") {
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
        val finalWater = if (selectedGender == "Мужской") {
            (totalWater * 1.12).toInt()
        } else {
            totalWater
        }

        return finalWater.coerceIn(1500, 4000)
    }
}
