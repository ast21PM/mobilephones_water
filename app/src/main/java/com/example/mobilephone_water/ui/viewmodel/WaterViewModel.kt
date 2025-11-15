package com.example.mobilephone_water.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mobilephone_water.data.database.WaterDatabase
import com.example.mobilephone_water.data.entity.DailyGoal
import com.example.mobilephone_water.data.entity.WaterRecord
import com.example.mobilephone_water.data.repository.WaterRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WaterRepository

    val allRecords: LiveData<List<WaterRecord>>
    val dailyGoal: LiveData<DailyGoal?>

    init {
        val database = WaterDatabase.getDatabase(application)
        val waterRecordDao = database.waterRecordDao()
        val dailyGoalDao = database.dailyGoalDao()

        repository = WaterRepository(waterRecordDao, dailyGoalDao)
        allRecords = repository.allRecords
        dailyGoal = repository.dailyGoal
    }

    fun getTotalAmountByDate(date: String): LiveData<Int?> {
        return repository.getTotalAmountByDate(date)
    }

    fun addWaterRecord(amount: Int) = viewModelScope.launch {
        val currentDate = getCurrentDate()
        val currentTimestamp = System.currentTimeMillis()

        val record = WaterRecord(
            amount = amount,
            timestamp = currentTimestamp,
            date = currentDate
        )

        repository.insertRecord(record)
    }

    fun updateRecord(record: WaterRecord) = viewModelScope.launch {
        repository.updateRecord(record)
    }

    fun deleteRecord(record: WaterRecord) = viewModelScope.launch {
        repository.deleteRecord(record)
    }

    fun deleteAllRecords() = viewModelScope.launch {
        repository.deleteAllRecords()
    }

    fun setDailyGoal(goalAmount: Int) = viewModelScope.launch {
        repository.setDailyGoal(goalAmount)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
