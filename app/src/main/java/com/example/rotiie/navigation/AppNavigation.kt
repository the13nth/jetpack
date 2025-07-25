package com.example.rotiie.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.example.rotiie.launcher.ui.screens.DocumentAnalysisScreen
import com.example.rotiie.launcher.ui.screens.LauncherHomeScreen
import com.example.rotiie.launcher.ui.screens.PredictionDetailScreen
import com.example.rotiie.launcher.ui.screens.CreateRoutineScreen
import com.example.rotiie.launcher.ui.screens.SettingsScreen
import com.example.rotiie.launcher.ui.screens.BrainScreen
import com.example.rotiie.launcher.ui.screens.PromptefyScreen
import kotlinx.serialization.Serializable

@Serializable
data object LauncherHomeRoute : NavKey

@Serializable
data object SettingsRoute : NavKey

@Serializable
data object DocumentAnalysisRoute : NavKey

@Serializable
data class PredictionDetailRoute(val predictionId: String) : NavKey

@Serializable
data object CreateRoutineRoute : NavKey

@Serializable
data object BrainRoute : NavKey

@Serializable
data object PromptefyRoute : NavKey

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(LauncherHomeRoute)

    NavDisplay(
        entryDecorators = listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<LauncherHomeRoute> {
                LauncherHomeScreen(
                    onNavigateToRoutineSelection = {
                        backStack.add(SettingsRoute)
                    },
                    onNavigateToSettings = {
                        backStack.add(SettingsRoute)
                    },
                    onNavigateToDocumentAnalysis = {
                        backStack.add(DocumentAnalysisRoute)
                    },
                    onNavigateToPredictionDetail = { prediction ->
                        backStack.add(PredictionDetailRoute(prediction.title))
                    },
                    onNavigateToCreateRoutine = {
                        backStack.add(CreateRoutineRoute)
                    },
                    onNavigateToBrain = {
                        backStack.add(BrainRoute)
                    },
                    onNavigateToPromptefy = {
                        backStack.add(PromptefyRoute)
                    }
                )
            }
            entry<SettingsRoute> {
                SettingsScreen(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<DocumentAnalysisRoute> {
                DocumentAnalysisScreen(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<PredictionDetailRoute> { route ->
                // For now, we'll use a sample prediction. In a real app, you'd fetch this by ID
                val samplePrediction = com.example.rotiie.launcher.ui.screens.PredictionData(
                    icon = "📧",
                    title = "Email Check",
                    subtitle = "You have 3 unread messages from important contacts",
                    confidence = 85,
                    isLearning = true,
                    primaryAction = "Open Email",
                    secondaryAction = "Mark as Read"
                )
                
                PredictionDetailScreen(
                    prediction = samplePrediction,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    },
                    onPrimaryAction = {
                        // Handle primary action
                    },
                    onSecondaryAction = {
                        // Handle secondary action
                    }
                )
            }
            entry<CreateRoutineRoute> {
                CreateRoutineScreen(
                    onBackClick = {
                        backStack.removeLastOrNull()
                    },
                    onRoutineCreated = { routine ->
                        // TODO: Save the routine to repository
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<BrainRoute> {
                BrainScreen(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onPrimaryAction = {
                        // Handle brain settings
                    },
                    onSecondaryAction = {
                        // Handle secondary brain action
                    }
                )
            }
            entry<PromptefyRoute> {
                PromptefyScreen(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onGeneratePrompt = { prompt ->
                        // TODO: Handle prompt generation
                    }
                )
            }
        }
    )
}