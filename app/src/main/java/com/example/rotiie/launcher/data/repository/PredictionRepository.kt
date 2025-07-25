package com.example.rotiie.launcher.data.repository

import com.example.rotiie.launcher.data.models.RoutineType
import com.example.rotiie.launcher.data.models.UserContext
import com.example.rotiie.launcher.data.mock.ActionPrediction
import com.example.rotiie.launcher.data.mock.UIStatePrediction
import kotlinx.coroutines.flow.Flow

interface PredictionRepository {
    suspend fun generateActionPredictions(context: UserContext): List<ActionPrediction>
    suspend fun generateUIStatePrediction(routineType: RoutineType): UIStatePrediction
    suspend fun updatePredictionAccuracy(predictionId: String, wasAccurate: Boolean)
    fun observePredictionUpdates(): Flow<List<ActionPrediction>>
    suspend fun getPredictionHistory(limit: Int = 20): List<ActionPrediction>
    suspend fun clearPredictionHistory()
}