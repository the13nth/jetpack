package com.meticha.jetpackboilerplate.launcher.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meticha.jetpackboilerplate.launcher.data.models.AppInfo
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.data.mock.ActionPrediction
import com.meticha.jetpackboilerplate.launcher.ui.generator.LauncherUIState
import com.meticha.jetpackboilerplate.launcher.ui.generator.UIGenerator
import com.meticha.jetpackboilerplate.launcher.ui.theme.RoutineTheme
import kotlinx.coroutines.delay

/**
 * Enhanced component that handles real-time contextual UI updates
 * Adapts UI based on predictions, routine changes, and user behavior patterns
 */
@Composable
fun RealTimeUIUpdater(
    predictions: List<ActionPrediction>,
    routineType: RoutineType,
    availableApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentUIState by remember { mutableStateOf<LauncherUIState?>(null) }
    var isUpdating by remember { mutableStateOf(false) }
    var lastUpdateTime by remember { mutableStateOf(0L) }
    var pendingUpdate by remember { mutableStateOf(false) }
    
    // Generate initial UI state with routine-specific theming
    LaunchedEffect(Unit) {
        isUpdating = true
        currentUIState = UIGenerator.generateLauncherUI(
            predictions = predictions,
            routineType = routineType,
            availableApps = availableApps
        )
        lastUpdateTime = System.currentTimeMillis()
        isUpdating = false
    }
    
    // Handle intelligent real-time updates with batching and debouncing
    LaunchedEffect(predictions, routineType) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastUpdate = currentTime - lastUpdateTime
        
        if (currentUIState != null) {
            // Immediate update for routine changes, debounced for prediction changes
            val shouldUpdateImmediately = timeSinceLastUpdate > 2000 || // Force update after 2 seconds
                    hasSignificantRoutineChange(currentUIState!!.theme.name, routineType)
            
            if (shouldUpdateImmediately) {
                performUIUpdate(predictions, routineType, availableApps) { updatedUI ->
                    currentUIState = updatedUI
                    lastUpdateTime = currentTime
                    isUpdating = false
                }
            } else {
                // Batch rapid updates
                pendingUpdate = true
                delay(150) // Debounce rapid changes
                
                if (pendingUpdate) {
                    performUIUpdate(predictions, routineType, availableApps) { updatedUI ->
                        currentUIState = updatedUI
                        lastUpdateTime = System.currentTimeMillis()
                        pendingUpdate = false
                        isUpdating = false
                    }
                }
            }
        }
    }
    
    // Apply routine-specific theming with smooth transitions
    RoutineTheme(routineType = routineType) {
        currentUIState?.let { uiState ->
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    val duration = targetState.layout.transitionDuration
                    
                    slideInVertically(
                        initialOffsetY = { it / 6 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(
                        animationSpec = tween(duration)
                    ) + scaleIn(
                        initialScale = 0.95f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) togetherWith slideOutVertically(
                        targetOffsetY = { -it / 6 },
                        animationSpec = tween(duration / 2)
                    ) + fadeOut(
                        animationSpec = tween(duration / 2)
                    ) + scaleOut(
                        targetScale = 1.05f,
                        animationSpec = tween(duration / 2)
                    )
                },
                modifier = modifier,
                label = "contextual_ui_transition"
            ) { animatedUIState ->
                Box(modifier = Modifier.fillMaxSize()) {
                    ContextualUIRenderer(
                        uiState = animatedUIState,
                        routineType = routineType,
                        onAppClick = onAppClick,
                        isUpdating = isUpdating
                    )
                    
                    // Show subtle loading indicator during updates
                    AnimatedVisibility(
                        visible = isUpdating,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Perform UI update with proper state management
 */
private suspend fun performUIUpdate(
    predictions: List<ActionPrediction>,
    routineType: RoutineType,
    availableApps: List<AppInfo>,
    onComplete: (LauncherUIState) -> Unit
) {
    try {
        val updatedUI = UIGenerator.generateLauncherUI(
            predictions = predictions,
            routineType = routineType,
            availableApps = availableApps
        )
        onComplete(updatedUI)
    } catch (e: Exception) {
        // Fallback to basic UI generation on error
        val fallbackUI = UIGenerator.generateLauncherUI(
            predictions = emptyList(),
            routineType = routineType,
            availableApps = availableApps
        )
        onComplete(fallbackUI)
    }
}

/**
 * Check if there's a significant routine change requiring immediate update
 */
private fun hasSignificantRoutineChange(currentThemeName: String, newRoutineType: RoutineType): Boolean {
    val expectedThemeName = when (newRoutineType) {
        RoutineType.MORNING -> "Morning Fresh"
        RoutineType.AFTERNOON -> "Productive Focus"
        RoutineType.EVENING -> "Evening Calm"
        RoutineType.WEEKEND -> "Weekend Leisure"
        RoutineType.CUSTOM -> "Custom"
    }
    return currentThemeName != expectedThemeName
}

/**
 * Enhanced contextual UI renderer that adapts based on predictions and routine
 */
@Composable
private fun ContextualUIRenderer(
    uiState: LauncherUIState,
    routineType: RoutineType,
    onAppClick: (AppInfo) -> Unit,
    isUpdating: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Render contextual widgets if enabled
        if (uiState.layout.showContextualWidgets && uiState.widgets.isNotEmpty()) {
            ContextualWidgets(
                widgets = uiState.widgets.filter { it.isVisible },
                routineType = routineType,
                modifier = Modifier.padding(horizontal = uiState.layout.adaptiveSpacing.dp)
            )
        }
        
        // Render prediction cards if enabled
        if (uiState.layout.showPredictionCards && uiState.primaryActions.isNotEmpty()) {
            ContextualPredictionCards(
                primaryActions = uiState.primaryActions,
                secondaryActions = uiState.secondaryActions,
                routineType = routineType,
                isUpdating = isUpdating,
                modifier = Modifier.padding(horizontal = uiState.layout.adaptiveSpacing.dp)
            )
        }
        
        // Render adaptive app grid with routine-aware layout
        RoutineAwareLayout(
            apps = uiState.appGrid.apps,
            currentRoutine = routineType,
            predictions = uiState.primaryActions.map { actionCard ->
                ActionPrediction(
                    action = actionCard.action,
                    confidence = actionCard.confidence,
                    associatedApps = actionCard.apps,
                    reasoning = actionCard.reasoning,
                    priority = actionCard.visualPriority
                )
            },
            onAppClick = onAppClick,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = uiState.layout.adaptiveSpacing.dp)
        )
    }
}

/**
 * Legacy renderer for backward compatibility
 */
@Composable
private fun AdaptiveUIRenderer(
    uiState: LauncherUIState,
    routineType: RoutineType,
    onAppClick: (AppInfo) -> Unit,
    isUpdating: Boolean,
    modifier: Modifier = Modifier
) {
    ContextualUIRenderer(
        uiState = uiState,
        routineType = routineType,
        onAppClick = onAppClick,
        isUpdating = isUpdating,
        modifier = modifier
    )
}