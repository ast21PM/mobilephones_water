package com.example.mobilephone_water.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_records")
data class WaterRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Int,              // Количество воды в мл
    val timestamp: Long,          // Время добавления записи
    val date: String              // Дата в формате "yyyy-MM-dd"
)
