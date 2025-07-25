package com.example.rotiie.launcher

import com.example.rotiie.launcher.data.repository.*
import javax.inject.Inject

/**
 * Simple class to verify that all repositories can be injected correctly
 * This demonstrates that the foundation setup is complete
 */
class FoundationVerification @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val intentionRepository: IntentionRepository,
    private val userActionRepository: UserActionRepository,
    private val behaviorPatternRepository: BehaviorPatternRepository
) {
    
    fun verifyRepositoriesInjected(): Boolean {
        return routineRepository is RoutineRepositoryImpl &&
               intentionRepository is IntentionRepositoryImpl &&
               userActionRepository is UserActionRepositoryImpl &&
               behaviorPatternRepository is BehaviorPatternRepositoryImpl
    }
    
    fun getRepositoryInfo(): String {
        return """
            Foundation Setup Verification:
            - RoutineRepository: ${routineRepository::class.simpleName}
            - IntentionRepository: ${intentionRepository::class.simpleName}
            - UserActionRepository: ${userActionRepository::class.simpleName}
            - BehaviorPatternRepository: ${behaviorPatternRepository::class.simpleName}
            
            All repositories are properly injected and ready for use.
        """.trimIndent()
    }
}