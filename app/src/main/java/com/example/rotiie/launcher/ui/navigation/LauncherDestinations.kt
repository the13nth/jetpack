package com.example.rotiie.launcher.ui.navigation

import kotlinx.serialization.Serializable
import com.example.rotiie.launcher.ui.screens.PredictionData

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
    
@Serializable
data class PredictionDetail(val prediction: PredictionData) : LauncherDestination()
    
@Serializable
data object CreateRoutine : LauncherDestination()
    
@Serializable
data object Brain : LauncherDestination()
    
@Serializable
data object Promptefy : LauncherDestination()
}