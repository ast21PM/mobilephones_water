package com.example.mobilephone_water.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoal(
    @PrimaryKey
    val id: Int = 1,              // Всегда 1, т.к. цель одна

    val goalAmount: Int           // Цель в мл (например, 2000 мл)
)
