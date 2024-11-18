package com.dicoding.asclepius.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_history")
data class PredictionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "imagePath")
    val imagePath: String,

    @ColumnInfo(name = "predictionResult")
    val predictionResult: String,

    @ColumnInfo(name = "confidenceScore")
    val confidenceScore: Float,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)