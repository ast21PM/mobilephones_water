package com.example.mobilephone_water.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mobilephone_water.data.entity.DailyGoal

@Dao
interface DailyGoalDao {

    @Query("SELECT * FROM daily_goals LIMIT 1")
    fun getDailyGoal(): LiveData<DailyGoal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: DailyGoal)

    @Update
    suspend fun updateGoal(goal: DailyGoal)

    @Delete
    suspend fun deleteGoal(goal: DailyGoal)

    @Query("DELETE FROM daily_goals")
    suspend fun deleteAll()

}
