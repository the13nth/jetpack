package com.meticha.jetpackboilerplate.launcher.ui.generator

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.meticha.jetpackboilerplate.launcher.data.models.AppInfo
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.data.mock.ActionPrediction
import com.meticha.jetpackboilerplate.launcher.ui.components.WidgetType

/**
 * UI Generator that creates contextual interfaces based on predictions and routine
 */
object UIGenerator {
    
    /**
     * Generate launcher UI state based on predictions and routine
     */
    fun generateLauncherUI(
        predictions: List<ActionPrediction>,
        routineType: RoutineType,
        availableApps: List<AppInfo>
    ): LauncherUIState {
        // Analyze prediction patterns for UI adaptation
        val predictionAnalysis = analyzePredictionPatterns(predictions, routineType)
        
        return LauncherUIState(
            primaryActions = generatePrimaryActions(predictions, routineType),
            secondaryActions = generateSecondaryActions(predictions, routineType),
            widgets = generateWidgetLayout(routineType, predictions, predictionAnalysis),
            layout = generateLayoutConfig(routineType, predictions, predictionAnalysis),
            theme = generateThemeConfig(routineType, predictionAnalysis),
            appGrid = generateAppGridConfig(routineType, predictions, availableApps)
        )
    }
    
    /**
     * Generate real-time UI updates when predictions change
     */
    fun generateUIUpdate(
        currentUI: LauncherUIState,
        newPredictions: List<ActionPrediction>,
        routineType: RoutineType,
        availableApps: List<AppInfo>
    ): LauncherUIState {
        val predictionAnalysis = analyzePredictionPatterns(newPredictions, routineType)
        
        // Only update components that need changes based on new predictions
        return currentUI.copy(
            primaryActions = if (shouldUpdatePrimaryActions(currentUI.primaryActions, newPredictions)) {
                generatePrimaryActions(newPredictions, routineType)
            } else currentUI.primaryActions,
            
            secondaryActions = if (shouldUpdateSecondaryActions(currentUI.secondaryActions, newPredictions)) {
                generateSecondaryActions(newPredictions, routineType)
            } else currentUI.secondaryActions,
            
            widgets = generateWidgetLayout(routineType, newPredictions, predictionAnalysis),
            
            layout = if (predictionAnalysis.requiresLayoutChange) {
                generateLayoutConfig(routineType, newPredictions, predictionAnalysis)
            } else currentUI.layout,
            
            appGrid = generateAppGridConfig(routineType, newPredictions, availableApps)
        )
    }
    
    /**
     * Analyze prediction patterns to inform UI generation decisions
     */
    private fun analyzePredictionPatterns(
        predictions: List<ActionPrediction>,
        routineType: RoutineType
    ): PredictionAnalysis {
        val highConfidencePredictions = predictions.filter { it.confidence >= 0.8f }
        val mediumConfidencePredictions = predictions.filter { it.confidence in 0.5f..0.8f }
        val lowConfidencePredictions = predictions.filter { it.confidence < 0.5f }
        
        val averageConfidence = predictions.map { it.confidence }.average().toFloat()
        val predictionDiversity = calculatePredictionDiversity(predictions)
        val routineAlignment = calculateRoutineAlignment(predictions, routineType)
        
        return PredictionAnalysis(
            totalPredictions = predictions.size,
            highConfidenceCount = highConfidencePredictions.size,
            mediumConfidenceCount = mediumConfidencePredictions.size,
            lowConfidenceCount = lowConfidencePredictions.size,
            averageConfidence = averageConfidence,
            predictionDiversity = predictionDiversity,
            routineAlignment = routineAlignment,
            requiresLayoutChange = shouldChangeLayout(averageConfidence, predictionDiversity),
            suggestedFocusLevel = calculateFocusLevel(averageConfidence, routineAlignment)
        )
    }
    
