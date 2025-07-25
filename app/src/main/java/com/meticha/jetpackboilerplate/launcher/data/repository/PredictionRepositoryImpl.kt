package com.meticha.jetpackboilerplate.launcher.data.repository

import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.data.models.UserContext
import com.meticha.jetpackboilerplate.launcher.data.mock.ActionPrediction
import com.meticha.jetpackboilerplate.launcher.data.mock.MockDataProvider
import com.meticha.jetpackboilerplate.launcher.data.mock.UIStatePrediction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictionRepositoryImpl @Inject constructor() : PredictionRepository {

    private val _predictionHistory = MutableStateFlow<List<ActionPrediction>>(emptyList())
    private val predictionHistory = _predictionHistory.asStateFlow()

    override suspend fun generateActionPredictions(context: UserContext): List<ActionPrediction> {
        // Simulate AI prediction generation delay
        delay(400)
        
        val predictions = MockDataProvider.getMockActionPredictions(context.currentRoutine)
        
        // Add to history
        val currentHistory = _predictionHistory.value.toMutableList()
        currentHistory.addAll(predictions)
        
        // Keep only recent predictions
        if (currentHistory.size > 100) {
            _predictionHistory.value = currentHistory.takeLast(100)
        } else {
            _predictionHistory.value = currentHistory
        }
        
        return predictions
    }

    override suspend fun generateUIStatePrediction(routineType: RoutineType): UIStatePrediction {
        // Simulate UI state analysis delay
        delay(300)
        return MockDataProvider.getMockUIStatePredictions(routineType)
    }

    override suspend fun updatePredictionAccuracy(predictionId: String, wasAccurate: Boolean) {
        // Simulate prediction feedback processing delay
        delay(150)
        
        // In a real implementation, this would update ML model accuracy
        // For mock, we just simulate the delay
        val currentHistory = _predictionHistory.value.toMutableList()
        val updatedHistory = currentHistory.map { prediction ->
            if (prediction.action.hashCode().toString() == predictionId) {
                // Simulate confidence adjustment based on accuracy feedback
                val newConfidence = if (wasAccurate) {
                    (prediction.confidence + 0.05f).coerceAtMost(1.0f)
                } else {
                    (prediction.confidence - 0.1f).coerceAtLeast(0.1f)
                }
                prediction.copy(confidence = newConfidence)
            } else {
                prediction
            }
        }
        _predictionHistory.value = updatedHistory
    }

    override fun observePredictionUpdates(): Flow<List<ActionPrediction>> {
        return predictionHistory
    }

    override suspend fun getPredictionHistory(limit: Int): List<ActionPrediction> {
        // Simulate history retrieval delay
        delay(100)
        return _predictionHistory.value.takeLast(limit)
    }

    override suspend fun clearPredictionHistory() {
        // Simulate history clearing delay
        delay(80)
        _predictionHistory.value = emptyList()
    }
}