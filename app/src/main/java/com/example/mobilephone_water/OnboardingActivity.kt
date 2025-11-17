package com.example.mobilephone_water

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.mobilephone_water.data.preferences.AppPreferences
import com.example.mobilephone_water.ui.adapters.OnboardingPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button  // ✅ НОВАЯ КНОПКА
    private lateinit var btnSkip: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var appPreferences: AppPreferences

    var selectedGender: String = "Мужской"
    var selectedWeight: Int = 70
    var selectedHeight: Int = 170
    var selectedAge: Int = 25
    var selectedName: String = ""
    var selectedActivity: String = "Регулярно"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        appPreferences = AppPreferences(this)

        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btn_next)
        btnBack = findViewById(R.id.btn_back)  // ✅ НОВАЯ КНОПКА
        btnSkip = findViewById(R.id.btn_skip)
        tabLayout = findViewById(R.id.tabLayout)

        val adapter = OnboardingPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // ✅ КНОПКА "ДАЛЕЕ"
        btnNext.setOnClickListener {
            if (viewPager.currentItem < 6) {
                viewPager.currentItem += 1
            } else {
                saveDataAndFinish()
            }
        }

        // ✅ НОВАЯ КНОПКА "НАЗАД"
        btnBack.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem -= 1
            }
        }

        // ✅ КНОПКА "ПРОПУСТИТЬ"
        btnSkip.setOnClickListener {
            saveDataAndFinish()
        }

        // ✅ ИЗМЕНЕНИЕ ТЕКСТА И ВИДИМОСТИ КНОПОК ПРИ ПЕРЕХОДЕ
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // ✅ КНОПКА NEXT
                btnNext.text = if (position == 6) "Завершить" else "Далее"

                // ✅ СКРЫТЬ КНОПКУ BACK НА ПЕРВОЙ СТРАНИЦЕ
                btnBack.isEnabled = position > 0
                btnBack.alpha = if (position > 0) 1f else 0.5f  // Полупрозрачная на первой странице

                // ✅ СКРЫТЬ КНОПКУ SKIP НА ПОСЛЕДНЕЙ СТРАНИЦЕ
                btnSkip.isEnabled = position < 6
                btnSkip.alpha = if (position < 6) 1f else 0.5f
            }
        })
    }

    private fun saveDataAndFinish() {
        val dailyWater = calculateDailyWater()

        appPreferences.saveUserProfile(
            selectedGender,
            selectedWeight,
            selectedHeight,
            selectedAge,
            selectedName,
            selectedActivity,
            dailyWater
        )
        appPreferences.isFirstLaunch = false

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun calculateDailyWater(): Int {
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
