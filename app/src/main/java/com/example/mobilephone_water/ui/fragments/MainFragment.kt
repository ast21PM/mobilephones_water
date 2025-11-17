package com.example.mobilephone_water

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobilephone_water.data.notifications.NotificationScheduler
import com.example.mobilephone_water.data.preferences.AppPreferences
import com.example.mobilephone_water.ui.views.WaterProgressView
import com.example.mobilephone_water.ui.viewmodel.WaterViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var viewModel: WaterViewModel
    private lateinit var appPreferences: AppPreferences

    private lateinit var waterProgressView: WaterProgressView
    private lateinit var btnAdd250ml: Button
    private lateinit var btnAdd330ml: Button
    private lateinit var btnAdd500ml: Button
    private lateinit var btnAdd1l: Button
    private lateinit var btnMinus100ml: Button
    private lateinit var btnReset: Button
    private lateinit var tvGoal: TextView
    private var dailyGoal = 2200

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

        
        dailyGoal = appPreferences.getDailyWaterGoal()

        
        if (dailyGoal == 2200) {
            
            val savedWater = appPreferences.getDailyWaterGoal()
            if (savedWater > 0) {
                dailyGoal = savedWater
            }
        }

        viewModel.setDailyGoal(dailyGoal)

        initViews(view)
        observeData()
        setupButtons()
        animateViewsOnStart()

        val notificationScheduler = NotificationScheduler(requireContext())
        if (!notificationScheduler.isNotificationEnabled()) {
            notificationScheduler.scheduleNotifications(intervalHours = 2)
        }

        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
    }

    private fun initViews(view: View) {
        waterProgressView = view.findViewById(R.id.water_progress_view)
        btnAdd250ml = view.findViewById(R.id.btn_add_250ml)
        btnAdd330ml = view.findViewById(R.id.btn_add_330ml)
        btnAdd500ml = view.findViewById(R.id.btn_add_500ml)
        btnAdd1l = view.findViewById(R.id.btn_add_1l)
        btnMinus100ml = view.findViewById(R.id.btn_minus_100ml)
        btnReset = view.findViewById(R.id.btn_reset)
        tvGoal = view.findViewById(R.id.tv_goal)

        tvGoal.setOnClickListener {
            showChangeGoalDialog()
        }
    }

    private fun observeData() {
        val currentDate = getCurrentDate()

     
        viewModel.setDailyGoal(dailyGoal)
        waterProgressView.setDailyGoal(dailyGoal)
        updateGoalText()

        viewModel.getTotalAmountByDate(currentDate)?.observe(viewLifecycleOwner) { total ->
            val amount = total ?: 0
            val progress = (amount.toFloat() / dailyGoal) * 100f
            waterProgressView.setProgress(progress, animated = true)
        }
    }

    private fun setupButtons() {
        btnAdd250ml.setOnClickListener {
            animateButtonClick(it)
            viewModel.addWaterRecord(200)
            showSuccessToast("💧 +200 мл добавлено")
        }

        btnAdd330ml.setOnClickListener {
            animateButtonClick(it)
            viewModel.addWaterRecord(330)
            showSuccessToast("💧 +330 мл добавлено")
        }

        btnAdd500ml.setOnClickListener {
            animateButtonClick(it)
            viewModel.addWaterRecord(500)
            showSuccessToast("💧 +500 мл добавлено")
        }

        btnAdd1l.setOnClickListener {
            animateButtonClick(it)
            viewModel.addWaterRecord(1000)
            showSuccessToast("💧 +1л добавлено")
        }

        btnMinus100ml.setOnClickListener {
            animateButtonClick(it)
            viewModel.addWaterRecord(-100)
            showSuccessToast("💧 -100 мл вычтено")
        }

        btnReset.setOnClickListener {
            animateButtonClick(it)
            showResetConfirmDialog()
        }
    }

    private fun showResetConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("🔄 Сброс данных")
            .setMessage("Вы уверены? Все записи за сегодня будут удалены.")
            .setPositiveButton("✓ Да") { dialog, _ ->
                resetTodayData()
                dialog.dismiss()
            }
            .setNegativeButton("✗ Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun resetTodayData() {
        val currentDate = getCurrentDate()
        viewModel.deleteAllRecordsForDate(currentDate)
        showSuccessToast("🔄 Данные сброшены")
    }

    private fun showChangeGoalDialog() {
        val editText = EditText(requireContext()).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            hint = "Введите цель (мл)"
            setText(dailyGoal.toString())
        }

        AlertDialog.Builder(requireContext())
            .setTitle("🎯 Изменить дневную цель")
            .setMessage("Текущая цель: $dailyGoal мл")
            .setView(editText)
            .setPositiveButton("✓ Сохранить") { dialog, _ ->
                val newGoal = editText.text.toString().toIntOrNull()
                if (newGoal != null && newGoal > 0 && newGoal <= 10000) {
                    dailyGoal = newGoal
                    viewModel.setDailyGoal(newGoal)
                    waterProgressView.setDailyGoal(newGoal)
                    updateGoalText()
                    Toast.makeText(
                        requireContext(),
                        "✅ Цель изменена на $newGoal мл",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "❌ Введите корректное значение (1-10000)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("✗ Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateGoalText() {
        tvGoal.text = "🎯 Цель: $dailyGoal мл в день (нажми для изменения)"
    }

    private fun animateButtonClick(view: View) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun animateViewsOnStart() {
        waterProgressView.alpha = 0f
        waterProgressView.scaleX = 0.8f
        waterProgressView.scaleY = 0.8f
        waterProgressView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    private fun showSuccessToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
