package com.meticha.jetpackboilerplate.launcher.utils

import java.util.UUID

object IdGenerator {
    fun generateId(): String = UUID.randomUUID().toString()
    
    fun generateRoutineId(): String = "routine_${generateId()}"
    fun generateIntentionId(): String = "intention_${generateId()}"
    fun generateUserActionId(): String = "action_${generateId()}"
    fun generateBehaviorPatternId(): String = "pattern_${generateId()}"
}