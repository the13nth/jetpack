package com.example.rotiie.launcher.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.rotiie.launcher.ui.navigation.LauncherDestination
import com.example.rotiie.launcher.ui.screens.DocumentAnalysisScreen
import com.example.rotiie.launcher.ui.screens.LauncherHomeScreen
import com.example.rotiie.launcher.ui.screens.SettingsScreen
import com.example.rotiie.launcher.ui.screens.CreateRoutineScreen
import com.example.rotiie.launcher.ui.screens.PredictionDetailScreen
import com.example.rotiie.launcher.ui.screens.BrainScreen
import com.example.rotiie.launcher.ui.screens.PromptefyScreen
import com.example.rotiie.ui.theme.CallBudyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallBudyTheme {
                LauncherApp()
            }
        }
    }
}

@Composable
fun LauncherApp() {
    var currentDestination by remember { mutableStateOf<LauncherDestination>(LauncherDestination.Home) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            when (currentDestination) {
                is LauncherDestination.Home -> {
                    LauncherHomeScreen(
                        onNavigateToRoutineSelection = {
                            currentDestination = LauncherDestination.Settings
                        },
                        onNavigateToSettings = {
                            currentDestination = LauncherDestination.Settings
                        },
                        onNavigateToDocumentAnalysis = {
                            currentDestination = LauncherDestination.IntentionDetail("document")
                        },
                        onNavigateToPredictionDetail = { prediction ->
                            currentDestination = LauncherDestination.PredictionDetail(prediction)
                        },
                        onNavigateToCreateRoutine = {
                            currentDestination = LauncherDestination.CreateRoutine
                        },
                        onNavigateToBrain = {
                            currentDestination = LauncherDestination.Brain
                        },
                        onNavigateToPromptefy = {
                            currentDestination = LauncherDestination.Promptefy
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                
                is LauncherDestination.Settings -> {
                    SettingsScreen(
                        onNavigateBack = {
                            currentDestination = LauncherDestination.Home
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                
                is LauncherDestination.IntentionDetail -> {
                    DocumentAnalysisScreen(
                        onNavigateBack = {
                            currentDestination = LauncherDestination.Home
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                
                is LauncherDestination.RoutineSelection -> {
                    // This is now handled by the Settings screen
                    SettingsScreen(
                        onNavigateBack = {
                            currentDestination = LauncherDestination.Home
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                
                is LauncherDestination.CreateRoutine -> {
                    CreateRoutineScreen(
                        onBackClick = {
                            currentDestination = LauncherDestination.Home
                        },
                        onRoutineCreated = { routine ->
                            // TODO: Save the routine to repository
                            currentDestination = LauncherDestination.Home
                        }
                    )
                }
                
                is LauncherDestination.PredictionDetail -> {
                    val predictionDetail = currentDestination as LauncherDestination.PredictionDetail
                    PredictionDetailScreen(
                        prediction = predictionDetail.prediction,
                        onBackClick = {
                            currentDestination = LauncherDestination.Home
                        },
                        onPrimaryAction = {
                            // TODO: Implement primary action
                        },
                        onSecondaryAction = {
                            // TODO: Implement secondary action
                        }
                    )
                }
                
                is LauncherDestination.Brain -> {
                    BrainScreen(
                        onNavigateBack = {
                            currentDestination = LauncherDestination.Home
                        },
                        onPrimaryAction = {
                            // TODO: Handle brain settings
                        },
                        onSecondaryAction = {
                            // TODO: Handle secondary brain action
                        }
                    )
                }
                
                is LauncherDestination.Promptefy -> {
                    PromptefyScreen(
                        onNavigateBack = {
                            currentDestination = LauncherDestination.Home
                        },
                        onGeneratePrompt = { prompt ->
                            // TODO: Handle prompt generation
                        }
                    )
                }
            }
        }
    }
}