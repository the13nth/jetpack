package com.meticha.jetpackboilerplate.launcher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meticha.jetpackboilerplate.launcher.data.models.AppInfo
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.data.mock.ActionPrediction
import com.meticha.jetpackboilerplate.launcher.data.mock.MockDataProvider
import com.meticha.jetpackboilerplate.ui.theme.CallBudyTheme

/**
 * Displays prediction-based app recommendations with confidence indicators
 */
@Composable
fun PredictionRecommendationCards(
    predictions: List<ActionPrediction>,
    routineType: RoutineType,
    onAppClick: (AppInfo) -> Unit,
    onQuickActionClick: (ActionPrediction) -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    AnimatedVisibility(
        visible = isVisible && predictions.isNotEmpty(),
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(400)
        ) + fadeIn(animationSpec = tween(400)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Section header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Predictions",
                    tint = getRoutineAccentColor(routineType),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Suggested for you",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Prediction cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = predictions.take(4), // Show top 4 predictions
                    key = { prediction -> prediction.action.hashCode() }
                ) { prediction ->
                    PredictionCard(
                        prediction = prediction,
                        routineType = routineType,
                        onAppClick = onAppClick,
                        onQuickActionClick = { onQuickActionClick(prediction) }
                    )
                }
            }
        }
    }
}

/**
 * Individual prediction card with confidence indicator and quick actions
 */
@Composable
private fun PredictionCard(
    prediction: ActionPrediction,
    routineType: RoutineType,
    onAppClick: (AppInfo) -> Unit,
    onQuickActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val confidenceAlpha by animateFloatAsState(
        targetValue = prediction.confidence,
        animationSpec = tween(500),
        label = "confidence_alpha"
    )

    Card(
        modifier = modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = getRoutineCardColor(routineType)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Action title and confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = prediction.action,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                ConfidenceIndicator(
                    confidence = prediction.confidence,
                    routineType = routineType,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reasoning text
            Text(
                text = prediction.reasoning,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Associated apps
            if (prediction.associatedApps.isNotEmpty()) {
                Text(
                    text = "Apps:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = prediction.associatedApps.take(3),
                        key = { app -> app.packageName }
                    ) { app ->
                        AppChip(
                            app = app,
                            routineType = routineType,
                            onClick = { onAppClick(app) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick action button
            OutlinedButton(
                onClick = onQuickActionClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Quick action",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Quick Action",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

/**
 * Confidence indicator showing prediction accuracy
 */
@Composable
private fun ConfidenceIndicator(
    confidence: Float,
    routineType: RoutineType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "${(confidence * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            ),
            color = getConfidenceColor(confidence)
        )
        
        LinearProgressIndicator(
            progress = { confidence },
            modifier = Modifier
                .width(40.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = getConfidenceColor(confidence),
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

/**
 * Small app chip for quick app access
 */
@Composable
private fun AppChip(
    app: AppInfo,
    routineType: RoutineType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = getRoutineIconColor(routineType).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(getRoutineIconColor(routineType)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = app.appName,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = app.appName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Get confidence-based color
 */
@Composable
private fun getConfidenceColor(confidence: Float): Color {
    return when {
        confidence >= 0.8f -> MaterialTheme.colorScheme.primary
        confidence >= 0.6f -> MaterialTheme.colorScheme.secondary
        confidence >= 0.4f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
}

/**
 * Get routine-specific card background color
 */
@Composable
private fun getRoutineCardColor(routineType: RoutineType): Color {
    return when (routineType) {
        RoutineType.MORNING -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
        RoutineType.AFTERNOON -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.05f)
        RoutineType.EVENING -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.05f)
        RoutineType.WEEKEND -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
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
 * Get routine-specific accent color
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
fun PredictionRecommendationCardsPreview() {
    CallBudyTheme {
        PredictionRecommendationCards(
            predictions = MockDataProvider.getMockActionPredictions(RoutineType.MORNING),
            routineType = RoutineType.MORNING,
            onAppClick = {},
            onQuickActionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PredictionCardPreview() {
    CallBudyTheme {
        PredictionCard(
            prediction = MockDataProvider.getMockActionPredictions(RoutineType.AFTERNOON).first(),
            routineType = RoutineType.AFTERNOON,
            onAppClick = {},
            onQuickActionClick = {}
        )
    }
}