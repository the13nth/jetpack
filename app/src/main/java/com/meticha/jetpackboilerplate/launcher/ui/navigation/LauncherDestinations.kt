package com.meticha.jetpackboilerplate.launcher.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class LauncherDestination {
    @Serializable
    data object Home : LauncherDestination()
    
    @Serializable
    data object RoutineSelection : LauncherDestination()
    
    @Serializable
    data object Settings : LauncherDestination()
    
    @Serializable
    data class IntentionDetail(val intentionId: String) : LauncherDestination()
}