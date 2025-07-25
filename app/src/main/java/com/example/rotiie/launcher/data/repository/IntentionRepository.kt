package com.example.rotiie.launcher.data.repository

import com.example.rotiie.launcher.data.models.Intention
import kotlinx.coroutines.flow.Flow

interface IntentionRepository {
    suspend fun insertIntention(intention: Intention)
    suspend fun updateIntention(intention: Intention)
    suspend fun deleteIntention(intentionId: String)
    suspend fun getIntentionById(intentionId: String): Intention?
    fun getIntentionsByRoutineId(routineId: String): Flow<List<Intention>>
    fun getActiveIntentions(): Flow<List<Intention>>
    suspend fun getIntentionsByPriority(minPriority: Int): List<Intention>
}