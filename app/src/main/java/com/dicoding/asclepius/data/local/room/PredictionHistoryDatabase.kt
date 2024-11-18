package com.dicoding.asclepius.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.entity.PredictionHistoryEntity

@Database(entities = [PredictionHistoryEntity::class], version = 1, exportSchema = false)
abstract class PredictionHistoryDatabase : RoomDatabase() {

    abstract fun predictionHistoryDao(): PredictionHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: PredictionHistoryDatabase? = null

        fun getInstance(context: Context): PredictionHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: createDatabase(context).also { INSTANCE = it }
            }
        }

        private fun createDatabase(context: Context): PredictionHistoryDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                PredictionHistoryDatabase::class.java,
                "prediction_history_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}