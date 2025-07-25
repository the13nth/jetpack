package com.example.rotiie.launcher.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Intention(
    val id: String,
    val routineId: String,
    val description: String,
    val priority: Int = 0,
    val sourceDocument: String? = null,
    val targetActions: String = "[]", // JSON string representation of List<String>
    val successMetrics: String = "[]", // JSON string representation of List<String>
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Helper functions to work with lists
    fun getTargetActionsList(): List<String> {
        return kotlinx.serialization.json.Json.decodeFromString(targetActions)
    }
    
    fun getSuccessMetricsList(): List<String> {
        return kotlinx.serialization.json.Json.decodeFromString(successMetrics)
    }
    
    companion object {
        fun create(
            id: String,
            routineId: String,
            description: String,
            priority: Int = 0,
            sourceDocument: String? = null,
            targetActionsList: List<String> = emptyList(),
            successMetricsList: List<String> = emptyList(),
            isActive: Boolean = true,
            createdAt: Long = System.currentTimeMillis()
        ): Intention {
            return Intention(
                id = id,
                routineId = routineId,
                description = description,
                priority = priority,
                sourceDocument = sourceDocument,
                targetActions = kotlinx.serialization.json.Json.encodeToString(targetActionsList),
                successMetrics = kotlinx.serialization.json.Json.encodeToString(successMetricsList),
                isActive = isActive,
                createdAt = createdAt
            )
        }
    }
}