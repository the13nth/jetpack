package com.meticha.jetpackboilerplate.launcher.ui

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
import com.meticha.jetpackboilerplate.launcher.ui.navigation.LauncherDestination
import com.meticha.jetpackboilerplate.launcher.ui.screens.LauncherHomeScreen
import com.meticha.jetpackboilerplate.launcher.ui.screens.RoutineSelectionScreen
import com.meticha.jetpackboilerplate.ui.theme.CallBudyTheme
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
                            currentDestination = LauncherDestination.RoutineSelection
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                
                is LauncherDestination.RoutineSelection -> {
                    RoutineSelectionScreen(
                        onNavigateBack = {
                            currentDestination = LauncherDestination.Home
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                
                is LauncherDestination.Settings -> {
                    // Settings screen placeholder - will be implemented in later tasks
                }
                
                is LauncherDestination.IntentionDetail -> {
                    // Intention detail screen placeholder - will be implemented in later tasks
                }
            }
        }
    }
}