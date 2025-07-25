package com.meticha.jetpackboilerplate.launcher.data.repository

import com.meticha.jetpackboilerplate.launcher.data.models.Routine
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    suspend fun insertRoutine(routine: Routine)
    suspend fun updateRoutine(routine: Routine)
    suspend fun deleteRoutine(routineId: String)
    suspend fun getRoutineById(routineId: String): Routine?
    fun getAllRoutines(): Flow<List<Routine>>
    fun getActiveRoutines(): Flow<List<Routine>>
    suspend fun getCurrentRoutine(): Routine?
}