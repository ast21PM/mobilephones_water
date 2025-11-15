package com.example.mobilephone_water

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobilephone_water.data.notifications.NotificationScheduler
import com.example.mobilephone_water.data.preferences.AppPreferences
import com.example.mobilephone_water.ui.viewmodel.WaterViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var viewModel: WaterViewModel
    private lateinit var appPreferences: AppPreferences

    private lateinit var tvTotalAmount: TextView
    private lateinit var tvGoal: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var etWaterAmount: EditText
    private lateinit var etGoalAmount: EditText
    private lateinit var btnAdd250ml: Button
    private lateinit var btnAdd500ml: Button
    private lateinit var btnAddCustom: Button
    private lateinit var btnSetGoal: Button
    private lateinit var btnRemove100ml: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[WaterViewModel::class.java]
        appPreferences = AppPreferences(requireContext())

        if (appPreferences.isFirstLaunch) {
            viewModel.setDailyGoal(2000)
            appPreferences.isFirstLaunch = false
            Toast.makeText(requireContext(), "Добро пожаловать! Цель: 2000 мл", Toast.LENGTH_SHORT).show()
        }

        initViews(view)
        observeData()
        setupButtons()


        val notificationScheduler = NotificationScheduler(requireContext())
        if (!notificationScheduler.isNotificationEnabled()) {
            notificationScheduler.scheduleNotifications(intervalHours = 2)
        }

        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
    }

    private fun initViews(view: View) {
        tvTotalAmount = view.findViewById(R.id.tv_total_amount)
        tvGoal = view.findViewById(R.id.tv_goal)
        progressBar = view.findViewById(R.id.progress_bar)
        etWaterAmount = view.findViewById(R.id.et_water_amount)
        etGoalAmount = view.findViewById(R.id.et_goal_amount)
        btnAdd250ml = view.findViewById(R.id.btn_add_250ml)
        btnAdd500ml = view.findViewById(R.id.btn_add_500ml)
        btnAddCustom = view.findViewById(R.id.btn_add_custom)
        btnSetGoal = view.findViewById(R.id.btn_set_goal)
        btnRemove100ml = view.findViewById(R.id.btn_remove_100ml)
    }

    private fun observeData() {
        val currentDate = getCurrentDate()

        viewModel.getTotalAmountByDate(currentDate).observe(viewLifecycleOwner) { total ->
            val amount = total ?: 0
            tvTotalAmount.text = "Выпито: $amount мл"
            updateProgressBar()
        }

        viewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            val goalAmount = goal?.goalAmount ?: 2000
            tvGoal.text = "Цель: $goalAmount мл"
            etGoalAmount.setText(goalAmount.toString())
            updateProgressBar()
        }
    }

    private fun updateProgressBar() {
        val currentTotal = tvTotalAmount.text.toString()
            .replace("Выпито: ", "")
            .replace(" мл", "")
            .toIntOrNull() ?: 0
        val goal = tvGoal.text.toString()
            .replace("Цель: ", "")
            .replace(" мл", "")
            .toIntOrNull() ?: 2000

        val progress = if (goal > 0) {
            ((currentTotal.toDouble() / goal) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }

        progressBar.progress = progress
    }

    private fun setupButtons() {
        btnAdd250ml.setOnClickListener {
            viewModel.addWaterRecord(250)
            etWaterAmount.setText("")
            Toast.makeText(requireContext(), "✓ +250 мл добавлено", Toast.LENGTH_SHORT).show()
        }

        btnAdd500ml.setOnClickListener {
            viewModel.addWaterRecord(500)
            etWaterAmount.setText("")
            Toast.makeText(requireContext(), "✓ +500 мл добавлено", Toast.LENGTH_SHORT).show()
        }

        btnAddCustom.setOnClickListener {
            val amount = etWaterAmount.text.toString().toIntOrNull()
            if (amount != null && amount > 0) {
                viewModel.addWaterRecord(amount)
                etWaterAmount.setText("")
                Toast.makeText(requireContext(), "✓ +$amount мл добавлено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "✗ Введите правильное значение", Toast.LENGTH_SHORT).show()
            }
        }

        btnSetGoal.setOnClickListener {
            val goal = etGoalAmount.text.toString().toIntOrNull()
            if (goal != null && goal > 0) {
                viewModel.setDailyGoal(goal)
                Toast.makeText(requireContext(), "✓ Цель изменена на $goal мл", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "✗ Введите правильное значение", Toast.LENGTH_SHORT).show()
            }
        }

        btnRemove100ml.setOnClickListener {
            val currentTotal = tvTotalAmount.text.toString()
                .replace("Выпито: ", "")
                .replace(" мл", "")
                .toIntOrNull() ?: 0

            if (currentTotal >= 100) {
                viewModel.addWaterRecord(-100)
                Toast.makeText(requireContext(), "✓ -100 мл удалено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "✗ Недостаточно воды для удаления", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Use MenuProvider instead")
    @Suppress("DEPRECATION")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    @Deprecated("Use MenuProvider instead")
    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController().navigate(R.id.settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
