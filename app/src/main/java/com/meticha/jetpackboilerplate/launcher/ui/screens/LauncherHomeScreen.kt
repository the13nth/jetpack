package com.meticha.jetpackboilerplate.launcher.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meticha.jetpackboilerplate.launcher.data.mock.ActionPrediction
import com.meticha.jetpackboilerplate.launcher.data.mock.MockDataProvider
import com.meticha.jetpackboilerplate.launcher.data.models.AppInfo
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.ui.LauncherViewModel
import com.meticha.jetpackboilerplate.launcher.ui.components.RealTimeUIUpdater
import com.meticha.jetpackboilerplate.launcher.ui.components.RoutineAwareLayout
import com.meticha.jetpackboilerplate.launcher.ui.theme.RoutineTheme
import com.meticha.jetpackboilerplate.ui.theme.CallBudyTheme

@Composable
fun LauncherHomeScreen(
        onNavigateToRoutineSelection: () -> Unit,
        modifier: Modifier = Modifier,
        viewModel: LauncherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LauncherHomeContent(
            currentRoutineName = uiState.currentRoutine?.name ?: "No routine active",
            currentRoutineType = uiState.currentRoutine?.type,
            availableApps = uiState.availableApps,
            predictions = uiState.predictions,
            isLoading = uiState.isLoading,
            error = uiState.error,
            onNavigateToRoutineSelection = onNavigateToRoutineSelection,
            onAppClick = viewModel::launchApp,
            onClearError = viewModel::clearError,
            modifier = modifier
    )
}

@Composable
private fun LauncherHomeContent(
        currentRoutineName: String,
        currentRoutineType: RoutineType?,
        availableApps: List<AppInfo>,
        predictions: List<ActionPrediction>,
        isLoading: Boolean,
        error: String?,
        onNavigateToRoutineSelection: () -> Unit,
        onAppClick: (AppInfo) -> Unit,
        onClearError: () -> Unit,
        modifier: Modifier = Modifier
) {
    // Apply routine-specific theme
    RoutineTheme(routineType = currentRoutineType ?: RoutineType.MORNING) {
        Box(modifier = modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                    text = "Error: $error",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                currentRoutineType != null && availableApps.isNotEmpty() -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Routine header
                        Column(
                                modifier =
                                        Modifier.padding(16.dp).clickable {
                                            onNavigateToRoutineSelection()
                                        },
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                    text = currentRoutineName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                            )
                            Text(
                                    text = "Tap to change routine",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Real-time adaptive UI with contextual generation
                        RealTimeUIUpdater(
                                predictions = predictions,
                                routineType = currentRoutineType,
                                availableApps = availableApps,
                                onAppClick = onAppClick,
                                modifier = Modifier.weight(1f)
                        )
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                    text = "Setting up launcher...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                            )
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun LauncherHomeScreenPreview() {
        CallBudyTheme {
            LauncherHomeContent(
                    currentRoutineName = "Morning Routine",
                    currentRoutineType = RoutineType.MORNING,
                    availableApps = MockDataProvider.mockApps.take(12),
                    predictions = MockDataProvider.getMockActionPredictions(RoutineType.MORNING),
                    isLoading = false,
                    error = null,
                    onNavigateToRoutineSelection = {},
                    onAppClick = {},
                    onClearError = {}
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun LauncherHomeScreenLoadingPreview() {
        CallBudyTheme {
            LauncherHomeContent(
                    currentRoutineName = "",
                    currentRoutineType = null,
                    availableApps = emptyList(),
                    predictions = emptyList(),
                    isLoading = true,
                    error = null,
                    onNavigateToRoutineSelection = {},
                    onAppClick = {},
                    onClearError = {}
            )
        }
    }
}
