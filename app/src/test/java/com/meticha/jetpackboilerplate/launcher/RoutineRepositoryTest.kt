package com.meticha.jetpackboilerplate.launcher

import com.meticha.jetpackboilerplate.launcher.data.models.Routine
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.data.repository.RoutineRepositoryImpl
import com.meticha.jetpackboilerplate.launcher.utils.IdGenerator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class RoutineRepositoryTest {

    @Test
    fun `repository should have default routines`() = runTest {
        val repository = RoutineRepositoryImpl()
        val routines = repository.getAllRoutines().first()
        
        assertEquals(4, routines.size)
        assertTrue(routines.any { it.type == RoutineType.MORNING })
        assertTrue(routines.any { it.type == RoutineType.AFTERNOON })
        assertTrue(routines.any { it.type == RoutineType.EVENING })
        assertTrue(routines.any { it.type == RoutineType.WEEKEND })
    }

    @Test
    fun `should insert and retrieve routine`() = runTest {
        val repository = RoutineRepositoryImpl()
        val newRoutine = Routine(
            id = IdGenerator.generateRoutineId(),
            name = "Test Routine",
            type = RoutineType.CUSTOM,
            startTime = "10:00",
            endTime = "12:00",
            isActive = true,
            priority = 5
        )

        repository.insertRoutine(newRoutine)
        val retrievedRoutine = repository.getRoutineById(newRoutine.id)
        
        assertNotNull(retrievedRoutine)
        assertEquals(newRoutine.name, retrievedRoutine?.name)
        assertEquals(newRoutine.type, retrievedRoutine?.type)
    }

    @Test
    fun `should get active routines only`() = runTest {
        val repository = RoutineRepositoryImpl()
        val activeRoutines = repository.getActiveRoutines().first()
        
        assertTrue(activeRoutines.all { it.isActive })
        assertEquals(4, activeRoutines.size) // All default routines are active
    }
}