package com.example.mobilephone_water.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobilephone_water.OnboardingActivity
import com.example.mobilephone_water.R

class OnboardingWeightFragment : Fragment() {

    private lateinit var seekBarWeight: SeekBar
    private lateinit var tvWeight: TextView
    private var selectedWeight = 70

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_weight, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seekBarWeight = view.findViewById(R.id.seekBar_weight)
        tvWeight = view.findViewById(R.id.tv_weight)

        // ✅ Диапазон 0-300 кг
        seekBarWeight.max = 300 // 0-300 = 300
        seekBarWeight.progress = 70 // Стартовое значение 70

        seekBarWeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedWeight = progress
                tvWeight.text = "$selectedWeight кг"
                (activity as? OnboardingActivity)?.selectedWeight = selectedWeight
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        tvWeight.text = "$selectedWeight кг"
    }
}