    /**
     * Calculate diversity of predictions (variety of action types)
     */
    private fun calculatePredictionDiversity(predictions: List<ActionPrediction>): Float {
        if (predictions.isEmpty()) return 0f
        
        val uniqueActionTypes = predictions.map { prediction ->
            when {
                prediction.action.contains("meditation", ignoreCase = true) -> "wellness"
                prediction.action.contains("work", ignoreCase = true) -> "productivity"
                prediction.action.contains("meeting", ignoreCase = true) -> "communication"
                prediction.action.contains("entertainment", ignoreCase = true) -> "leisure"
                prediction.action.contains("social", ignoreCase = true) -> "social"
                else -> "general"
            }
        }.toSet()
        
        return uniqueActionTypes.size / 6f // Normalize by max possible types
    }
    
    /**
     * Calculate how well predictions align with routine expectations
     */
    private fun calculateRoutineAlignment(
        predictions: List<ActionPrediction>,
        routineType: RoutineType
    ): Float {
        if (predictions.isEmpty()) return 0f
        
        val expectedCategories = when (routineType) {
            RoutineType.MORNING -> setOf("wellness", "productivity", "communication")
            RoutineType.AFTERNOON -> setOf("productivity", "communication", "work")
            RoutineType.EVENING -> setOf("leisure", "social", "entertainment")
            RoutineType.WEEKEND -> setOf("leisure", "social", "wellness", "entertainment")
            RoutineType.CUSTOM -> setOf("general")
        }
        
        val alignedPredictions = predictions.count { prediction ->
            val actionType = categorizeAction(prediction.action)
            expectedCategories.contains(actionType)
        }
        
        return alignedPredictions.toFloat() / predictions.size
    }
    
    /**
     * Categorize action for alignment calculation
     */
    private fun categorizeAction(action: String): String {
        return when {
            action.contains("meditation", ignoreCase = true) || 
            action.contains("wellness", ignoreCase = true) -> "wellness"
            action.contains("work", ignoreCase = true) || 
            action.contains("productivity", ignoreCase = true) -> "productivity"
            action.contains("meeting", ignoreCase = true) || 
            action.contains("communication", ignoreCase = true) -> "communication"
            action.contains("entertainment", ignoreCase = true) || 
            action.contains("watch", ignoreCase = true) -> "entertainment"
            action.contains("social", ignoreCase = true) || 
            action.contains("connect", ignoreCase = true) -> "social"
            else -> "general"
        }
    }
    
    /**
     * Determine if layout should change based on prediction characteristics
     */
    private fun shouldChangeLayout(averageConfidence: Float, diversity: Float): Boolean {
        return averageConfidence > 0.8f || diversity < 0.3f
    }
    
    /**
     * Calculate suggested focus level for UI density
     */
    private fun calculateFocusLevel(averageConfidence: Float, routineAlignment: Float): FocusLevel {
        return when {
            averageConfidence > 0.8f && routineAlignment > 0.7f -> FocusLevel.HIGH
            averageConfidence > 0.6f && routineAlignment > 0.5f -> FocusLevel.MEDIUM
            else -> FocusLevel.LOW
        }
    }
    
    /**
     * Check if primary actions need updating
     */
    private fun shouldUpdatePrimaryActions(
        currentActions: List<ActionCard>,
        newPredictions: List<ActionPrediction>
    ): Boolean {
        val newHighConfidencePredictions = newPredictions.filter { it.confidence >= 0.7f }.take(3)
        return currentActions.size != newHighConfidencePredictions.size ||
                currentActions.zip(newHighConfidencePredictions).any { (current, new) ->
                    current.action != new.action || kotlin.math.abs(current.confidence - new.confidence) > 0.1f
                }
    }
    
    /**
     * Check if secondary actions need updating
     */
    private fun shouldUpdateSecondaryActions(
        currentActions: List<ActionCard>,
        newPredictions: List<ActionPrediction>
    ): Boolean {
        val newMediumConfidencePredictions = newPredictions.filter { it.confidence in 0.4f..0.7f }.take(4)
        return currentActions.size != newMediumConfidencePredictions.size ||
                currentActions.zip(newMediumConfidencePredictions).any { (current, new) ->
                    current.action != new.action
                }
    }
    
