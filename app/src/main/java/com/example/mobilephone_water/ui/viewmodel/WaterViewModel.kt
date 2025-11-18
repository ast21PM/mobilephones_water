package com.example.mobilephone_water.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobilephone_water.data.database.WaterDatabase
import com.example.mobilephone_water.data.entity.DailyGoal
import com.example.mobilephone_water.data.entity.WaterRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val waterRecordDao = WaterDatabase.getDatabase(application).waterRecordDao()
    private val dailyGoalDao = WaterDatabase.getDatabase(application).dailyGoalDao()

    val allRecords: LiveData<List<WaterRecord>> = waterRecordDao.getAllRecords()
    val dailyGoal: LiveData<DailyGoal?> = dailyGoalDao.getDailyGoal()

    private val _averageWeekly = MutableLiveData<Int>(0)
    val averageWeekly: LiveData<Int> = _averageWeekly

    fun getRecordsByDate(date: String): LiveData<List<WaterRecord>> {
        return waterRecordDao.getRecordsByDate(date)
    }

    fun getTotalAmountByDate(date: String): LiveData<Int?> {
        return waterRecordDao.getTotalAmountByDate(date)
    }

    fun calculateAverageWeekly() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val average = waterRecordDao.getAverageWeekly()
                _averageWeekly.postValue(average)
            } catch (e: Exception) {
                e.printStackTrace()
                _averageWeekly.postValue(0)
            }
        }
    }

    fun addWaterRecord(amount: Int) {
        viewModelScope.launch {
            try {
                val record = WaterRecord(
                    amount = amount,
                    timestamp = System.currentTimeMillis(),
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
                waterRecordDao.insert(record)
                calculateAverageWeekly()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setDailyGoal(goalAmount: Int) {
        viewModelScope.launch {
            try {
                val goal = DailyGoal(id = 1, goalAmount = goalAmount)
                dailyGoalDao.insertGoal(goal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAllRecordsForDate(date: String) {
        viewModelScope.launch {
            try {
                waterRecordDao.deleteRecordsByDate(date)
                calculateAverageWeekly()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
