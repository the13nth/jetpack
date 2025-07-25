package com.meticha.jetpackboilerplate.launcher.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.meticha.jetpackboilerplate.launcher.ui.screens.LauncherHomeScreen
import com.meticha.jetpackboilerplate.ui.theme.CallBudyTheme

class SimpleLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallBudyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LauncherHomeScreen(
                        onNavigateToRoutineSelection = {},
                        onNavigateToSettings = {},
                        onNavigateToDocumentAnalysis = {},
                        onNavigateToPredictionDetail = {},
                onNavigateToCreateRoutine = {}
                    )
                }
            }
        }
    }
} 