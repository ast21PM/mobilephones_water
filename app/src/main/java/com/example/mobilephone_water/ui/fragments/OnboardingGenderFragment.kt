package com.example.mobilephone_water.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.mobilephone_water.OnboardingActivity
import com.example.mobilephone_water.R

class OnboardingGenderFragment : Fragment() {

    private lateinit var btnMale: Button
    private lateinit var btnFemale: Button
    private var selectedGender = "Мужской"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_gender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMale = view.findViewById(R.id.btn_male)
        btnFemale = view.findViewById(R.id.btn_female)

        btnMale.setOnClickListener {
            selectedGender = "Мужской"
            updateButtonStates()
            (activity as? OnboardingActivity)?.selectedGender = selectedGender
        }

        btnFemale.setOnClickListener {
            selectedGender = "Женский"
            updateButtonStates()
            (activity as? OnboardingActivity)?.selectedGender = selectedGender
        }

        // По умолчанию выбран мужской
        updateButtonStates()
    }

    private fun updateButtonStates() {
        if (selectedGender == "Мужской") {
            btnMale.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#0288D1")
            )
            btnMale.setTextColor(android.graphics.Color.WHITE)
            btnFemale.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#333333")
            )
            btnFemale.setTextColor(android.graphics.Color.parseColor("#888888"))
        } else {
            btnMale.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#333333")
            )
            btnMale.setTextColor(android.graphics.Color.parseColor("#888888"))
            btnFemale.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#E91E63")
            )
            btnFemale.setTextColor(android.graphics.Color.WHITE)
        }
    }
}
