package com.meticha.jetpackboilerplate.launcher.data.repository

import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.data.models.UserContext
import com.meticha.jetpackboilerplate.launcher.data.mock.ActionPrediction
import com.meticha.jetpackboilerplate.launcher.data.mock.UIStatePrediction
import kotlinx.coroutines.flow.Flow

interface PredictionRepository {
    suspend fun generateActionPredictions(context: UserContext): List<ActionPrediction>
    suspend fun generateUIStatePrediction(routineType: RoutineType): UIStatePrediction
    suspend fun updatePredictionAccuracy(predictionId: String, wasAccurate: Boolean)
    fun observePredictionUpdates(): Flow<List<ActionPrediction>>
    suspend fun getPredictionHistory(limit: Int = 20): List<ActionPrediction>
    suspend fun clearPredictionHistory()
}