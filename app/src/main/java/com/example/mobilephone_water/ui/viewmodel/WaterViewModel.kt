package com.example.mobilephone_water.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobilephone_water.data.DailyResetManager
import com.example.mobilephone_water.data.database.WaterDatabase
import com.example.mobilephone_water.data.entity.DailyGoal
import com.example.mobilephone_water.data.entity.WaterRecord
import com.example.mobilephone_water.data.preferences.AppPreferences
import com.example.mobilephone_water.data.repository.WaterRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WaterRepository
    private val appPreferences: AppPreferences

    val allRecords: LiveData<List<WaterRecord>>
    val dailyGoal: LiveData<DailyGoal?>


    private val _averageWeekly = MutableLiveData<Int>()
    val averageWeekly: LiveData<Int> = _averageWeekly

    init {
        val database = WaterDatabase.getDatabase(application)
        val waterRecordDao = database.waterRecordDao()
        val dailyGoalDao = database.dailyGoalDao()

        repository = WaterRepository(waterRecordDao, dailyGoalDao)
        allRecords = repository.allRecords
        dailyGoal = repository.dailyGoal

        appPreferences = AppPreferences(application)


        checkAndResetDailyData()


        allRecords.observeForever {
            calculateWeeklyAverage()
        }


        calculateWeeklyAverage()
    }

    private fun checkAndResetDailyData() {
        if (DailyResetManager.shouldResetDailyData(getApplication())) {
            DailyResetManager.resetDailyData(getApplication())
        }
    }

    private fun calculateWeeklyAverage() {
        viewModelScope.launch {
            try {
                val average = repository.getAverageWaterIntake()
                _averageWeekly.postValue(average)
            } catch (e: Exception) {
                _averageWeekly.postValue(0)
            }
        }
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
        calculateWeeklyAverage()
    }

    fun updateRecord(record: WaterRecord) = viewModelScope.launch {
        repository.updateRecord(record)
        calculateWeeklyAverage()
    }

    fun deleteRecord(record: WaterRecord) = viewModelScope.launch {
        repository.deleteRecord(record)
        calculateWeeklyAverage()
    }

    fun deleteAllRecords() = viewModelScope.launch {
        repository.deleteAllRecords()
        calculateWeeklyAverage()
    }

    fun setDailyGoal(goalAmount: Int) = viewModelScope.launch {
        repository.setDailyGoal(goalAmount)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
