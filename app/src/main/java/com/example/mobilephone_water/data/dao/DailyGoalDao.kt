package com.example.mobilephone_water.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mobilephone_water.data.entity.DailyGoal

@Dao
interface DailyGoalDao {

    // Получить текущую цель
    @Query("SELECT * FROM daily_goals WHERE id = 1")
    fun getDailyGoal(): LiveData<DailyGoal?>

    // Установить/обновить цель
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setDailyGoal(goal: DailyGoal)
}
