package com.example.mobilephone_water.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mobilephone_water.ui.fragments.OnboardingGenderFragment
import com.example.mobilephone_water.ui.fragments.OnboardingWeightFragment
import com.example.mobilephone_water.ui.fragments.OnboardingHeightFragment
import com.example.mobilephone_water.ui.fragments.OnboardingAgeFragment
import com.example.mobilephone_water.ui.fragments.OnboardingNameFragment
import com.example.mobilephone_water.ui.fragments.OnboardingActivityFragment
import com.example.mobilephone_water.ui.fragments.OnboardingResultFragment

class OnboardingPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 7

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingGenderFragment()
            1 -> OnboardingWeightFragment()
            2 -> OnboardingHeightFragment()
            3 -> OnboardingAgeFragment()
            4 -> OnboardingNameFragment()
            5 -> OnboardingActivityFragment()
            6 -> OnboardingResultFragment()
            else -> OnboardingGenderFragment()
        }
    }
}
