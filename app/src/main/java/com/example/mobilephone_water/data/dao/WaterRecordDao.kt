package com.example.mobilephone_water.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mobilephone_water.data.entity.WaterRecord

@Dao
interface WaterRecordDao {

    // Получить все записи
    @Query("SELECT * FROM water_records ORDER BY timestamp DESC")
    fun getAllRecords(): LiveData<List<WaterRecord>>

    // Получить записи за конкретную дату
    @Query("SELECT * FROM water_records WHERE date = :date ORDER BY timestamp DESC")
    fun getRecordsByDate(date: String): LiveData<List<WaterRecord>>

    // Получить сумму воды за день
    @Query("SELECT SUM(amount) FROM water_records WHERE date = :date")
    fun getTotalAmountByDate(date: String): LiveData<Int?>

    // Добавить запись
    @Insert
    suspend fun insert(record: WaterRecord)

    // Обновить запись
    @Update
    suspend fun update(record: WaterRecord)

    // Удалить запись
    @Delete
    suspend fun delete(record: WaterRecord)

    // Удалить все записи
    @Query("DELETE FROM water_records")
    suspend fun deleteAll()
}
