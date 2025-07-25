package com.example.rotiie.launcher.data.repository

import com.example.rotiie.launcher.data.models.Routine
import com.example.rotiie.launcher.data.models.RoutineType
import com.example.rotiie.launcher.data.mock.MockDataProvider
import com.example.rotiie.launcher.utils.Constants
import com.example.rotiie.launcher.utils.IdGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepositoryImpl @Inject constructor() : RoutineRepository {

    private val _routines = MutableStateFlow<List<Routine>>(MockDataProvider.getMockRoutines())
    private val routines = _routines.asStateFlow()

    override suspend fun insertRoutine(routine: Routine) {
        // Simulate network/database delay
        delay(200)
        val currentList = _routines.value.toMutableList()
        currentList.add(routine)
        _routines.value = currentList
    }

    override suspend fun updateRoutine(routine: Routine) {
        // Simulate network/database delay
        delay(150)
        val currentList = _routines.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == routine.id }
        if (index != -1) {
            currentList[index] = routine.copy(updatedAt = System.currentTimeMillis())
            _routines.value = currentList
        }
    }

    override suspend fun deleteRoutine(routineId: String) {
        // Simulate network/database delay
        delay(100)
        val currentList = _routines.value.toMutableList()
        currentList.removeAll { it.id == routineId }
        _routines.value = currentList
    }

    override suspend fun getRoutineById(routineId: String): Routine? {
        // Simulate database query delay
        delay(50)
        return _routines.value.find { it.id == routineId }
    }

    override fun getAllRoutines(): Flow<List<Routine>> {
        return routines
    }

    override fun getActiveRoutines(): Flow<List<Routine>> {
        return routines.map { list -> list.filter { it.isActive } }
    }

    override suspend fun getCurrentRoutine(): Routine? {
        // Simulate context analysis delay
        delay(100)
        return _routines.value
            .filter { it.isActive }
            .maxByOrNull { it.priority }
    }
    
    // Additional method for simulating routine switching with realistic delays
    suspend fun switchToRoutine(routineType: RoutineType) {
        // Simulate context switching delay
        delay(300)
        val currentList = _routines.value.toMutableList()
        currentList.forEachIndexed { index, routine ->
            currentList[index] = routine.copy(
                isActive = routine.type == routineType,
                updatedAt = System.currentTimeMillis()
            )
        }
        _routines.value = currentList
    }

    private fun createDefaultRoutines(): List<Routine> {
        return listOf(
            Routine(
                id = IdGenerator.generateRoutineId(),
                name = "Morning Routine",
                type = RoutineType.MORNING,
                startTime = Constants.DEFAULT_MORNING_START,
                endTime = Constants.DEFAULT_MORNING_END,
                isActive = true,
                priority = 1
            ),
            Routine(
                id = IdGenerator.generateRoutineId(),
                name = "Afternoon Routine",
                type = RoutineType.AFTERNOON,
                startTime = Constants.DEFAULT_AFTERNOON_START,
                endTime = Constants.DEFAULT_AFTERNOON_END,
                isActive = true,
                priority = 2
            ),
            Routine(
                id = IdGenerator.generateRoutineId(),
                name = "Evening Routine",
                type = RoutineType.EVENING,
                startTime = Constants.DEFAULT_EVENING_START,
                endTime = Constants.DEFAULT_EVENING_END,
                isActive = true,
                priority = 3
            ),
            Routine(
                id = IdGenerator.generateRoutineId(),
                name = "Weekend Routine",
                type = RoutineType.WEEKEND,
                startTime = Constants.DEFAULT_WEEKEND_START,
                endTime = Constants.DEFAULT_WEEKEND_END,
                isActive = true,
                priority = 4
            )
        )
    }
}