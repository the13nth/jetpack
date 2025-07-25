package com.example.rotiie.launcher

import com.example.rotiie.launcher.data.models.*
import com.example.rotiie.launcher.data.repository.*
import com.example.rotiie.launcher.utils.IdGenerator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

/**
 * Test to verify the foundation setup is working correctly
 */
class FoundationTest {

    @Test
    fun `test core data models creation`() {
        // Test Routine creation
        val routine = Routine(
            id = IdGenerator.generateRoutineId(),
            name = "Test Routine",
            type = RoutineType.MORNING,
            startTime = "06:00",
            endTime = "12:00"
        )
        
        assertNotNull(routine.id)
        assertEquals("Test Routine", routine.name)
        assertEquals(RoutineType.MORNING, routine.type)
        assertTrue(routine.isActive)

        // Test Intention creation
        val intention = Intention.create(
            id = IdGenerator.generateIntentionId(),
            routineId = routine.id,
            description = "Test intention",
            targetActionsList = listOf("action1", "action2"),
            successMetricsList = listOf("metric1")
        )
        
        assertNotNull(intention.id)
        assertEquals(routine.id, intention.routineId)
        assertEquals("Test intention", intention.description)
        assertEquals(listOf("action1", "action2"), intention.getTargetActionsList())
        assertEquals(listOf("metric1"), intention.getSuccessMetricsList())

        // Test UserAction creation
        val userAction = UserAction(
            id = IdGenerator.generateUserActionId(),
            type = ActionType.APP_LAUNCH,
            routineId = routine.id,
            appPackageName = "com.example.app"
        )
        
        assertNotNull(userAction.id)
        assertEquals(ActionType.APP_LAUNCH, userAction.type)
        assertEquals(routine.id, userAction.routineId)
        assertEquals("com.example.app", userAction.appPackageName)

        // Test BehaviorPattern creation
        val behaviorPattern = BehaviorPattern.create(
            id = IdGenerator.generateBehaviorPatternId(),
            sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.UI_INTERACTION),
            frequency = 5,
            successRate = 0.8f,
            contextFactorsList = listOf("morning", "home"),
            routineType = RoutineType.MORNING
        )
        
        assertNotNull(behaviorPattern.id)
        assertEquals(listOf(ActionType.APP_LAUNCH, ActionType.UI_INTERACTION), behaviorPattern.getSequenceList())
        assertEquals(5, behaviorPattern.frequency)
        assertEquals(0.8f, behaviorPattern.successRate, 0.01f)
        assertEquals(listOf("morning", "home"), behaviorPattern.getContextFactorsList())
        assertEquals(RoutineType.MORNING, behaviorPattern.routineType)
    }

    @Test
    fun `test repository implementations work correctly`() = runTest {
        // Test RoutineRepository
        val routineRepo = RoutineRepositoryImpl()
        val routines = routineRepo.getAllRoutines().first()
        assertTrue("Should have default routines", routines.isNotEmpty())
        assertEquals(4, routines.size) // Morning, Afternoon, Evening, Weekend
        
        val newRoutine = Routine(
            id = IdGenerator.generateRoutineId(),
            name = "Custom Routine",
            type = RoutineType.CUSTOM,
            startTime = "14:00",
            endTime = "16:00"
        )
        routineRepo.insertRoutine(newRoutine)
        
        val updatedRoutines = routineRepo.getAllRoutines().first()
        assertEquals(5, updatedRoutines.size)
        assertTrue(updatedRoutines.any { it.name == "Custom Routine" })

        // Test IntentionRepository
        val intentionRepo = IntentionRepositoryImpl()
        val intention = Intention.create(
            id = IdGenerator.generateIntentionId(),
            routineId = newRoutine.id,
            description = "Test intention"
        )
        intentionRepo.insertIntention(intention)
        
        val intentions = intentionRepo.getIntentionsByRoutineId(newRoutine.id).first()
        assertEquals(1, intentions.size)
        assertEquals("Test intention", intentions.first().description)

        // Test UserActionRepository
        val userActionRepo = UserActionRepositoryImpl()
        val userAction = UserAction(
            id = IdGenerator.generateUserActionId(),
            type = ActionType.APP_LAUNCH,
            routineId = newRoutine.id
        )
        userActionRepo.insertUserAction(userAction)
        
        val recentActions = userActionRepo.getRecentUserActions(10).first()
        assertEquals(1, recentActions.size)
        assertEquals(ActionType.APP_LAUNCH, recentActions.first().type)

        // Test BehaviorPatternRepository
        val behaviorPatternRepo = BehaviorPatternRepositoryImpl()
        val behaviorPattern = BehaviorPattern.create(
            id = IdGenerator.generateBehaviorPatternId(),
            sequenceList = listOf(ActionType.APP_LAUNCH),
            frequency = 3,
            routineType = RoutineType.CUSTOM
        )
        behaviorPatternRepo.insertBehaviorPattern(behaviorPattern)
        
        val patterns = behaviorPatternRepo.getBehaviorPatternsByRoutineType(RoutineType.CUSTOM).first()
        assertEquals(1, patterns.size)
        assertEquals(3, patterns.first().frequency)
    }

    @Test
    fun `test id generation is unique`() {
        val ids = mutableSetOf<String>()
        
        repeat(100) {
            val routineId = IdGenerator.generateRoutineId()
            val intentionId = IdGenerator.generateIntentionId()
            val actionId = IdGenerator.generateUserActionId()
            val patternId = IdGenerator.generateBehaviorPatternId()
            
            assertTrue("Routine ID should be unique", ids.add(routineId))
            assertTrue("Intention ID should be unique", ids.add(intentionId))
            assertTrue("Action ID should be unique", ids.add(actionId))
            assertTrue("Pattern ID should be unique", ids.add(patternId))
            
            assertTrue("Routine ID should have correct prefix", routineId.startsWith("routine_"))
            assertTrue("Intention ID should have correct prefix", intentionId.startsWith("intention_"))
            assertTrue("Action ID should have correct prefix", actionId.startsWith("action_"))
            assertTrue("Pattern ID should have correct prefix", patternId.startsWith("pattern_"))
        }
    }
}