package com.meticha.jetpackboilerplate.launcher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.data.mock.ActionPrediction
import com.meticha.jetpackboilerplate.launcher.data.mock.MockDataProvider
import com.meticha.jetpackboilerplate.ui.theme.CallBudyTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Contextual widgets that adapt based on current routine and predictions
 */
@Composable
fun ContextualWidgets(
    routineType: RoutineType,
    predictions: List<ActionPrediction>,
    onWidgetClick: (WidgetType) -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val widgets = remember(routineType, predictions) {
        generateContextualWidgets(routineType, predictions)
    }

    AnimatedVisibility(
        visible = isVisible && widgets.isNotEmpty(),
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(400)
        ) + fadeIn(animationSpec = tween(400)),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            items(
                items = widgets,
                key = { widget -> widget.type.name }
            ) { widget ->
                ContextualWidget(
                    widget = widget,
                    routineType = routineType,
                    onClick = { onWidgetClick(widget.type) }
                )
            }
        }
    }
}

/**
 * Individual contextual widget
 */
@Composable
private fun ContextualWidget(
    widget: WidgetData,
    routineType: RoutineType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = getRoutineCardColor(routineType)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Widget icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(getRoutineIconColor(routineType)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = widget.icon,
                    contentDescription = widget.title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Widget title
            Text(
                text = widget.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Widget content
            Text(
                text = widget.content,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Generate contextual widgets based on routine and predictions
 */
private fun generateContextualWidgets(
    routineType: RoutineType,
    predictions: List<ActionPrediction>
): List<WidgetData> {
    val currentTime = LocalTime.now()
    val timeString = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    
    return when (routineType) {
        RoutineType.MORNING -> listOf(
            WidgetData(
                type = WidgetType.TIME,
                title = "Good Morning",
                content = timeString,
                icon = Icons.Default.WbSunny
            ),
            WidgetData(
                type = WidgetType.WELLNESS,
                title = "Wellness",
                content = "Start with meditation",
                icon = Icons.Default.Favorite
            ),
            WidgetData(
                type = WidgetType.SCHEDULE,
                title = "Today's Plan",
                content = "3 meetings scheduled",
                icon = Icons.Default.Schedule
            )
        )
        
        RoutineType.AFTERNOON -> listOf(
            WidgetData(
                type = WidgetType.PRODUCTIVITY,
                title = "Focus Time",
                content = "Deep work session",
                icon = Icons.Default.TrendingUp
            ),
            WidgetData(
                type = WidgetType.TIME,
                title = "Afternoon",
                content = timeString,
                icon = Icons.Default.AccessTime
            ),
            WidgetData(
                type = WidgetType.NOTIFICATIONS,
                title = "Updates",
                content = "${predictions.size} suggestions",
                icon = Icons.Default.Notifications
            )
        )
        
        RoutineType.EVENING -> listOf(
            WidgetData(
                type = WidgetType.TIME,
                title = "Evening",
                content = timeString,
                icon = Icons.Default.AccessTime
            ),
            WidgetData(
                type = WidgetType.WELLNESS,
                title = "Unwind",
                content = "Time to relax",
                icon = Icons.Default.Favorite
            ),
            WidgetData(
                type = WidgetType.SCHEDULE,
                title = "Tomorrow",
                content = "Prep for tomorrow",
                icon = Icons.Default.Schedule
            )
        )
        
        RoutineType.WEEKEND -> listOf(
            WidgetData(
                type = WidgetType.TIME,
                title = "Weekend",
                content = timeString,
                icon = Icons.Default.WbSunny
            ),
            WidgetData(
                type = WidgetType.WELLNESS,
                title = "Self Care",
                content = "Personal time",
                icon = Icons.Default.Favorite
            ),
            WidgetData(
                type = WidgetType.PRODUCTIVITY,
                title = "Hobbies",
                content = "Creative projects",
                icon = Icons.Default.TrendingUp
            )
        )
        
        RoutineType.CUSTOM -> listOf(
            WidgetData(
                type = WidgetType.TIME,
                title = "Current Time",
                content = timeString,
                icon = Icons.Default.AccessTime
            ),
            WidgetData(
                type = WidgetType.NOTIFICATIONS,
                title = "Suggestions",
                content = "${predictions.size} available",
                icon = Icons.Default.Notifications
            )
        )
    }
}

/**
 * Widget data class
 */
data class WidgetData(
    val type: WidgetType,
    val title: String,
    val content: String,
    val icon: ImageVector
)

/**
 * Widget types for different contexts
 */
enum class WidgetType {
    TIME,
    WELLNESS,
    PRODUCTIVITY,
    SCHEDULE,
    NOTIFICATIONS
}

/**
 * Get routine-specific card background color
 */
@Composable
private fun getRoutineCardColor(routineType: RoutineType): Color {
    return when (routineType) {
        RoutineType.MORNING -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
        RoutineType.AFTERNOON -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.08f)
        RoutineType.EVENING -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f)
        RoutineType.WEEKEND -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)
        RoutineType.CUSTOM -> MaterialTheme.colorScheme.surface
    }
}

/**
 * Get routine-specific icon background color
 */
@Composable
private fun getRoutineIconColor(routineType: RoutineType): Color {
    return when (routineType) {
        RoutineType.MORNING -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        RoutineType.AFTERNOON -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
        RoutineType.EVENING -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
        RoutineType.WEEKEND -> MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
        RoutineType.CUSTOM -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }
}

@Preview(showBackground = true)
@Composable
fun ContextualWidgetsPreview() {
    CallBudyTheme {
        ContextualWidgets(
            routineType = RoutineType.MORNING,
            predictions = MockDataProvider.getMockActionPredictions(RoutineType.MORNING),
            onWidgetClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContextualWidgetsAfternoonPreview() {
    CallBudyTheme {
        ContextualWidgets(
            routineType = RoutineType.AFTERNOON,
            predictions = MockDataProvider.getMockActionPredictions(RoutineType.AFTERNOON),
            onWidgetClick = {}
        )
    }
}