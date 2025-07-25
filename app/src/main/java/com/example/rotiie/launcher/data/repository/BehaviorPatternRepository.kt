package com.example.rotiie.launcher.data.repository

import com.example.rotiie.launcher.data.models.BehaviorPattern
import com.example.rotiie.launcher.data.models.RoutineType
import kotlinx.coroutines.flow.Flow

interface BehaviorPatternRepository {
    suspend fun insertBehaviorPattern(pattern: BehaviorPattern)
    suspend fun updateBehaviorPattern(pattern: BehaviorPattern)
    suspend fun deleteBehaviorPattern(patternId: String)
    suspend fun getBehaviorPatternById(patternId: String): BehaviorPattern?
    fun getAllBehaviorPatterns(): Flow<List<BehaviorPattern>>
    fun getBehaviorPatternsByRoutineType(routineType: RoutineType?): Flow<List<BehaviorPattern>>
    suspend fun getBehaviorPatternsBySuccessRate(minSuccessRate: Float): List<BehaviorPattern>
    suspend fun updatePatternFrequency(patternId: String, newFrequency: Int)
    suspend fun updatePatternSuccessRate(patternId: String, newSuccessRate: Float)
}