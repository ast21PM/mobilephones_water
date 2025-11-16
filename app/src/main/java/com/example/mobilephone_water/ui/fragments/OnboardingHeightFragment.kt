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

class OnboardingHeightFragment : Fragment() {

    private lateinit var seekBarHeight: SeekBar
    private lateinit var tvHeight: TextView
    private var selectedHeight = 170

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_height, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seekBarHeight = view.findViewById(R.id.seekBar_height)
        tvHeight = view.findViewById(R.id.tv_height)

        // ✅ Диапазон 100-230 см
        seekBarHeight.max = 130 // 230 - 100 = 130
        seekBarHeight.progress = 70 // 170 - 100 = 70 (стартовое значение)

        seekBarHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedHeight = progress + 100
                tvHeight.text = "$selectedHeight см"
                (activity as? OnboardingActivity)?.selectedHeight = selectedHeight
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        tvHeight.text = "$selectedHeight см"
    }
}
