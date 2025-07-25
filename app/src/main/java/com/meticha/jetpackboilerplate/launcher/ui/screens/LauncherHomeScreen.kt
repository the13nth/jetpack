package com.meticha.jetpackboilerplate.launcher.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.content.Intent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.ui.LauncherViewModel
import com.meticha.jetpackboilerplate.launcher.ui.components.AIInsight
import com.meticha.jetpackboilerplate.launcher.ui.components.PredictionGrid
import com.meticha.jetpackboilerplate.launcher.ui.components.QuickActionGrid
import com.meticha.jetpackboilerplate.launcher.ui.components.StatusBar
import com.meticha.jetpackboilerplate.launcher.ui.theme.RoutineTheme
import com.meticha.jetpackboilerplate.ui.theme.CallBudyTheme
import com.meticha.jetpackboilerplate.launcher.utils.PersistenceManager
import kotlinx.serialization.Serializable

@Composable
fun LauncherHomeScreen(
    onNavigateToRoutineSelection: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDocumentAnalysis: () -> Unit,
    onNavigateToPredictionDetail: (PredictionData) -> Unit,
    onNavigateToCreateRoutine: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentRoutine = uiState.currentRoutine
    var selectedRoutineType by remember { mutableStateOf(currentRoutine?.type ?: RoutineType.MORNING) }
    var showAppPicker by remember { mutableStateOf(false) }
    var selectedApps by remember { mutableStateOf(mutableMapOf<RoutineType, List<QuickActionData>>()) }
    var isLoadingApps by remember { mutableStateOf(true) }
    
    val context = LocalContext.current
    val persistenceManager = remember { PersistenceManager(context) }
    
    // Load saved apps for all routines on initialization
    LaunchedEffect(Unit) {
        val loadedApps = mutableMapOf<RoutineType, List<QuickActionData>>()
        RoutineType.values().forEach { routineType ->
            loadedApps[routineType] = persistenceManager.loadAppsForRoutine(routineType)
        }
        selectedApps = loadedApps
        isLoadingApps = false
        android.util.Log.d("LauncherHomeScreen", "Loaded saved apps: ${loadedApps.values.sumOf { it.size }} total apps")
    }
    
    // Save apps when routine type changes (in case there were any unsaved changes)
    LaunchedEffect(selectedRoutineType) {
        val currentApps = selectedApps[selectedRoutineType] ?: emptyList()
        persistenceManager.saveAppsForRoutine(selectedRoutineType, currentApps)
    }

    LauncherHomeContent(
        routineType = selectedRoutineType,
        routineName = getRoutineName(selectedRoutineType),
        onNavigateToRoutineSelection = onNavigateToRoutineSelection,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToDocumentAnalysis = onNavigateToDocumentAnalysis,
        onNavigateToPredictionDetail = onNavigateToPredictionDetail,
        onNavigateToCreateRoutine = onNavigateToCreateRoutine,
        onRoutineTypeChanged = { newRoutineType ->
            selectedRoutineType = newRoutineType
        },
        onAddAppClick = {
            showAppPicker = true
        },
        onActionLongClick = { action ->
            // Remove the app from the routine
            val currentApps = selectedApps[selectedRoutineType] ?: emptyList()
            val updatedApps = currentApps.filter { it != action }
            val updatedSelectedApps = selectedApps.toMutableMap().apply {
                put(selectedRoutineType, updatedApps)
            }
            selectedApps = updatedSelectedApps
            
            // Save the updated apps to persistent storage
            persistenceManager.saveAppsForRoutine(selectedRoutineType, updatedApps)
            
            Toast.makeText(context, "Removed ${action.label} from routine", Toast.LENGTH_SHORT).show()
        },
        selectedApps = selectedApps[selectedRoutineType] ?: emptyList(),
        isLoadingApps = isLoadingApps,
        modifier = modifier
    )
    
    // App Picker Dialog
    if (showAppPicker) {
        AppPickerDialog(
            onDismiss = { showAppPicker = false },
                                    onAppSelected = { appInfo ->
                            // Add selected app to the current routine
                            val currentApps = selectedApps[selectedRoutineType] ?: emptyList()
                            val newApp = QuickActionData(appInfo.icon, appInfo.appName, appInfo.packageName)
                            val updatedApps = currentApps + newApp
                            val updatedSelectedApps = selectedApps.toMutableMap().apply {
                                put(selectedRoutineType, updatedApps)
                            }
                            selectedApps = updatedSelectedApps
                            
                            // Save the updated apps to persistent storage
                            persistenceManager.saveAppsForRoutine(selectedRoutineType, updatedApps)
                            
                            showAppPicker = false
                        }
        )
    }
}

