package com.dicoding.asclepius.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dicoding.asclepius.data.local.entity.PredictionHistoryEntity

@Dao
interface PredictionHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(history: PredictionHistoryEntity): Long

    @Query("SELECT * FROM prediction_history WHERE id = :id")
    fun getPredictionById(id: Int): LiveData<PredictionHistoryEntity>

    @Query("DELETE FROM prediction_history WHERE id = :id")
    suspend fun deletePredictionById(id: Int)

    @Query("SELECT * FROM prediction_history")
    fun getAllPredictions(): LiveData<List<PredictionHistoryEntity>>

    @Query("DELETE FROM prediction_history")
    suspend fun deleteAllPredictions()
}