    /**
     * Generate primary action cards based on top predictions
     */
    private fun generatePrimaryActions(
        predictions: List<ActionPrediction>,
        routineType: RoutineType
    ): List<ActionCard> {
        return predictions
            .filter { it.confidence >= 0.7f } // High confidence predictions
            .take(3) // Top 3 primary actions
            .map { prediction ->
                ActionCard(
                    action = prediction.action,
                    apps = prediction.associatedApps,
                    confidence = prediction.confidence,
                    quickActions = generateQuickActions(prediction, routineType),
                    visualPriority = calculateVisualPriority(prediction, routineType),
                    reasoning = prediction.reasoning
                )
            }
    }
    
    /**
     * Generate secondary action cards for medium confidence predictions
     */
    private fun generateSecondaryActions(
        predictions: List<ActionPrediction>,
        routineType: RoutineType
    ): List<ActionCard> {
        return predictions
            .filter { it.confidence in 0.4f..0.7f } // Medium confidence predictions
            .take(4) // Up to 4 secondary actions
            .map { prediction ->
                ActionCard(
                    action = prediction.action,
                    apps = prediction.associatedApps,
                    confidence = prediction.confidence,
                    quickActions = generateQuickActions(prediction, routineType),
                    visualPriority = calculateVisualPriority(prediction, routineType),
                    reasoning = prediction.reasoning
                )
            }
    }
    
    /**
     * Generate widget layout based on routine and predictions
     */
    private fun generateWidgetLayout(
        routineType: RoutineType,
        predictions: List<ActionPrediction>,
        predictionAnalysis: PredictionAnalysis
    ): List<WidgetConfig> {
        // Adapt widget visibility and priority based on prediction analysis
        val baseWidgets = when (routineType) {
            RoutineType.MORNING -> listOf(
                WidgetConfig(WidgetType.TIME, priority = 1, isVisible = true),
                WidgetConfig(WidgetType.WELLNESS, priority = 2, isVisible = true),
                WidgetConfig(WidgetType.SCHEDULE, priority = 3, isVisible = predictions.isNotEmpty())
            )
            RoutineType.AFTERNOON -> listOf(
                WidgetConfig(WidgetType.PRODUCTIVITY, priority = 1, isVisible = true),
                WidgetConfig(WidgetType.TIME, priority = 2, isVisible = true),
                WidgetConfig(WidgetType.NOTIFICATIONS, priority = 3, isVisible = predictions.size > 2)
            )
            RoutineType.EVENING -> listOf(
                WidgetConfig(WidgetType.TIME, priority = 1, isVisible = true),
                WidgetConfig(WidgetType.WELLNESS, priority = 2, isVisible = true),
                WidgetConfig(WidgetType.SCHEDULE, priority = 3, isVisible = true)
            )
            RoutineType.WEEKEND -> listOf(
                WidgetConfig(WidgetType.TIME, priority = 1, isVisible = true),
                WidgetConfig(WidgetType.WELLNESS, priority = 2, isVisible = true),
                WidgetConfig(WidgetType.PRODUCTIVITY, priority = 3, isVisible = predictions.isNotEmpty())
            )
            RoutineType.CUSTOM -> listOf(
                WidgetConfig(WidgetType.TIME, priority = 1, isVisible = true),
                WidgetConfig(WidgetType.NOTIFICATIONS, priority = 2, isVisible = predictions.isNotEmpty())
            )
        }
        
        // Adjust widget priorities based on prediction analysis
        return baseWidgets.map { widget ->
            when (predictionAnalysis.suggestedFocusLevel) {
                FocusLevel.HIGH -> widget.copy(
                    isVisible = widget.priority <= 2 // Show only top 2 widgets for high focus
                )
                FocusLevel.MEDIUM -> widget.copy(
                    isVisible = widget.isVisible && widget.priority <= 3
                )
                FocusLevel.LOW -> widget // Show all widgets for low focus
            }
        }
    }
    