@Composable
private fun LauncherHomeContent(
    routineType: RoutineType,
    routineName: String,
    onNavigateToRoutineSelection: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDocumentAnalysis: () -> Unit,
    onNavigateToPredictionDetail: (PredictionData) -> Unit,
    onNavigateToCreateRoutine: () -> Unit,
    onRoutineTypeChanged: (RoutineType) -> Unit,
    onAddAppClick: () -> Unit,
    onActionLongClick: (QuickActionData) -> Unit,
    selectedApps: List<QuickActionData>,
    isLoadingApps: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Apply routine-specific theme
    RoutineTheme(routineType = routineType) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(getRoutineGradient(routineType))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Status Bar
                StatusBar()
                
                                            // Header
                            Header(
                                routineType = routineType,
                                routineName = routineName,
                                onRoutineClick = onNavigateToRoutineSelection,
                                onRoutineTypeChanged = onRoutineTypeChanged,
                                onNavigateToCreateRoutine = onNavigateToCreateRoutine
                            )
                
                // Main Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // AI Predictions Section
                    item {
                        Text(
                            text = "✨ AI Predictions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    
                    // Prediction Grid
                    item {
                        Text(
                            text = "AI Predictions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    
                    item {
                        PredictionGrid(
                            predictions = getPredictionCards(routineType),
                            onPredictionClick = onNavigateToPredictionDetail
                        )
                    }
                    
                    // AI Insights
                    item {
                        AIInsight(
                            message = getAIInsight(routineType),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    // Quick Actions
                    item {
                        Text(
                            text = getQuickActionsTitle(routineType),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    
                    item {
                        val context = LocalContext.current
                        if (isLoadingApps) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            QuickActionGrid(
                                actions = selectedApps,
                                onActionClick = { action -> 
                                    // Launch the selected app if it has a package name
                                    action.packageName?.let { packageName ->
                                        try {
                                            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                                            if (intent != null) {
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(intent)
                                                // Show a brief toast notification
                                                Toast.makeText(context, "Launching ${action.label}...", Toast.LENGTH_SHORT).show()
                                            } else {
                                                android.util.Log.e("AppLauncher", "No launch intent found for package: $packageName")
                                                Toast.makeText(context, "Cannot launch ${action.label}", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            android.util.Log.e("AppLauncher", "Failed to launch app $packageName: ${e.message}")
                                            Toast.makeText(context, "Failed to launch ${action.label}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onActionLongClick = onActionLongClick,
                                onAddAppClick = onAddAppClick
                            )
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            
            
        }
    }
}

@Composable
private fun Header(
    routineType: RoutineType,
    routineName: String,
    onRoutineClick: () -> Unit,
    onRoutineTypeChanged: (RoutineType) -> Unit,
    onNavigateToCreateRoutine: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = getGreeting(routineType),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Box {
            Card(
                modifier = Modifier
                    .clickable { expanded = true }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = getRoutineIcon(routineType),
                        fontSize = 16.sp
                    )
                    Text(
                        text = routineName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                RoutineType.values().forEach { routine ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = getRoutineIcon(routine))
                                Column {
                                    Text(
                                        text = getRoutineName(routine),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = getRoutineDescription(routine),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                                                    onClick = {
                                if (routine == RoutineType.CUSTOM) {
                                    onNavigateToCreateRoutine()
                                } else {
                                    onRoutineTypeChanged(routine)
                                }
                                expanded = false
                            },
                        leadingIcon = {
                            if (routine == routineType) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Spacer(modifier = Modifier.width(24.dp))
                            }
                        }
                    )
                }
                

            }
        }
    }
}

@Composable
private fun NavigationButton(
    icon: String,
    onClick: () -> Unit,
    isActive: Boolean
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.6f,
        animationSpec = tween(300),
        label = "alpha"
    )
    
    Card(
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
            }
        ),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 16.sp,
                modifier = Modifier.alpha(animatedAlpha)
            )
        }
    }
}

// Helper functions
private fun getRoutineGradient(routineType: RoutineType): Brush {
    val colors = when (routineType) {
        RoutineType.MORNING -> listOf(Color(0xFF2A3142), Color(0xFF1A1F2E))
        RoutineType.AFTERNOON -> listOf(Color(0xFF2A3142), Color(0xFF1A1F2E))
        RoutineType.EVENING -> listOf(Color(0xFF2A3142), Color(0xFF1A1F2E))
        RoutineType.WEEKEND -> listOf(Color(0xFF2A3142), Color(0xFF1A1F2E))
        else -> listOf(Color(0xFF2A3142), Color(0xFF1A1F2E))
    }
    return Brush.verticalGradient(colors)
}

private fun getRoutineName(routineType: RoutineType): String {
    return when (routineType) {
        RoutineType.MORNING -> "Morning Routine"
        RoutineType.AFTERNOON -> "Afternoon Focus"
        RoutineType.EVENING -> "Evening Wind-down"
        RoutineType.WEEKEND -> "Weekend Energy"
        else -> "Custom Routine"
    }
}

private fun getRoutineDescription(routineType: RoutineType): String {
    return when (routineType) {
        RoutineType.MORNING -> "Start your day with energy and focus"
        RoutineType.AFTERNOON -> "Maintain productivity and creativity"
        RoutineType.EVENING -> "Wind down and prepare for rest"
        RoutineType.WEEKEND -> "Enjoy leisure and personal time"
        else -> "Customize your routine"
    }
}

private fun getGreeting(routineType: RoutineType): String {
    return when (routineType) {
        RoutineType.MORNING -> "Good morning, Peter!"
        RoutineType.AFTERNOON -> "Focus time, Peter"
        RoutineType.EVENING -> "Time to unwind, Peter"
        RoutineType.WEEKEND -> "Happy weekend, Peter!"
        else -> "Hello, Peter!"
    }
}

private fun getRoutineIcon(routineType: RoutineType): String {
    return when (routineType) {
        RoutineType.MORNING -> "☀️"
        RoutineType.AFTERNOON -> "💼"
        RoutineType.EVENING -> "🌙"
        RoutineType.WEEKEND -> "🌟"
        else -> "📅"
    }
}

private fun getQuickActionsTitle(routineType: RoutineType): String {
    return when (routineType) {
        RoutineType.MORNING -> "Quick Actions"
        RoutineType.AFTERNOON -> "Work Tools"
        RoutineType.EVENING -> "Evening Apps"
        RoutineType.WEEKEND -> "Leisure Apps"
        else -> "Quick Actions"
    }
}

private fun getAIInsight(routineType: RoutineType): String {
    return when (routineType) {
        RoutineType.MORNING -> "You're 73% more productive when you start with coffee and reading"
        RoutineType.AFTERNOON -> "You're 73% more productive when you take a 5-minute break every hour"
        RoutineType.EVENING -> "Digital sunset mode can improve your sleep quality by 23%"
        RoutineType.WEEKEND -> "You're 60% more likely to exercise when you plan outdoor activities"
        else -> "AI is learning your patterns to provide better suggestions"
    }
}

// Mock data functions (these would come from your data layer)
private fun getPredictionCards(routineType: RoutineType): List<PredictionData> {
    return when (routineType) {
        RoutineType.MORNING -> listOf(
            PredictionData(
                icon = "☕",
                title = "Start Coffee Routine",
                subtitle = "Usually begins at 9:45 AM",
                confidence = 94,
                isLearning = true,
                primaryAction = "Start Brewing",
                secondaryAction = "Not Now"
            ),
            PredictionData(
                icon = "📖",
                title = "Daily Reading",
                subtitle = "Chapter 3 - Project Management",
                confidence = 87,
                isLearning = false,
                primaryAction = "Open Book",
                secondaryAction = "Later"
            ),
            PredictionData(
                icon = "🏃",
                title = "Morning Jog",
                subtitle = "Park route - 25 mins",
                confidence = 72,
                isLearning = false,
                primaryAction = "Start Route",
                secondaryAction = "Skip Today"
            ),
            PredictionData(
                icon = "📧",
                title = "Check Email",
                subtitle = "3 important messages",
                confidence = 89,
                isLearning = false,
                primaryAction = "Open Mail",
                secondaryAction = "Quick View"
            )
        )
        RoutineType.AFTERNOON -> listOf(
            PredictionData(
                icon = "📊",
                title = "Project Review Meeting",
                subtitle = "Due in 30 minutes",
                confidence = 98,
                isLearning = false,
                primaryAction = "Join Meeting",
                secondaryAction = "Prep Notes"
            ),
            PredictionData(
                icon = "📝",
                title = "Design Mockups",
                subtitle = "Launcher UI - 2 hours remaining",
                confidence = 91,
                isLearning = false,
                primaryAction = "Open Figma",
                secondaryAction = "Review Brief"
            ),
            PredictionData(
                icon = "☕",
                title = "Afternoon Break",
                subtitle = "Time for a coffee break",
                confidence = 76,
                isLearning = false,
                primaryAction = "Take Break",
                secondaryAction = "Skip"
            ),
            PredictionData(
                icon = "📱",
                title = "Check Messages",
                subtitle = "5 unread notifications",
                confidence = 82,
                isLearning = false,
                primaryAction = "View All",
                secondaryAction = "Quick Reply"
            )
        )
        RoutineType.EVENING -> listOf(
            PredictionData(
                icon = "📱",
                title = "Digital Sunset",
                subtitle = "Reduce blue light exposure",
                confidence = 96,
                isLearning = false,
                primaryAction = "Enable Now",
                secondaryAction = "Schedule"
            ),
            PredictionData(
                icon = "🎵",
                title = "Meditation Session",
                subtitle = "10-minute guided meditation",
                confidence = 78,
                isLearning = false,
                primaryAction = "Start Session",
                secondaryAction = "Browse"
            ),
            PredictionData(
                icon = "📚",
                title = "Evening Reading",
                subtitle = "Fiction - \"The Midnight Library\"",
                confidence = 89,
                isLearning = false,
                primaryAction = "Continue Reading",
                secondaryAction = "Bookmark"
            ),
            PredictionData(
                icon = "🌙",
                title = "Sleep Preparation",
                subtitle = "Set alarm for tomorrow",
                confidence = 93,
                isLearning = false,
                primaryAction = "Set Alarm",
                secondaryAction = "Sleep Mode"
            )
        )
        RoutineType.WEEKEND -> listOf(
            PredictionData(
                icon = "🥾",
                title = "Hiking Trail",
                subtitle = "Mountain View Trail - 2.5 hours",
                confidence = 85,
                isLearning = false,
                primaryAction = "Get Directions",
                secondaryAction = "Check Weather"
            ),
            PredictionData(
                icon = "🍳",
                title = "Weekend Brunch",
                subtitle = "Try that new pancake recipe",
                confidence = 71,
                isLearning = false,
                primaryAction = "View Recipe",
                secondaryAction = "Shopping List"
            ),
            PredictionData(
                icon = "🎬",
                title = "Movie Night",
                subtitle = "New releases available",
                confidence = 68,
                isLearning = false,
                primaryAction = "Browse Movies",
                secondaryAction = "Watch Later"
            ),
            PredictionData(
                icon = "🧘",
                title = "Weekend Yoga",
                subtitle = "30-minute session",
                confidence = 74,
                isLearning = false,
                primaryAction = "Start Session",
                secondaryAction = "Schedule"
            )
        )
        else -> emptyList()
    }
}

private fun getQuickActions(routineType: RoutineType): List<QuickActionData> {
    // Return empty list to show add buttons instead of predefined actions
    return emptyList()
}

// Data classes for predictions and quick actions
@Serializable
data class PredictionData(
    val icon: String,
    val title: String,
    val subtitle: String,
    val confidence: Int,
    val isLearning: Boolean,
    val primaryAction: String,
    val secondaryAction: String
)

data class QuickActionData(
    val icon: String,
    val label: String,
    val packageName: String? = null
)

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: String
)

@Composable
fun AppPickerDialog(
    onDismiss: () -> Unit,
    onAppSelected: (AppInfo) -> Unit
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        try {
            android.util.Log.d("AppPicker", "Starting app detection...")
            
            // Method 1: Query launcher activities with basic flags
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            
            val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
            android.util.Log.d("AppPicker", "Found ${resolveInfoList.size} apps via launcher query")
            
            // Log first few apps for debugging
            resolveInfoList.take(5).forEach { resolveInfo ->
                android.util.Log.d("AppPicker", "Sample app: ${resolveInfo.activityInfo.packageName} - ${resolveInfo.loadLabel(packageManager)}")
            }
            
            val apps = if (resolveInfoList.isNotEmpty()) {
                resolveInfoList
                    .filter { 
                        val packageName = it.activityInfo.packageName
                        // Only exclude our own app for now
                        packageName != context.packageName
                    }
                    .map { resolveInfo ->
                        val appName = try {
                            resolveInfo.loadLabel(packageManager).toString()
                        } catch (e: Exception) {
                            resolveInfo.activityInfo.packageName
                        }
                        
                        val packageName = resolveInfo.activityInfo.packageName
                        
                        AppInfo(packageName, appName, "📱")
                    }
                    .sortedBy { it.appName }
                    .take(50) // Limit to first 50 apps for performance
            } else {
                // Method 2: Fallback - get all installed packages
                android.util.Log.d("AppPicker", "Trying fallback method")
                val packageInfoList = packageManager.getInstalledPackages(0)
                android.util.Log.d("AppPicker", "Fallback found ${packageInfoList.size} packages")
                
                packageInfoList
                    .filter { packageInfo ->
                        val packageName = packageInfo.packageName
                        packageName != context.packageName
                    }
                    .map { packageInfo ->
                        val appName = try {
                            packageInfo.applicationInfo?.loadLabel(packageManager)?.toString() ?: packageInfo.packageName
                        } catch (e: Exception) {
                            packageInfo.packageName
                        }
                        
                        AppInfo(packageInfo.packageName, appName, "📱")
                    }
                    .sortedBy { it.appName }
                    .take(50)
            }
            
            android.util.Log.d("AppPicker", "Final app count: ${apps.size}")
            installedApps = apps
            
        } catch (e: Exception) {
            android.util.Log.e("AppPicker", "Error loading apps: ${e.message}")
            errorMessage = "Failed to load apps: ${e.message}"
            installedApps = emptyList()
        }
    }
    
    // Set loading to false when apps are loaded
    LaunchedEffect(installedApps) {
        isLoading = false
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add App to Routine",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading apps",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                installedApps.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No apps found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = "Found ${installedApps.size} apps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        if (installedApps.isEmpty()) {
                            item {
                                Text(
                                    text = "Debug: No apps detected. Check logcat for details.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        items(installedApps) { app ->
                            AppListItem(
                                app = app,
                                onClick = { onAppSelected(app) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun AppListItem(
    app: AppInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = app.icon,
                fontSize = 24.sp
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LauncherHomeScreenPreview() {
    CallBudyTheme {
        LauncherHomeContent(
            routineType = RoutineType.MORNING,
            routineName = "Morning Routine",
            onNavigateToRoutineSelection = {},
            onNavigateToSettings = {},
            onNavigateToDocumentAnalysis = {},
            onNavigateToPredictionDetail = {},
            onNavigateToCreateRoutine = {},
            onRoutineTypeChanged = {},
            onAddAppClick = {},
            onActionLongClick = {},
            selectedApps = emptyList(),
            isLoadingApps = false
        )
    }
}
