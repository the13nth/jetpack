package com.meticha.jetpackboilerplate.launcher.data.repository

import com.meticha.jetpackboilerplate.launcher.data.models.Intention
import com.meticha.jetpackboilerplate.launcher.data.mock.MockDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntentionRepositoryImpl @Inject constructor() : IntentionRepository {

    private val _intentions = MutableStateFlow<List<Intention>>(MockDataProvider.getMockIntentions())
    private val intentions = _intentions.asStateFlow()

    override suspend fun insertIntention(intention: Intention) {
        // Simulate intention processing delay
        delay(250)
        val currentList = _intentions.value.toMutableList()
        currentList.add(intention)
        _intentions.value = currentList
    }

    override suspend fun updateIntention(intention: Intention) {
        // Simulate intention update delay
        delay(180)
        val currentList = _intentions.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == intention.id }
        if (index != -1) {
            currentList[index] = intention
            _intentions.value = currentList
        }
    }

    override suspend fun deleteIntention(intentionId: String) {
        // Simulate intention deletion delay
        delay(120)
        val currentList = _intentions.value.toMutableList()
        currentList.removeAll { it.id == intentionId }
        _intentions.value = currentList
    }

    override suspend fun getIntentionById(intentionId: String): Intention? {
        // Simulate database query delay
        delay(80)
        return _intentions.value.find { it.id == intentionId }
    }

    override fun getIntentionsByRoutineId(routineId: String): Flow<List<Intention>> {
        return intentions.map { list -> list.filter { it.routineId == routineId } }
    }

    override fun getActiveIntentions(): Flow<List<Intention>> {
        return intentions.map { list -> list.filter { it.isActive } }
    }

    override suspend fun getIntentionsByPriority(minPriority: Int): List<Intention> {
        // Simulate priority-based query delay
        delay(100)
        return _intentions.value.filter { it.priority >= minPriority }
            .sortedByDescending { it.priority }
    }
}