    /**
     * Generate layout configuration based on routine and predictions
     */
    private fun generateLayoutConfig(
        routineType: RoutineType,
        predictions: List<ActionPrediction>,
        predictionAnalysis: PredictionAnalysis
    ): LayoutConfig {
        // Adapt grid columns based on focus level and routine
        val baseColumns = when (routineType) {
            RoutineType.MORNING -> 3 // Focused morning layout
            RoutineType.AFTERNOON -> 4 // Productive afternoon layout
            RoutineType.EVENING -> 3 // Relaxed evening layout
            RoutineType.WEEKEND -> 4 // Diverse weekend layout
            RoutineType.CUSTOM -> 3
        }
        
        val gridColumns = when (predictionAnalysis.suggestedFocusLevel) {
            FocusLevel.HIGH -> kotlin.math.max(2, baseColumns - 1) // Reduce columns for high focus
            FocusLevel.MEDIUM -> baseColumns
            FocusLevel.LOW -> kotlin.math.min(5, baseColumns + 1) // Increase columns for low focus
        }
        
        val showPredictions = predictions.isNotEmpty() && predictionAnalysis.averageConfidence > 0.3f
        val showWidgets = predictionAnalysis.suggestedFocusLevel != FocusLevel.HIGH
        
        return LayoutConfig(
            gridColumns = gridColumns,
            showPredictionCards = showPredictions,
            showContextualWidgets = showWidgets,
            adaptiveSpacing = calculateAdaptiveSpacing(routineType, predictions.size, predictionAnalysis),
            transitionDuration = if (predictionAnalysis.requiresLayoutChange) 600 else 400
        )
    }
    
    /**
     * Generate theme configuration for routine-specific styling
     */
    private fun generateThemeConfig(routineType: RoutineType, predictionAnalysis: PredictionAnalysis): ThemeConfig {
        val baseTheme = when (routineType) {
            RoutineType.MORNING -> ThemeConfig(
                name = "Morning Fresh",
                primaryColorHue = 200f, // Blue tones
                saturation = 0.6f,
                brightness = 0.9f,
                cardElevation = 4f,
                cornerRadius = 16f
            )
            RoutineType.AFTERNOON -> ThemeConfig(
                name = "Productive Focus",
                primaryColorHue = 260f, // Purple tones
                saturation = 0.7f,
                brightness = 0.8f,
                cardElevation = 6f,
                cornerRadius = 12f
            )
            RoutineType.EVENING -> ThemeConfig(
                name = "Evening Calm",
                primaryColorHue = 30f, // Orange/amber tones
                saturation = 0.5f,
                brightness = 0.7f,
                cardElevation = 8f,
                cornerRadius = 20f
            )
            RoutineType.WEEKEND -> ThemeConfig(
                name = "Weekend Leisure",
                primaryColorHue = 120f, // Green tones
                saturation = 0.6f,
                brightness = 0.8f,
                cardElevation = 5f,
                cornerRadius = 18f
            )
            RoutineType.CUSTOM -> ThemeConfig(
                name = "Custom",
                primaryColorHue = 0f,
                saturation = 0.5f,
                brightness = 0.8f,
                cardElevation = 4f,
                cornerRadius = 16f
            )
        }
        
        // Adapt theme based on prediction analysis
        return when (predictionAnalysis.suggestedFocusLevel) {
            FocusLevel.HIGH -> baseTheme.copy(
                saturation = baseTheme.saturation * 0.8f, // Reduce saturation for focus
                cardElevation = baseTheme.cardElevation + 2f, // Increase elevation for emphasis
                cornerRadius = baseTheme.cornerRadius + 4f // Softer corners for calm
            )
            FocusLevel.MEDIUM -> baseTheme
            FocusLevel.LOW -> baseTheme.copy(
                saturation = baseTheme.saturation * 1.2f, // Increase saturation for vibrancy
                cardElevation = baseTheme.cardElevation - 1f, // Reduce elevation for density
                cornerRadius = baseTheme.cornerRadius - 2f // Sharper corners for efficiency
            )
        }
    }
    
    /**
     * Generate app grid configuration based on predictions
     */
    private fun generateAppGridConfig(
        routineType: RoutineType,
        predictions: List<ActionPrediction>,
        availableApps: List<AppInfo>
    ): AppGridConfig {
        val predictedAppPackages = predictions.flatMap { prediction ->
            prediction.associatedApps.map { it.packageName }
        }.toSet()
        
        val prioritizedApps = availableApps.sortedWith(
            compareByDescending<AppInfo> { app ->
                if (predictedAppPackages.contains(app.packageName)) 1.0f else 0.0f
            }.thenByDescending { app ->
                getRoutineCategoryPriority(app.category, routineType)
            }.thenByDescending { app ->
                app.usageCount
            }
        )
        
        return AppGridConfig(
            apps = prioritizedApps,
            highlightPredicted = true,
            animateChanges = true,
            groupByCategory = routineType == RoutineType.AFTERNOON
        )
    }
    
