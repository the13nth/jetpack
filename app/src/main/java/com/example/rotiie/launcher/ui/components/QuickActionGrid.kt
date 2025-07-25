package com.example.rotiie.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rotiie.launcher.ui.screens.QuickActionData

@Composable
fun QuickActionGrid(
    actions: List<QuickActionData>,
    onActionClick: (QuickActionData) -> Unit,
    onActionLongClick: (QuickActionData) -> Unit = {},
    onAddAppClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // Create a list that includes actions plus add buttons to fill remaining slots
        val totalSlots = 4
        val remainingSlots = totalSlots - actions.size
        
        val displayItems = actions.toMutableList()
        repeat(remainingSlots) {
            displayItems.add(QuickActionData("", "", null)) // Placeholder for add button
        }
        
        displayItems.chunked(4).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { item ->
                    if (item.icon.isNotEmpty() && item.label.isNotEmpty()) {
                        // Show actual app
                        QuickActionButton(
                            action = item,
                            onClick = { onActionClick(item) },
                            onLongClick = { onActionLongClick(item) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Show add button
                        AddAppButton(
                            onClick = onAddAppClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            if (row != displayItems.chunked(4).last()) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AddAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add App",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Add App",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    action: QuickActionData,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = action.icon,
                fontSize = 20.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuickActionGridPreview() {
    MaterialTheme {
        QuickActionGrid(
            actions = listOf(
                QuickActionData("📧", "Mail", "com.android.email"),
                QuickActionData("🎵", "Music", "com.android.music"),
                QuickActionData("☁️", "Weather", "com.android.weather"),
                QuickActionData("📰", "News", "com.android.news")
            ),
            onActionClick = {},
            onActionLongClick = {},
            onAddAppClick = {}
        )
    }
} 

@Preview(showBackground = true)
@Composable
fun AddAppButtonPreview() {
    MaterialTheme {
        AddAppButton(onClick = {})
    }
}