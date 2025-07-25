package com.meticha.jetpackboilerplate.launcher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.ui.generator.ActionCard
import com.meticha.jetpackboilerplate.launcher.ui.generator.WidgetConfig
import com.meticha.jetpackboilerplate.launcher.ui.theme.getRoutineAccentColor
import com.meticha.jetpackboilerplate.launcher.ui.theme.getRoutineCardColor

/**
 * Enhanced contextual prediction cards that adapt to routine and confidence levels
 */
@Composable
fun ContextualPredictionCards(
    primaryActions: List<ActionCard>,
    secondaryActions: List<ActionCard>,
    routineType: RoutineType,
    isUpdating: Boolean,
    modifier: Modifier = Modifier
) {
    val accentColor = getRoutineAccentColor(routineType)
    val cardColor = getRoutineCardColor(routineType)
    
    Column(modifier = modifier) {
        // Primary actions - high confidence predictions
        if (primaryActions.isNotEmpty()) {
            Text(
                text = getRoutineContextualTitle(routineType),
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(primaryActions) { action ->
                    ContextualActionCard(
                        action = action,
                        routineType = routineType,
                        isPrimary = true,
                        isUpdating = isUpdating
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Secondary actions - medium confidence predictions
        if (secondaryActions.isNotEmpty()) {
            Text(
                text = "Also consider",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(secondaryActions) { action ->
                    ContextualActionCard(
                        action = action,
                        routineType = routineType,
                        isPrimary = false,
                        isUpdating = isUpdating
                    )
                }
            }
        }
    }
}

/**
 * Individual contextual action card with routine-specific styling
 */
@Composable
private fun ContextualActionCard(
    action: ActionCard,
    routineType: RoutineType,
    isPrimary: Boolean,
    isUpdating: Boolean,
    modifier: Modifier = Modifier
) {
    val accentColor = getRoutineAccentColor(routineType)
    val cardColor = getRoutineCardColor(routineType)
    
    // Animate confidence-based scaling
    val confidenceScale by animateFloatAsState(
        targetValue = if (isPrimary) 1f + (action.confidence - 0.7f) * 0.3f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "confidence_scale"
    )
    
    // Animate update state
    val updateAlpha by animateFloatAsState(
        targetValue = if (isUpdating) 0.7f else 1f,
        animationSpec = tween(300),
        label = "update_alpha"
    )
    
    AnimatedVisibility(
        visible = true,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Card(
            modifier = modifier
                .width(if (isPrimary) 160.dp else 140.dp)
                .scale(confidenceScale)
                .alpha(updateAlpha),
            shape = RoundedCornerShape(
                when (routineType) {
                    RoutineType.MORNING -> 16.dp
                    RoutineType.AFTERNOON -> 12.dp
                    RoutineType.EVENING -> 20.dp
                    RoutineType.WEEKEND -> 18.dp
                    RoutineType.CUSTOM -> 16.dp
                }
            ),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isPrimary) 6.dp else 3.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Confidence indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = accentColor.copy(alpha = action.confidence),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${(action.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
                
                // Action description
                Text(
                    text = action.action,
                    style = if (isPrimary) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                    fontWeight = if (isPrimary) FontWeight.Medium else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Associated apps indicator
                if (action.apps.isNotEmpty()) {
                    Text(
                        text = "${action.apps.size} app${if (action.apps.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                // Routine-specific visual indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = action.confidence),
                                    accentColor.copy(alpha = action.confidence * 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }
        }
    }
}

/**
 * Enhanced contextual widgets that adapt to routine context
 */
@Composable
fun ContextualWidgets(
    widgets: List<WidgetConfig>,
    routineType: RoutineType,
    modifier: Modifier = Modifier
) {
    if (widgets.isEmpty()) return
    
    val accentColor = getRoutineAccentColor(routineType)
    val cardColor = getRoutineCardColor(routineType)
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        items(widgets.sortedBy { it.priority }) { widget ->
            ContextualWidget(
                widget = widget,
                routineType = routineType,
                accentColor = accentColor,
                cardColor = cardColor
            )
        }
    }
}

/**
 * Individual contextual widget with routine-specific behavior
 */
@Composable
private fun ContextualWidget(
    widget: WidgetConfig,
    routineType: RoutineType,
    accentColor: Color,
    cardColor: Color,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = widget.isVisible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Card(
            modifier = modifier.width(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = getWidgetTitle(widget.type, routineType),
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = getWidgetContent(widget.type, routineType),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Get routine-specific contextual title for predictions
 */
private fun getRoutineContextualTitle(routineType: RoutineType): String {
    return when (routineType) {
        RoutineType.MORNING -> "Start your morning with"
        RoutineType.AFTERNOON -> "Focus on productivity"
        RoutineType.EVENING -> "Wind down with"
        RoutineType.WEEKEND -> "Enjoy your weekend"
        RoutineType.CUSTOM -> "Suggested actions"
    }
}

/**
 * Get widget title based on type and routine
 */
private fun getWidgetTitle(type: WidgetType, routineType: RoutineType): String {
    return when (type) {
        WidgetType.TIME -> when (routineType) {
            RoutineType.MORNING -> "Good Morning"
            RoutineType.AFTERNOON -> "Afternoon"
            RoutineType.EVENING -> "Evening"
            RoutineType.WEEKEND -> "Weekend"
            RoutineType.CUSTOM -> "Current Time"
        }
        WidgetType.WELLNESS -> when (routineType) {
            RoutineType.MORNING -> "Mindfulness"
            RoutineType.EVENING -> "Relaxation"
            else -> "Wellness"
        }
        WidgetType.PRODUCTIVITY -> "Focus Time"
        WidgetType.SCHEDULE -> "Schedule"
        WidgetType.NOTIFICATIONS -> "Updates"
    }
}

/**
 * Get widget content based on type and routine
 */
private fun getWidgetContent(type: WidgetType, routineType: RoutineType): String {
    return when (type) {
        WidgetType.TIME -> java.time.LocalTime.now().toString().substring(0, 5)
        WidgetType.WELLNESS -> when (routineType) {
            RoutineType.MORNING -> "10 min meditation"
            RoutineType.EVENING -> "Breathing exercise"
            else -> "Take a break"
        }
        WidgetType.PRODUCTIVITY -> when (routineType) {
            RoutineType.AFTERNOON -> "Deep work mode"
            else -> "Stay focused"
        }
        WidgetType.SCHEDULE -> "3 events today"
        WidgetType.NOTIFICATIONS -> "2 new messages"
    }
}