    /**
     * Generate contextual quick actions for a prediction based on routine and action type
     */
    private fun generateQuickActions(
        prediction: ActionPrediction,
        routineType: RoutineType
    ): List<QuickAction> {
        val baseActions = mutableListOf<QuickAction>()
        
        // Add primary app launch if available
        prediction.associatedApps.firstOrNull()?.let { app ->
            baseActions.add(
                QuickAction(
                    label = "Open ${app.appName}",
                    action = QuickActionType.LAUNCH_APP,
                    data = app.packageName
                )
            )
        }
        
        // Add routine-specific contextual quick actions
        when (routineType) {
            RoutineType.MORNING -> {
                when {
                    prediction.action.contains("meditation", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Start 10min session",
                                action = QuickActionType.START_TIMER,
                                data = "600"
                            )
                        )
                    }
                    prediction.action.contains("email", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Quick scan",
                                action = QuickActionType.QUICK_JOIN,
                                data = "email_scan"
                            )
                        )
                    }
                    prediction.action.contains("plan", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Today's agenda",
                                action = QuickActionType.START_ACTIVITY,
                                data = "daily_planning"
                            )
                        )
                    }
                }
            }
            RoutineType.AFTERNOON -> {
                when {
                    prediction.action.contains("meeting", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Join meeting",
                                action = QuickActionType.QUICK_JOIN,
                                data = "meeting"
                            )
                        )
                    }
                    prediction.action.contains("work", ignoreCase = true) || 
                    prediction.action.contains("project", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Focus mode",
                                action = QuickActionType.START_ACTIVITY,
                                data = "focus_mode"
                            )
                        )
                    }
                    prediction.action.contains("documentation", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "New document",
                                action = QuickActionType.START_ACTIVITY,
                                data = "new_document"
                            )
                        )
                    }
                }
            }
            RoutineType.EVENING -> {
                when {
                    prediction.action.contains("entertainment", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Continue watching",
                                action = QuickActionType.RESUME_CONTENT,
                                data = "last_watched"
                            )
                        )
                    }
                    prediction.action.contains("music", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Evening playlist",
                                action = QuickActionType.RESUME_CONTENT,
                                data = "evening_playlist"
                            )
                        )
                    }
                    prediction.action.contains("social", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Check messages",
                                action = QuickActionType.QUICK_JOIN,
                                data = "social_check"
                            )
                        )
                    }
                }
            }
            RoutineType.WEEKEND -> {
                when {
                    prediction.action.contains("fitness", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Start workout",
                                action = QuickActionType.START_ACTIVITY,
                                data = "workout"
                            )
                        )
                    }
                    prediction.action.contains("hobby", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Creative time",
                                action = QuickActionType.START_ACTIVITY,
                                data = "creative_session"
                            )
                        )
                    }
                    prediction.action.contains("leisure", ignoreCase = true) -> {
                        baseActions.add(
                            QuickAction(
                                label = "Explore interests",
                                action = QuickActionType.START_ACTIVITY,
                                data = "leisure_exploration"
                            )
                        )
                    }
                }
            }
            RoutineType.CUSTOM -> {
                // Generic contextual actions for custom routines
                if (prediction.confidence > 0.8f) {
                    baseActions.add(
                        QuickAction(
                            label = "Quick start",
                            action = QuickActionType.START_ACTIVITY,
                            data = "quick_start"
                        )
                    )
                }
            }
        }
        
        return baseActions.take(2) // Limit to 2 quick actions per prediction
    }
    
    /**
     * Calculate visual priority for action cards
     */
    private fun calculateVisualPriority(
        prediction: ActionPrediction,
        routineType: RoutineType
    ): Int {
        var priority = (prediction.confidence * 10).toInt()
        
        // Boost priority for routine-specific actions
        when (routineType) {
            RoutineType.MORNING -> {
                if (prediction.action.contains("meditation") || 
                    prediction.action.contains("wellness")) {
                    priority += 2
                }
            }
            RoutineType.AFTERNOON -> {
                if (prediction.action.contains("work") || 
                    prediction.action.contains("meeting")) {
                    priority += 2
                }
            }
            RoutineType.EVENING -> {
                if (prediction.action.contains("relax") || 
                    prediction.action.contains("entertainment")) {
                    priority += 2
                }
            }
            RoutineType.WEEKEND -> {
                if (prediction.action.contains("hobby") || 
                    prediction.action.contains("social")) {
                    priority += 2
                }
            }
            RoutineType.CUSTOM -> {
                // No specific boosts for custom routines
            }
        }
        
        return priority.coerceIn(1, 10)
    }
    
    /**
     * Calculate adaptive spacing based on routine and prediction analysis
     */
    private fun calculateAdaptiveSpacing(
        routineType: RoutineType,
        predictionCount: Int,
        predictionAnalysis: PredictionAnalysis
    ): Float {
        val baseSpacing = when (routineType) {
            RoutineType.MORNING -> 16f // More spacious for calm morning
            RoutineType.AFTERNOON -> 12f // Compact for productivity
            RoutineType.EVENING -> 18f // Relaxed spacing
            RoutineType.WEEKEND -> 14f // Balanced spacing
            RoutineType.CUSTOM -> 16f
        }
        
        // Adjust spacing based on content density and focus level
        val densityMultiplier = when {
            predictionCount > 4 -> 0.8f // Tighter spacing for more content
            predictionCount < 2 -> 1.2f // Looser spacing for less content
            else -> 1.0f
        }
        
        // Further adjust based on focus level
        val focusMultiplier = when (predictionAnalysis.suggestedFocusLevel) {
            FocusLevel.HIGH -> 1.3f // More spacious for high focus
            FocusLevel.MEDIUM -> 1.0f // Normal spacing
            FocusLevel.LOW -> 0.9f // Tighter spacing for more content
        }
        
        return baseSpacing * densityMultiplier * focusMultiplier
    }
    
    /**
     * Get category priority for specific routine
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
}

/**
 * Data classes for UI generation
 */
