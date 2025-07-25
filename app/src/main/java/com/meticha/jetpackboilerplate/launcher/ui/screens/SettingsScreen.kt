package com.meticha.jetpackboilerplate.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meticha.jetpackboilerplate.launcher.ui.LauncherViewModel
import com.meticha.jetpackboilerplate.launcher.ui.components.StatusBar
import com.meticha.jetpackboilerplate.ui.theme.CallBudyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Routine Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        SettingsContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar
            StatusBar()
            
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Routine Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Configure AI",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242)
                        )
                    }
                }
            }
            
            // Settings Sections
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Intentions & Goals Section
                item {
                    SettingsSection(
                        title = "🎯 Intentions & Goals",
                        items = listOf(
                            SettingsItem(
                                title = "Morning Productivity",
                                subtitle = "Focus on health and learning",
                                action = SettingsAction.Button("Edit")
                            ),
                            SettingsItem(
                                title = "Work Focus",
                                subtitle = "Deep work and collaboration",
                                action = SettingsAction.Button("Edit")
                            ),
                            SettingsItem(
                                title = "Evening Relaxation",
                                subtitle = "Mindfulness and rest",
                                action = SettingsAction.Button("Edit")
                            )
                        )
                    )
                }
                
                // AI Behavior Section
                item {
                    SettingsSection(
                        title = "🤖 AI Behavior",
                        items = listOf(
                            SettingsItem(
                                title = "Prediction Confidence",
                                subtitle = "Show predictions above 70%",
                                action = SettingsAction.Toggle(true)
                            ),
                            SettingsItem(
                                title = "Learning Mode",
                                subtitle = "Actively learn from behavior",
                                action = SettingsAction.Toggle(true)
                            ),
                            SettingsItem(
                                title = "Gentle Nudges",
                                subtitle = "Remind about intentions",
                                action = SettingsAction.Toggle(false)
                            )
                        )
                    )
                }
                
                // Privacy & Data Section
                item {
                    SettingsSection(
                        title = "🛡️ Privacy & Data",
                        items = listOf(
                            SettingsItem(
                                title = "Local Processing",
                                subtitle = "All AI runs on device",
                                action = SettingsAction.Status("✓ Active", Color(0xFF10B981))
                            ),
                            SettingsItem(
                                title = "Data Export",
                                subtitle = "Export your patterns",
                                action = SettingsAction.Button("Export")
                            ),
                            SettingsItem(
                                title = "Reset Learning",
                                subtitle = "Clear AI knowledge",
                                action = SettingsAction.Button("Reset", Color(0xFFDC2626))
                            )
                        )
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    items: List<SettingsItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            items.forEach { item ->
                SettingsItemRow(item = item)
                if (item != items.last()) {
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF0F0F0)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsItemRow(
    item: SettingsItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
        }
        
        when (val action = item.action) {
            is SettingsAction.Button -> {
                Button(
                    onClick = { /* Handle button click */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = action.color ?: MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = action.text,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (action.color != null) Color.White else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            is SettingsAction.Toggle -> {
                var isOn by remember { mutableStateOf(action.initialValue) }
                Switch(
                    checked = isOn,
                    onCheckedChange = { isOn = it }
                )
            }
            is SettingsAction.Status -> {
                Text(
                    text = action.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = action.color
                )
            }
        }
    }
}

// Data classes
data class SettingsItem(
    val title: String,
    val subtitle: String,
    val action: SettingsAction
)

sealed class SettingsAction {
    data class Button(val text: String, val color: Color? = null) : SettingsAction()
    data class Toggle(val initialValue: Boolean) : SettingsAction()
    data class Status(val text: String, val color: Color) : SettingsAction()
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    CallBudyTheme {
        SettingsContent()
    }
} 