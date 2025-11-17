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

class OnboardingAgeFragment : Fragment() {

    private lateinit var seekBarAge: SeekBar
    private lateinit var tvAge: TextView
    private var selectedAge = 25

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_age, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seekBarAge = view.findViewById(R.id.seekBar_age)
        tvAge = view.findViewById(R.id.tv_age)

        
        seekBarAge.max = 100 
        seekBarAge.progress = 25 

        seekBarAge.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedAge = progress
                tvAge.text = "$selectedAge лет"
                (activity as? OnboardingActivity)?.selectedAge = selectedAge
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        tvAge.text = "$selectedAge лет"
    }
}
