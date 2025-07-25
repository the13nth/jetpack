package com.meticha.jetpackboilerplate.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.meticha.jetpackboilerplate.launcher.ui.screens.PredictionData

@Composable
fun PredictionGrid(
    predictions: List<PredictionData>,
    onPredictionClick: (PredictionData) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Create rows of 2 predictions each
        predictions.chunked(2).forEach { rowPredictions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowPredictions.forEach { prediction ->
                    CompactPredictionCard(
                        prediction = prediction,
                        onClick = { onPredictionClick(prediction) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Add empty space if odd number of predictions
                if (rowPredictions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CompactPredictionCard(
    prediction: PredictionData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
            .then(
                if (prediction.isLearning) {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (prediction.isLearning) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
                            Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                Text(
                    text = prediction.icon,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title
            Text(
                text = prediction.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Confidence indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(getConfidenceColor(prediction.confidence))
                )
                Text(
                    text = "${prediction.confidence}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // View button
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "View",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun getConfidenceColor(confidence: Int): Color {
    return when {
        confidence >= 80 -> Color(0xFF10B981) // Green
        confidence >= 60 -> Color(0xFFF59E0B) // Orange
        else -> Color(0xFFEF4444) // Red
    }
}

@Preview(showBackground = true)
@Composable
fun PredictionGridPreview() {
    MaterialTheme {
        val samplePredictions = listOf(
            PredictionData("📧", "Email Check", "3 unread messages", 85, false, "Open", "Mark Read"),
            PredictionData("🎵", "Music", "Continue playlist", 72, true, "Play", "Skip"),
            PredictionData("☁️", "Weather", "Rain expected", 90, false, "View", "Dismiss"),
            PredictionData("📰", "News", "Breaking updates", 68, false, "Read", "Later")
        )
        
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            PredictionGrid(
                predictions = samplePredictions,
                onPredictionClick = {}
            )
        }
    }
} 