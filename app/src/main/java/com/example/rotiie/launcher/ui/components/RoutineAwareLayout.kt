package com.example.rotiie.launcher.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rotiie.launcher.data.models.AppInfo
import com.example.rotiie.launcher.data.models.RoutineType
import com.example.rotiie.launcher.data.mock.ActionPrediction
import com.example.rotiie.launcher.data.mock.MockDataProvider
import com.example.rotiie.ui.theme.CallBudyTheme
import kotlinx.coroutines.delay

/**
 * Layout manager that adapts the app grid based on routine changes
 * Handles smooth transitions between different routine layouts
 */
@Composable
fun RoutineAwareLayout(
    apps: List<AppInfo>,
    currentRoutine: RoutineType,
    predictions: List<ActionPrediction>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var layoutKey by remember { mutableStateOf(currentRoutine) }
    var isTransitioning by remember { mutableStateOf(false) }

    // Handle routine changes with transition delay
    LaunchedEffect(currentRoutine) {
        if (layoutKey != currentRoutine) {
            isTransitioning = true
            delay(150) // Brief delay for smooth transition
            layoutKey = currentRoutine
            delay(300) // Allow transition to complete
            isTransitioning = false
        }
    }

    // Filter and prioritize apps based on predictions
    val prioritizedApps = remember(apps, predictions, currentRoutine) {
        prioritizeAppsForRoutine(apps, predictions, currentRoutine)
    }

    AnimatedContent(
        targetState = layoutKey,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { width -> width },
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { width -> -width },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
        },
        modifier = modifier,
        label = "routine_layout_transition"
    ) { routine ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (routine) {
                RoutineType.MORNING -> MorningLayout(
                    apps = prioritizedApps,
                    predictions = predictions,
                    onAppClick = onAppClick,
                    isTransitioning = isTransitioning
                )
                RoutineType.AFTERNOON -> AfternoonLayout(
                    apps = prioritizedApps,
                    predictions = predictions,
                    onAppClick = onAppClick,
                    isTransitioning = isTransitioning
                )
                RoutineType.EVENING -> EveningLayout(
                    apps = prioritizedApps,
                    predictions = predictions,
                    onAppClick = onAppClick,
                    isTransitioning = isTransitioning
                )
                RoutineType.WEEKEND -> WeekendLayout(
                    apps = prioritizedApps,
                    predictions = predictions,
                    onAppClick = onAppClick,
                    isTransitioning = isTransitioning
                )
                RoutineType.CUSTOM -> CustomLayout(
                    apps = prioritizedApps,
                    predictions = predictions,
                    onAppClick = onAppClick,
                    isTransitioning = isTransitioning
                )
            }
        }
    }
}

/**
 * Morning-focused layout with emphasis on wellness and productivity apps
 */
