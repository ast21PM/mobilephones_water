package com.example.mobilephone_water.data.repository

import androidx.lifecycle.LiveData
import com.example.mobilephone_water.data.dao.DailyGoalDao
import com.example.mobilephone_water.data.dao.WaterRecordDao
import com.example.mobilephone_water.data.entity.DailyGoal
import com.example.mobilephone_water.data.entity.WaterRecord

class WaterRepository(
    private val waterRecordDao: WaterRecordDao,
    private val dailyGoalDao: DailyGoalDao
) {

    val allRecords: LiveData<List<WaterRecord>> = waterRecordDao.getAllRecords()

    fun getRecordsByDate(date: String): LiveData<List<WaterRecord>> {
        return waterRecordDao.getRecordsByDate(date)
    }

    fun getTotalAmountByDate(date: String): LiveData<Int?> {
        return waterRecordDao.getTotalAmountByDate(date)
    }

    suspend fun insertRecord(record: WaterRecord) {
        waterRecordDao.insert(record)
    }

    suspend fun updateRecord(record: WaterRecord) {
        waterRecordDao.update(record)
    }

    suspend fun deleteRecord(record: WaterRecord) {
        waterRecordDao.delete(record)
    }

    suspend fun deleteAllRecords() {
        waterRecordDao.deleteAll()
    }

    val dailyGoal: LiveData<DailyGoal?> = dailyGoalDao.getDailyGoal()

    suspend fun setDailyGoal(goalAmount: Int) {
        dailyGoalDao.setDailyGoal(DailyGoal(id = 1, goalAmount = goalAmount))
    }
}
