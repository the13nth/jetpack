package com.meticha.jetpackboilerplate.launcher.data.models

import kotlinx.serialization.Serializable

@Serializable
data class BehaviorPattern(
    val id: String,
    val userId: String? = null, // For future multi-user support
    val sequence: String = "[]", // JSON string representation of List<ActionType>
    val frequency: Int = 1,
    val successRate: Float = 0.0f,
    val contextFactors: String = "[]", // JSON string representation of List<String>
    val embedding: String? = null, // Base64 encoded FloatArray for vector storage
    val lastUpdated: Long = System.currentTimeMillis(),
    val routineType: RoutineType? = null
) {
    // Helper functions to work with lists
    fun getSequenceList(): List<ActionType> {
        return kotlinx.serialization.json.Json.decodeFromString(sequence)
    }
    
    fun getContextFactorsList(): List<String> {
        return kotlinx.serialization.json.Json.decodeFromString(contextFactors)
    }
    
    companion object {
        fun create(
            id: String,
            userId: String? = null,
            sequenceList: List<ActionType>,
            frequency: Int = 1,
            successRate: Float = 0.0f,
            contextFactorsList: List<String> = emptyList(),
            embedding: String? = null,
            lastUpdated: Long = System.currentTimeMillis(),
            routineType: RoutineType? = null
        ): BehaviorPattern {
            return BehaviorPattern(
                id = id,
                userId = userId,
                sequence = kotlinx.serialization.json.Json.encodeToString(sequenceList),
                frequency = frequency,
                successRate = successRate,
                contextFactors = kotlinx.serialization.json.Json.encodeToString(contextFactorsList),
                embedding = embedding,
                lastUpdated = lastUpdated,
                routineType = routineType
            )
        }
    }
}