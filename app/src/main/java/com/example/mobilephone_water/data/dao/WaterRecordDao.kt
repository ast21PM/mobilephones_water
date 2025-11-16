package com.example.mobilephone_water.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mobilephone_water.data.entity.WaterRecord

@Dao
interface WaterRecordDao {

    @Query("SELECT * FROM water_records ORDER BY timestamp DESC")
    fun getAllRecords(): LiveData<List<WaterRecord>>

    @Query("SELECT * FROM water_records WHERE date = :date ORDER BY timestamp DESC")
    fun getRecordsByDate(date: String): LiveData<List<WaterRecord>>

    @Query("SELECT SUM(amount) FROM water_records WHERE date = :date")
    fun getTotalAmountByDate(date: String): LiveData<Int?>

    @Insert
    suspend fun insert(record: WaterRecord)

    @Update
    suspend fun update(record: WaterRecord)

    @Delete
    suspend fun delete(record: WaterRecord)

    @Query("DELETE FROM water_records")
    suspend fun deleteAll()

    @Query("""
        SELECT COALESCE(CAST(AVG(daily_total) AS INTEGER), 0)
        FROM (
            SELECT SUM(amount) as daily_total
            FROM water_records
            WHERE date >= date('now', '-7 days')
            GROUP BY date
        )
    """)
    suspend fun getAverageWeekly(): Int

    @Query("DELETE FROM water_records WHERE date = :date")
    suspend fun deleteRecordsByDate(date: String)
}
