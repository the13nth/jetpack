package com.example.rotiie.launcher.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Routine(
    val id: String,
    val name: String,
    val type: RoutineType,
    val startTime: String, // HH:mm format
    val endTime: String,   // HH:mm format
    val isActive: Boolean = true,
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class RoutineType {
    MORNING,
    AFTERNOON, 
    EVENING,
    WEEKEND,
    CUSTOM
}