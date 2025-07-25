package com.example.rotiie.launcher.data.repository

import com.example.rotiie.launcher.data.models.*
import com.example.rotiie.launcher.data.mock.ActionPrediction
import com.example.rotiie.launcher.data.mock.MockDataProvider
import com.example.rotiie.launcher.utils.IdGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Coordinates all mock repositories and provides realistic state changes for UI testing
 */
@Singleton
class MockRepositoryManager @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val intentionRepository: IntentionRepository,
    private val userActionRepository: UserActionRepository,
    private val behaviorPatternRepository: BehaviorPatternRepository,
    private val predictionRepository: PredictionRepository
) {

    /**
     * Simulates a complete user interaction flow with realistic delays and state changes
     */
    suspend fun simulateUserFlow(routineType: RoutineType) {
        // Simulate routine switching
        delay(200)
        
        // Get current context
        val context = MockDataProvider.getMockUserContext(routineType)
        
        // Generate predictions based on context
        val predictions = predictionRepository.generateActionPredictions(context)
        
        // Simulate user selecting a predicted action
        delay(500)
        val selectedPrediction = predictions.firstOrNull()
        
        selectedPrediction?.let { prediction ->
            // Record user action
            val userAction = UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = System.currentTimeMillis(),
                routineId = context.currentRoutine.name,
                appPackageName = prediction.associatedApps.firstOrNull()?.packageName,
                outcome = ActionOutcome.SUCCESS,
                duration = (10..60).random() * 60 * 1000L // Random duration 10-60 minutes
            )
            
            userActionRepository.insertUserAction(userAction)
            
            // Update prediction accuracy (simulate 80% accuracy)
            val wasAccurate = (1..10).random() <= 8
            predictionRepository.updatePredictionAccuracy(
                prediction.action.hashCode().toString(),
                wasAccurate
            )
            
            // Simulate behavior pattern learning
            delay(300)
            updateBehaviorPatterns(userAction, context)
        }
    }

    /**
     * Simulates adding a new intention and updating related predictions
     */
    suspend fun simulateIntentionCreation(routineType: RoutineType, description: String) {
        val routines = MockDataProvider.getMockRoutines()
        val routine = routines.first { it.type == routineType }
        
        val newIntention = Intention.create(
            id = IdGenerator.generateId(),
            routineId = routine.id,
            description = description,
            priority = (1..5).random(),
            targetActionsList = listOf("focus_work", "deep_thinking", "productivity"),
            successMetricsList = listOf("task_completed", "goal_achieved", "time_well_spent")
        )
        
        // Simulate intention processing delay
        intentionRepository.insertIntention(newIntention)
        
        // Simulate updating predictions based on new intention
        delay(400)
        val context = MockDataProvider.getMockUserContext(routineType)
        predictionRepository.generateActionPredictions(context)
    }

    /**
     * Simulates routine switching with cascading updates
     */
    suspend fun simulateRoutineSwitch(fromRoutine: RoutineType, toRoutine: RoutineType) {
        // Record routine switch action
        val switchAction = UserAction(
            id = IdGenerator.generateId(),
            type = ActionType.ROUTINE_SWITCH,
            timestamp = System.currentTimeMillis(),
            routineId = fromRoutine.name,
            contextData = """{"switched_to": "${toRoutine.name}", "manual": true}""",
            outcome = ActionOutcome.SUCCESS
        )
        
        userActionRepository.insertUserAction(switchAction)
        
        // Simulate context analysis delay
        delay(300)
        
        // Generate new predictions for the target routine
        val newContext = MockDataProvider.getMockUserContext(toRoutine)
        predictionRepository.generateActionPredictions(newContext)
        
        // Update behavior patterns to reflect routine switching behavior
        updateRoutineSwitchPattern(fromRoutine, toRoutine)
    }

    /**
     * Provides a combined flow of all repository states for UI observation
     */
    fun observeSystemState(): Flow<SystemState> = combine(
        routineRepository.getActiveRoutines(),
        intentionRepository.getActiveIntentions(),
        userActionRepository.getRecentUserActions(10),
        behaviorPatternRepository.getAllBehaviorPatterns(),
        predictionRepository.observePredictionUpdates()
    ) { routines, intentions, actions, patterns, predictions ->
        SystemState(
            activeRoutines = routines,
            activeIntentions = intentions,
            recentActions = actions,
            behaviorPatterns = patterns,
            currentPredictions = predictions.takeLast(5)
        )
    }

    /**
     * Simulates periodic background learning and pattern updates
     */
    fun simulateBackgroundLearning(): Flow<Unit> = flow {
        while (true) {
            delay(30000) // Every 30 seconds
            
            // Simulate pattern frequency updates
            val patterns = MockDataProvider.getMockBehaviorPatterns()
            patterns.forEach { pattern ->
                val newFrequency = pattern.frequency + (0..2).random()
                behaviorPatternRepository.updatePatternFrequency(pattern.id, newFrequency)
            }
            
            emit(Unit)
        }
    }

    private suspend fun updateBehaviorPatterns(userAction: UserAction, context: UserContext) {
        // Simulate behavior pattern analysis
        delay(200)
        
        val newPattern = BehaviorPattern.create(
            id = IdGenerator.generateId(),
            sequenceList = listOf(ActionType.UI_INTERACTION, userAction.type, ActionType.APP_LAUNCH),
            frequency = 1,
            successRate = if (userAction.outcome == ActionOutcome.SUCCESS) 1.0f else 0.0f,
            contextFactorsList = listOf(
                context.currentRoutine.name.lowercase(),
                if (context.locationContext?.isHome == true) "home" else "away",
                context.timeOfDay
            ),
            routineType = context.currentRoutine
        )
        
        behaviorPatternRepository.insertBehaviorPattern(newPattern)
    }

    private suspend fun updateRoutineSwitchPattern(from: RoutineType, to: RoutineType) {
        delay(150)
        
        val switchPattern = BehaviorPattern.create(
            id = IdGenerator.generateId(),
            sequenceList = listOf(ActionType.ROUTINE_SWITCH, ActionType.UI_INTERACTION),
            frequency = 1,
            successRate = 0.9f,
            contextFactorsList = listOf("routine_switch", from.name.lowercase(), to.name.lowercase()),
            routineType = to
        )
        
        behaviorPatternRepository.insertBehaviorPattern(switchPattern)
    }
}

/**
 * Represents the complete system state for UI observation
 */
data class SystemState(
    val activeRoutines: List<Routine>,
    val activeIntentions: List<Intention>,
    val recentActions: List<UserAction>,
    val behaviorPatterns: List<BehaviorPattern>,
    val currentPredictions: List<ActionPrediction>
)