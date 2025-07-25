package com.meticha.jetpackboilerplate.launcher.data.repository

import com.meticha.jetpackboilerplate.launcher.data.models.BehaviorPattern
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.data.mock.MockDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BehaviorPatternRepositoryImpl @Inject constructor() : BehaviorPatternRepository {

    private val _behaviorPatterns = MutableStateFlow<List<BehaviorPattern>>(MockDataProvider.getMockBehaviorPatterns())
    private val behaviorPatterns = _behaviorPatterns.asStateFlow()

    override suspend fun insertBehaviorPattern(pattern: BehaviorPattern) {
        // Simulate pattern analysis and storage delay
        delay(300)
        val currentList = _behaviorPatterns.value.toMutableList()
        currentList.add(pattern)
        _behaviorPatterns.value = currentList
    }

    override suspend fun updateBehaviorPattern(pattern: BehaviorPattern) {
        // Simulate pattern update and reanalysis delay
        delay(250)
        val currentList = _behaviorPatterns.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == pattern.id }
        if (index != -1) {
            currentList[index] = pattern.copy(lastUpdated = System.currentTimeMillis())
            _behaviorPatterns.value = currentList
        }
    }

    override suspend fun deleteBehaviorPattern(patternId: String) {
        // Simulate pattern deletion delay
        delay(150)
        val currentList = _behaviorPatterns.value.toMutableList()
        currentList.removeAll { it.id == patternId }
        _behaviorPatterns.value = currentList
    }

    override suspend fun getBehaviorPatternById(patternId: String): BehaviorPattern? {
        // Simulate pattern lookup delay
        delay(80)
        return _behaviorPatterns.value.find { it.id == patternId }
    }

    override fun getAllBehaviorPatterns(): Flow<List<BehaviorPattern>> {
        return behaviorPatterns
    }

    override fun getBehaviorPatternsByRoutineType(routineType: RoutineType?): Flow<List<BehaviorPattern>> {
        return behaviorPatterns.map { list -> 
            list.filter { it.routineType == routineType }
                .sortedByDescending { it.frequency }
        }
    }

    override suspend fun getBehaviorPatternsBySuccessRate(minSuccessRate: Float): List<BehaviorPattern> {
        // Simulate success rate analysis delay
        delay(120)
        return _behaviorPatterns.value.filter { it.successRate >= minSuccessRate }
            .sortedByDescending { it.successRate }
    }

    override suspend fun updatePatternFrequency(patternId: String, newFrequency: Int) {
        // Simulate frequency update delay
        delay(100)
        val currentList = _behaviorPatterns.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == patternId }
        if (index != -1) {
            val pattern = currentList[index]
            currentList[index] = pattern.copy(
                frequency = newFrequency,
                lastUpdated = System.currentTimeMillis()
            )
            _behaviorPatterns.value = currentList
        }
    }

    override suspend fun updatePatternSuccessRate(patternId: String, newSuccessRate: Float) {
        // Simulate success rate update delay
        delay(100)
        val currentList = _behaviorPatterns.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == patternId }
        if (index != -1) {
            val pattern = currentList[index]
            currentList[index] = pattern.copy(
                successRate = newSuccessRate,
                lastUpdated = System.currentTimeMillis()
            )
            _behaviorPatterns.value = currentList
        }
    }
}