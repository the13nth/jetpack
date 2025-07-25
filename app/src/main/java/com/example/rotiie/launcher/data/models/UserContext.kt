package com.example.rotiie.launcher.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserContext(
    val currentRoutine: RoutineType,
    val timeOfDay: String, // HH:mm format
    val recentActions: List<UserAction> = emptyList(),
    val activeIntentions: List<Intention> = emptyList(),
    val locationContext: LocationContext? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class LocationContext(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val isHome: Boolean = false,
    val isWork: Boolean = false
)