@Composable
private fun MorningLayout(
    apps: List<AppInfo>,
    predictions: List<ActionPrediction>,
    onAppClick: (AppInfo) -> Unit,
    isTransitioning: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Contextual widgets for morning routine
        ContextualWidgets(
            routineType = RoutineType.MORNING,
            predictions = predictions,
            onWidgetClick = { /* Handle widget clicks */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Prediction-based recommendations
        PredictionRecommendationCards(
            predictions = predictions,
            routineType = RoutineType.MORNING,
            onAppClick = onAppClick,
            onQuickActionClick = { /* Handle quick actions */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Top priority apps for morning routine
        val morningApps = apps.filter { app ->
            app.category in listOf("Health", "Productivity", "Email", "News", "Weather")
        }.take(9) // 3x3 grid for focused morning experience

        DynamicAppGrid(
            apps = morningApps.ifEmpty { apps.take(9) },
            routineType = RoutineType.MORNING,
            onAppClick = onAppClick,
            isVisible = !isTransitioning,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Afternoon work-focused layout with productivity and communication apps
 */
@Composable
private fun AfternoonLayout(
    apps: List<AppInfo>,
    predictions: List<ActionPrediction>,
    onAppClick: (AppInfo) -> Unit,
    isTransitioning: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Contextual widgets for afternoon routine
        ContextualWidgets(
            routineType = RoutineType.AFTERNOON,
            predictions = predictions,
            onWidgetClick = { /* Handle widget clicks */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Prediction-based recommendations
        PredictionRecommendationCards(
            predictions = predictions,
            routineType = RoutineType.AFTERNOON,
            onAppClick = onAppClick,
            onQuickActionClick = { /* Handle quick actions */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Work-focused apps for afternoon productivity
        val workApps = apps.filter { app ->
            app.category in listOf("Work", "Productivity", "Professional", "Communication", "Browser")
        }.take(16) // 4x4 grid for comprehensive work tools

        DynamicAppGrid(
            apps = workApps.ifEmpty { apps.take(16) },
            routineType = RoutineType.AFTERNOON,
            onAppClick = onAppClick,
            isVisible = !isTransitioning,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Evening relaxation-focused layout with entertainment and social apps
 */
@Composable
private fun EveningLayout(
    apps: List<AppInfo>,
    predictions: List<ActionPrediction>,
    onAppClick: (AppInfo) -> Unit,
    isTransitioning: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Contextual widgets for evening routine
        ContextualWidgets(
            routineType = RoutineType.EVENING,
            predictions = predictions,
            onWidgetClick = { /* Handle widget clicks */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Prediction-based recommendations
        PredictionRecommendationCards(
            predictions = predictions,
            routineType = RoutineType.EVENING,
            onAppClick = onAppClick,
            onQuickActionClick = { /* Handle quick actions */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Relaxation and entertainment apps for evening
        val eveningApps = apps.filter { app ->
            app.category in listOf("Entertainment", "Social", "Reading", "Music")
        }.take(9) // 3x3 grid for relaxed evening experience

        DynamicAppGrid(
            apps = eveningApps.ifEmpty { apps.take(9) },
            routineType = RoutineType.EVENING,
            onAppClick = onAppClick,
            isVisible = !isTransitioning,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Weekend leisure-focused layout with entertainment, fitness, and social apps
 */
@Composable
private fun WeekendLayout(
    apps: List<AppInfo>,
    predictions: List<ActionPrediction>,
    onAppClick: (AppInfo) -> Unit,
    isTransitioning: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Contextual widgets for weekend routine
        ContextualWidgets(
            routineType = RoutineType.WEEKEND,
            predictions = predictions,
            onWidgetClick = { /* Handle widget clicks */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Prediction-based recommendations
        PredictionRecommendationCards(
            predictions = predictions,
            routineType = RoutineType.WEEKEND,
            onAppClick = onAppClick,
            onQuickActionClick = { /* Handle quick actions */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Leisure and personal apps for weekend
        val weekendApps = apps.filter { app ->
            app.category in listOf("Entertainment", "Social", "Fitness", "Reading", "Games", "Photography")
        }.take(16) // 4x4 grid for diverse weekend activities

        DynamicAppGrid(
            apps = weekendApps.ifEmpty { apps.take(16) },
            routineType = RoutineType.WEEKEND,
            onAppClick = onAppClick,
            isVisible = !isTransitioning,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Custom layout that adapts based on user preferences
 */
@Composable
private fun CustomLayout(
    apps: List<AppInfo>,
    predictions: List<ActionPrediction>,
    onAppClick: (AppInfo) -> Unit,
    isTransitioning: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Contextual widgets for custom routine
        ContextualWidgets(
            routineType = RoutineType.CUSTOM,
            predictions = predictions,
            onWidgetClick = { /* Handle widget clicks */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Prediction-based recommendations
        PredictionRecommendationCards(
            predictions = predictions,
            routineType = RoutineType.CUSTOM,
            onAppClick = onAppClick,
            onQuickActionClick = { /* Handle quick actions */ },
            isVisible = !isTransitioning,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Default layout for custom routines
        DynamicAppGrid(
            apps = apps.take(12),
            routineType = RoutineType.CUSTOM,
            onAppClick = onAppClick,
            isVisible = !isTransitioning,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Prioritize apps based on routine context and AI predictions
 */
private fun prioritizeAppsForRoutine(
    apps: List<AppInfo>,
    predictions: List<ActionPrediction>,
    routineType: RoutineType
): List<AppInfo> {
    // Create a map of predicted app packages for quick lookup
    val predictedApps = predictions.flatMap { prediction ->
        prediction.associatedApps.map { it.packageName }
    }.toSet()

    // Sort apps with prediction-based priority
    return apps.sortedWith(
        compareByDescending<AppInfo> { app ->
            // Boost priority if app is in predictions
            if (predictedApps.contains(app.packageName)) 1.0f else 0.0f
        }.thenByDescending { app ->
            // Routine-specific category priority
            getRoutineCategoryPriority(app.category, routineType)
        }.thenByDescending { app ->
            // Usage-based priority
            app.usageCount
        }.thenByDescending { app ->
            // Recency priority
            app.lastUsed ?: 0L
        }.thenBy { app ->
            // Alphabetical as final tiebreaker
            app.appName
        }
    )
}

/**
 * Get category priority score for specific routine
 */
private fun getRoutineCategoryPriority(category: String?, routineType: RoutineType): Float {
    return when (routineType) {
        RoutineType.MORNING -> when (category) {
            "Health" -> 1.0f
            "Productivity" -> 0.9f
            "Email" -> 0.8f
            "News" -> 0.7f
            "Weather" -> 0.6f
            else -> 0.0f
        }
        RoutineType.AFTERNOON -> when (category) {
            "Work" -> 1.0f
            "Productivity" -> 0.9f
            "Professional" -> 0.8f
            "Communication" -> 0.7f
            "Browser" -> 0.6f
            else -> 0.0f
        }
        RoutineType.EVENING -> when (category) {
            "Entertainment" -> 1.0f
            "Social" -> 0.9f
            "Reading" -> 0.8f
            "Music" -> 0.7f
            else -> 0.0f
        }
        RoutineType.WEEKEND -> when (category) {
            "Entertainment" -> 1.0f
            "Social" -> 0.9f
            "Fitness" -> 0.8f
            "Reading" -> 0.7f
            "Games" -> 0.6f
            "Photography" -> 0.5f
            else -> 0.0f
        }
        RoutineType.CUSTOM -> 0.0f
    }
}

@Preview(showBackground = true)
@Composable
fun RoutineAwareLayoutPreview() {
    CallBudyTheme {
        RoutineAwareLayout(
            apps = MockDataProvider.mockApps,
            currentRoutine = RoutineType.MORNING,
            predictions = MockDataProvider.getMockActionPredictions(RoutineType.MORNING),
            onAppClick = {}
        )
    }
}