data class LauncherUIState(
    val primaryActions: List<ActionCard>,
    val secondaryActions: List<ActionCard>,
    val widgets: List<WidgetConfig>,
    val layout: LayoutConfig,
    val theme: ThemeConfig,
    val appGrid: AppGridConfig
)

data class ActionCard(
    val action: String,
    val apps: List<AppInfo>,
    val confidence: Float,
    val quickActions: List<QuickAction>,
    val visualPriority: Int,
    val reasoning: String
)

data class QuickAction(
    val label: String,
    val action: QuickActionType,
    val data: String
)

enum class QuickActionType {
    LAUNCH_APP,
    START_TIMER,
    QUICK_JOIN,
    RESUME_CONTENT,
    START_ACTIVITY
}

data class WidgetConfig(
    val type: WidgetType,
    val priority: Int,
    val isVisible: Boolean
)

data class LayoutConfig(
    val gridColumns: Int,
    val showPredictionCards: Boolean,
    val showContextualWidgets: Boolean,
    val adaptiveSpacing: Float,
    val transitionDuration: Int
)

data class ThemeConfig(
    val name: String,
    val primaryColorHue: Float,
    val saturation: Float,
    val brightness: Float,
    val cardElevation: Float,
    val cornerRadius: Float
)

data class AppGridConfig(
    val apps: List<AppInfo>,
    val highlightPredicted: Boolean,
    val animateChanges: Boolean,
    val groupByCategory: Boolean
)

/**
 * Analysis of prediction patterns to inform UI generation
 */
data class PredictionAnalysis(
    val totalPredictions: Int,
    val highConfidenceCount: Int,
    val mediumConfidenceCount: Int,
    val lowConfidenceCount: Int,
    val averageConfidence: Float,
    val predictionDiversity: Float,
    val routineAlignment: Float,
    val requiresLayoutChange: Boolean,
    val suggestedFocusLevel: FocusLevel
)

/**
 * Focus level for UI density and emphasis
 */
enum class FocusLevel {
    HIGH,    // Minimal UI, high confidence predictions
    MEDIUM,  // Balanced UI with moderate predictions
    LOW      // Full UI with diverse options
}