package com.example.rotiie.launcher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rotiie.launcher.data.models.AppInfo
import com.example.rotiie.launcher.data.models.RoutineType
import com.example.rotiie.launcher.data.mock.MockDataProvider
import com.example.rotiie.ui.theme.CallBudyTheme

/**
 * Dynamic app grid that adapts based on routine and predictions
 * Implements priority-based positioning and smooth transitions
 */
@Composable
fun DynamicAppGrid(
    apps: List<AppInfo>,
    routineType: RoutineType,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    // Sort apps by priority based on routine context
    val sortedApps = apps.sortedWith(
        compareByDescending<AppInfo> { it.usageCount }
            .thenByDescending { it.lastUsed ?: 0L }
            .thenBy { it.appName }
    )

    // Determine grid columns based on routine type
    val columns = when (routineType) {
        RoutineType.MORNING -> 3 // Focused, fewer apps
        RoutineType.AFTERNOON -> 4 // More apps for work productivity
        RoutineType.EVENING -> 3 // Relaxed, fewer apps
        RoutineType.WEEKEND -> 4 // More leisure apps
        RoutineType.CUSTOM -> 3
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(animationSpec = tween(200)),
        modifier = modifier
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = sortedApps,
                key = { app -> app.packageName }
            ) { app ->
                AppGridItem(
                    app = app,
                    routineType = routineType,
                    onClick = { onAppClick(app) }
                )
            }
        }
    }
}

/**
 * Individual app item in the grid with routine-specific styling
 */
@Composable
private fun AppGridItem(
    app: AppInfo,
    routineType: RoutineType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Calculate priority-based visual emphasis
    val priority = calculateAppPriority(app, routineType)
    val scale by animateFloatAsState(
        targetValue = if (priority > 0.7f) 1.05f else 1.0f,
        animationSpec = tween(300),
        label = "app_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (priority > 0.3f) 1.0f else 0.7f,
        animationSpec = tween(300),
        label = "app_alpha"
    )

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .alpha(alpha)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = getRoutineCardColor(routineType)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (priority > 0.7f) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App icon placeholder (using material icon for now)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        getRoutineIconColor(routineType),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = app.appName,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // App name
            Text(
                text = app.appName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (priority > 0.7f) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Priority indicator for high-priority apps
            if (priority > 0.8f) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(getRoutineAccentColor(routineType))
                        .padding(top = 2.dp)
                )
            }
        }
    }
}

/**
 * Calculate app priority based on routine context and usage patterns
 */
private fun calculateAppPriority(app: AppInfo, routineType: RoutineType): Float {
    val baseUsagePriority = (app.usageCount / 500f).coerceAtMost(1f)
    val recencyPriority = app.lastUsed?.let { lastUsed ->
        val hoursSinceLastUse = (System.currentTimeMillis() - lastUsed) / (1000 * 60 * 60)
        (1f - (hoursSinceLastUse / 24f)).coerceAtLeast(0f)
    } ?: 0f

    // Routine-specific category bonuses
    val categoryBonus = when (routineType) {
        RoutineType.MORNING -> when (app.category) {
            "Health", "Productivity", "Email" -> 0.3f
            "News", "Weather" -> 0.2f
            else -> 0f
        }
        RoutineType.AFTERNOON -> when (app.category) {
            "Work", "Productivity", "Professional" -> 0.4f
            "Communication" -> 0.2f
            else -> 0f
        }
        RoutineType.EVENING -> when (app.category) {
            "Entertainment", "Social", "Reading" -> 0.3f
            "Music" -> 0.2f
            else -> 0f
        }
        RoutineType.WEEKEND -> when (app.category) {
            "Entertainment", "Social", "Fitness", "Reading" -> 0.3f
            "Games", "Photography" -> 0.2f
            else -> 0f
        }
        RoutineType.CUSTOM -> 0f
    }

    return ((baseUsagePriority * 0.4f) + (recencyPriority * 0.4f) + (categoryBonus * 0.2f))
        .coerceIn(0f, 1f)
}

/**
 * Get routine-specific card background color
 */
@Composable
private fun getRoutineCardColor(routineType: RoutineType): Color {
    return when (routineType) {
        RoutineType.MORNING -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        RoutineType.AFTERNOON -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
        RoutineType.EVENING -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)
        RoutineType.WEEKEND -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
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

/**
 * Get routine-specific accent color for priority indicators
 */
@Composable
private fun getRoutineAccentColor(routineType: RoutineType): Color {
    return when (routineType) {
        RoutineType.MORNING -> MaterialTheme.colorScheme.primary
        RoutineType.AFTERNOON -> MaterialTheme.colorScheme.secondary
        RoutineType.EVENING -> MaterialTheme.colorScheme.tertiary
        RoutineType.WEEKEND -> MaterialTheme.colorScheme.outline
        RoutineType.CUSTOM -> MaterialTheme.colorScheme.onSurface
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicAppGridPreview() {
    CallBudyTheme {
        DynamicAppGrid(
            apps = MockDataProvider.mockApps.take(12),
            routineType = RoutineType.MORNING,
            onAppClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicAppGridAfternoonPreview() {
    CallBudyTheme {
        DynamicAppGrid(
            apps = MockDataProvider.mockApps.take(16),
            routineType = RoutineType.AFTERNOON,
            onAppClick = {}
        )
    }
}