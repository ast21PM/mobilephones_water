package com.example.mobilephone_water

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mobilephone_water.ui.viewmodel.WaterViewModel
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private lateinit var viewModel: WaterViewModel

    private lateinit var tvTodayAmount: TextView
    private lateinit var tvTodayGoal: TextView
    private lateinit var progressToday: ProgressBar
    private lateinit var tvWeekAmount: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[WaterViewModel::class.java]

        initViews(view)
        observeData()
    }

    private fun initViews(view: View) {
        tvTodayAmount = view.findViewById(R.id.tv_today_amount)
        tvTodayGoal = view.findViewById(R.id.tv_today_goal)
        progressToday = view.findViewById(R.id.progress_today)
        tvWeekAmount = view.findViewById(R.id.tv_week_amount)
    }

    private fun observeData() {
        val currentDate = getCurrentDate()

        viewModel.getTotalAmountByDate(currentDate).observe(viewLifecycleOwner) { total ->
            val amount = total ?: 0
            tvTodayAmount.text = "$amount мл"
            updateProgressBar(amount)
        }

        viewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            val goalAmount = goal?.goalAmount ?: 2000
            tvTodayGoal.text = "Цель: $goalAmount мл"
        }

        tvWeekAmount.text = "~2000 мл/день"
    }

    private fun updateProgressBar(currentAmount: Int) {
        val goal = tvTodayGoal.text.toString()
            .replace("Цель: ", "").replace(" мл", "").toIntOrNull() ?: 2000

        val progress = if (goal > 0) {
            ((currentAmount.toDouble() / goal) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }

        progressToday.progress = progress
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
