package com.example.mobilephone_water.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.mobilephone_water.OnboardingActivity
import com.example.mobilephone_water.R

class OnboardingActivityFragment : Fragment() {

    private lateinit var btnRare: Button
    private lateinit var btnRegular: Button
    private lateinit var btnOften: Button
    private var selectedActivity = "Регулярно"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRare = view.findViewById(R.id.btn_rare)
        btnRegular = view.findViewById(R.id.btn_regular)
        btnOften = view.findViewById(R.id.btn_often)

        btnRare.setOnClickListener {
            selectedActivity = "Редко"
            updateButtonStates()
            (activity as? OnboardingActivity)?.selectedActivity = selectedActivity
        }

        btnRegular.setOnClickListener {
            selectedActivity = "Регулярно"
            updateButtonStates()
            (activity as? OnboardingActivity)?.selectedActivity = selectedActivity
        }

        btnOften.setOnClickListener {
            selectedActivity = "Часто"
            updateButtonStates()
            (activity as? OnboardingActivity)?.selectedActivity = selectedActivity
        }

        updateButtonStates()
    }

    private fun updateButtonStates() {
        val selectedColor = android.graphics.Color.parseColor("#0288D1")
        val unselectedColor = android.graphics.Color.parseColor("#333333")
        val selectedTextColor = android.graphics.Color.WHITE
        val unselectedTextColor = android.graphics.Color.parseColor("#888888")

        when (selectedActivity) {
            "Редко" -> {
                btnRare.backgroundTintList = android.content.res.ColorStateList.valueOf(selectedColor)
                btnRare.setTextColor(selectedTextColor)
                btnRegular.backgroundTintList = android.content.res.ColorStateList.valueOf(unselectedColor)
                btnRegular.setTextColor(unselectedTextColor)
                btnOften.backgroundTintList = android.content.res.ColorStateList.valueOf(unselectedColor)
                btnOften.setTextColor(unselectedTextColor)
            }
            "Регулярно" -> {
                btnRare.backgroundTintList = android.content.res.ColorStateList.valueOf(unselectedColor)
                btnRare.setTextColor(unselectedTextColor)
                btnRegular.backgroundTintList = android.content.res.ColorStateList.valueOf(selectedColor)
                btnRegular.setTextColor(selectedTextColor)
                btnOften.backgroundTintList = android.content.res.ColorStateList.valueOf(unselectedColor)
                btnOften.setTextColor(unselectedTextColor)
            }
            "Часто" -> {
                btnRare.backgroundTintList = android.content.res.ColorStateList.valueOf(unselectedColor)
                btnRare.setTextColor(unselectedTextColor)
                btnRegular.backgroundTintList = android.content.res.ColorStateList.valueOf(unselectedColor)
                btnRegular.setTextColor(unselectedTextColor)
                btnOften.backgroundTintList = android.content.res.ColorStateList.valueOf(selectedColor)
                btnOften.setTextColor(selectedTextColor)
            }
        }
    }
}
