package com.example.rotiie.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rotiie.launcher.data.models.Routine
import com.example.rotiie.launcher.data.models.RoutineType
import com.example.rotiie.launcher.data.mock.MockDataProvider
import com.example.rotiie.launcher.utils.IdGenerator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    onBackClick: () -> Unit,
    onRoutineCreated: (Routine) -> Unit,
    modifier: Modifier = Modifier
) {
    var routineName by remember { mutableStateOf(TextFieldValue("")) }
    var routineDescription by remember { mutableStateOf(TextFieldValue("")) }
    var selectedIcon by remember { mutableStateOf("📱") }
    var selectedColor by remember { mutableStateOf(Color(0xFF8B9DC3)) }
    var selectedApps by remember { mutableStateOf(setOf<String>()) }
    var showAppSelection by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val availableApps = remember { MockDataProvider.mockApps }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Custom Routine") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Routine Name
            item {
                Text(
                    text = "Routine Name",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    placeholder = { Text("Enter routine name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
            
            // Routine Description
            item {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = routineDescription,
                    onValueChange = { routineDescription = it },
                    placeholder = { Text("Describe your routine") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
            
            // Icon Selection
            item {
                Text(
                    text = "Choose Icon",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                IconSelectionGrid(
                    selectedIcon = selectedIcon,
                    onIconSelected = { selectedIcon = it }
                )
            }
            
            // Color Selection
            item {
                Text(
                    text = "Choose Color",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                ColorSelectionGrid(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )
            }
            
            // App Selection
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select apps for quick access",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showAppSelection = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Select Apps (${selectedApps.size} selected)")
                }
            }
            
            // Preview
            item {
                Text(
                    text = "Preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                RoutinePreview(
                    name = routineName.text,
                    description = routineDescription.text,
                    icon = selectedIcon,
                    color = selectedColor,
                    selectedApps = selectedApps
                )
            }
            
            // Create Button
            item {
                Button(
                    onClick = {
                        scope.launch {
                            val newRoutine = Routine(
                                id = IdGenerator.generateRoutineId(),
                                name = routineName.text.ifEmpty { "Custom Routine" },
                                type = RoutineType.CUSTOM,
                                startTime = "00:00",
                                endTime = "23:59",
                                isActive = true,
                                priority = 5
                            )
                            onRoutineCreated(newRoutine)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = routineName.text.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Create Routine",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    // App Selection Dialog
    if (showAppSelection) {
        AppSelectionDialog(
            availableApps = availableApps,
            selectedApps = selectedApps,
            onAppsSelected = { selectedApps = it },
            onDismiss = { showAppSelection = false }
        )
    }
}

@Composable
private fun IconSelectionGrid(
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    val icons = listOf("📱", "🎯", "⚡", "🌟", "💡", "🚀", "🎨", "📚", "🏃", "🧘", "🎵", "📝")
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icons.chunked(6).forEach { rowIcons ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowIcons.forEach { icon ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (selectedIcon == icon) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .border(
                                width = if (selectedIcon == icon) 2.dp else 0.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable { onIconSelected(icon) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = icon,
                            fontSize = 20.sp
                        )
                    }
                }
                // Fill remaining space if row has fewer than 6 items
                repeat(6 - rowIcons.size) {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
private fun ColorSelectionGrid(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color(0xFF8B9DC3), // Blue-gray
        Color(0xFF6B7280), // Gray
        Color(0xFF9CA3AF), // Light gray
        Color(0xFF4B5563), // Dark gray
        Color(0xFF374151), // Very dark gray
        Color(0xFF1F2937)  // Almost black
    )
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (selectedColor == color) 2.dp else 0.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) },
                contentAlignment = Alignment.Center
            ) {
                if (selectedColor == color) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RoutinePreview(
    name: String,
    description: String,
    icon: String,
    color: Color,
    selectedApps: Set<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 24.sp
                    )
                }
                
                Column {
                    Text(
                        text = name.ifEmpty { "Custom Routine" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            if (selectedApps.isNotEmpty()) {
                Text(
                    text = "Quick Actions: ${selectedApps.size} apps selected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AppSelectionDialog(
    availableApps: List<com.example.rotiie.launcher.data.models.AppInfo>,
    selectedApps: Set<String>,
    onAppsSelected: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Apps")
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableApps) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newSelection = if (selectedApps.contains(app.packageName)) {
                                    selectedApps - app.packageName
                                } else {
                                    selectedApps + app.packageName
                                }
                                onAppsSelected(newSelection)
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedApps.contains(app.packageName),
                            onCheckedChange = { checked ->
                                val newSelection = if (checked) {
                                    selectedApps + app.packageName
                                } else {
                                    selectedApps - app.packageName
                                }
                                onAppsSelected(newSelection)
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = app.appName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = app.category ?: "Unknown",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CreateRoutineScreenPreview() {
    MaterialTheme {
        CreateRoutineScreen(
            onBackClick = {},
            onRoutineCreated = {}
        )
    }
} 