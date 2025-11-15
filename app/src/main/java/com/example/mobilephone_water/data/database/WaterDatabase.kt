package com.example.mobilephone_water.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mobilephone_water.data.dao.DailyGoalDao
import com.example.mobilephone_water.data.dao.WaterRecordDao
import com.example.mobilephone_water.data.entity.DailyGoal
import com.example.mobilephone_water.data.entity.WaterRecord

@Database(
    entities = [WaterRecord::class, DailyGoal::class],
    version = 1,
    exportSchema = false
)
abstract class WaterDatabase : RoomDatabase() {

    abstract fun waterRecordDao(): WaterRecordDao
    abstract fun dailyGoalDao(): DailyGoalDao

    companion object {
        @Volatile
        private var INSTANCE: WaterDatabase? = null

        fun getDatabase(context: Context): WaterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WaterDatabase::class.java,
                    "water_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
