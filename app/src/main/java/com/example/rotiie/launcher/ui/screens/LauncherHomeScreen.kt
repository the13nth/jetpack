package com.example.rotiie.launcher.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.os.VibrationEffect
import android.os.Build
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rotiie.launcher.data.models.RoutineType
import com.example.rotiie.launcher.ui.LauncherViewModel
import com.example.rotiie.launcher.ui.components.AIInsight
import com.example.rotiie.launcher.ui.components.PredictionGrid
import com.example.rotiie.launcher.ui.components.QuickActionGrid
import com.example.rotiie.launcher.ui.components.StatusBar
import com.example.rotiie.launcher.ui.theme.RoutineTheme
import com.example.rotiie.ui.theme.CallBudyTheme
import com.example.rotiie.launcher.utils.PersistenceManager
import kotlinx.serialization.Serializable
import com.example.rotiie.launcher.ui.screens.SettingsViewModel

@Composable
fun LauncherHomeScreen(
    onNavigateToRoutineSelection: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDocumentAnalysis: () -> Unit,
    onNavigateToPredictionDetail: (PredictionData) -> Unit,
    onNavigateToCreateRoutine: () -> Unit,
    onNavigateToBrain: () -> Unit,
    onNavigateToPromptefy: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentRoutine = uiState.currentRoutine
    var selectedRoutineType by remember { mutableStateOf(currentRoutine?.type ?: RoutineType.MORNING) }
    var showAppPicker by remember { mutableStateOf(false) }
    var selectedApps by remember { mutableStateOf(mutableMapOf<RoutineType, List<QuickActionData>>()) }
    var isLoadingApps by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    
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

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Main Content
        AnimatedVisibility(
            visible = !showSettings,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            LauncherHomeContent(
                routineType = selectedRoutineType,
                routineName = getRoutineName(selectedRoutineType),
                onNavigateToRoutineSelection = onNavigateToRoutineSelection,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToDocumentAnalysis = onNavigateToDocumentAnalysis,
                onNavigateToPredictionDetail = onNavigateToPredictionDetail,
                onNavigateToCreateRoutine = onNavigateToCreateRoutine,
                onNavigateToBrain = onNavigateToBrain,
                onNavigateToPromptefy = onNavigateToPromptefy,
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
                onLongPress = { showSettings = true },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Settings Page
        AnimatedVisibility(
            visible = showSettings,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        ) {
            SettingsPage(
                onBackClick = { showSettings = false },
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToBrain = onNavigateToBrain,
                onNavigateToDocumentAnalysis = onNavigateToDocumentAnalysis,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    
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
    onNavigateToBrain: () -> Unit,
    onNavigateToPromptefy: () -> Unit,
    onRoutineTypeChanged: (RoutineType) -> Unit,
    onAddAppClick: () -> Unit,
    onActionLongClick: (QuickActionData) -> Unit,
    selectedApps: List<QuickActionData>,
    isLoadingApps: Boolean = false,
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    
    // Update time and date every minute
    LaunchedEffect(Unit) {
        while (true) {
            val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val dateFormat = java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.getDefault())
            currentTime = timeFormat.format(java.util.Date())
            currentDate = dateFormat.format(java.util.Date())
            kotlinx.coroutines.delay(60000) // Update every minute
        }
    }
    
    // Apply routine-specific theme
    RoutineTheme(routineType = routineType) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(getRoutineGradient(routineType))
                .combinedClickable(
                    onClick = { /* Regular click - do nothing */ },
                    onLongClick = { 
                        android.util.Log.d("LauncherHomeScreen", "Long press detected via combinedClickable!")
                        // Haptic feedback
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                val vibratorManager = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                                val vibrator = vibratorManager.defaultVibrator
                                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                @Suppress("DEPRECATION")
                                val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
                                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                @Suppress("DEPRECATION")
                                val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
                                vibrator.vibrate(50)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("LauncherHomeScreen", "Vibration failed: ${e.message}")
                        }
                        // Show brief feedback
                        Toast.makeText(context, "Opening settings...", Toast.LENGTH_SHORT).show()
                        onLongPress() 
                    }
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Status Bar with Settings Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time and status indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentTime,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "📶",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "🔋",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Brain button
                    IconButton(
                        onClick = { 
                            android.util.Log.d("LauncherHomeScreen", "Brain button clicked!")
                            onNavigateToBrain() 
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Brain",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Header
                Header(
                    routineType = routineType,
                    routineName = routineName,
                    currentTime = currentTime,
                    currentDate = currentDate,
                    onRoutineClick = onNavigateToRoutineSelection,
                    onRoutineTypeChanged = onRoutineTypeChanged,
                    onNavigateToCreateRoutine = onNavigateToCreateRoutine,
                    onNavigateToBrain = onNavigateToBrain,
                    onNavigateToPromptefy = onNavigateToPromptefy
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
private fun SettingsPage(
    onBackClick: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToBrain: () -> Unit,
    onNavigateToDocumentAnalysis: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLoginDialog by remember { mutableStateOf(false) }
    var showSignUpDialog by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(getRoutineGradient(RoutineType.MORNING))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Settings content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Account Section
                item {
                    SettingsSection(
                        title = "👤 Account",
                        items = if (settingsUiState.isLoggedIn) {
                            listOf(
                                SwipeSettingsItem(
                                    icon = Icons.Default.Person,
                                    title = "Signed In",
                                    subtitle = settingsUiState.currentUser?.email ?: "User",
                                    onClick = { /* Show user info */ }
                                ),
                                SwipeSettingsItem(
                                    icon = Icons.Default.Logout,
                                    title = "Sign Out",
                                    subtitle = "Sign out of your account",
                                    onClick = { settingsViewModel.signOut() }
                                )
                            )
                        } else {
                            listOf(
                                SwipeSettingsItem(
                                    icon = Icons.Default.Person,
                                    title = "Sign In",
                                    subtitle = "Sign in to sync your data",
                                    onClick = { showLoginDialog = true }
                                ),
                                SwipeSettingsItem(
                                    icon = Icons.Default.PersonAdd,
                                    title = "Create Account",
                                    subtitle = "Create a new account",
                                    onClick = { showSignUpDialog = true }
                                )
                            )
                        }
                    )
                }
                
                // AI Behavior Section
                item {
                    SettingsSection(
                        title = "🤖 AI Behavior",
                        items = listOf(
                            SwipeSettingsItem(
                                icon = Icons.Default.Psychology,
                                title = "Prediction Confidence",
                                subtitle = "Show predictions above 70%",
                                onClick = { /* TODO: Toggle */ }
                            ),
                            SwipeSettingsItem(
                                icon = Icons.Default.School,
                                title = "Learning Mode",
                                subtitle = "Actively learn from behavior",
                                onClick = { /* TODO: Toggle */ }
                            ),
                            SwipeSettingsItem(
                                icon = Icons.Default.Notifications,
                                title = "Gentle Nudges",
                                subtitle = "Remind about intentions",
                                onClick = { /* TODO: Toggle */ }
                            )
                        )
                    )
                }
                
                // Routine Management Section
                item {
                    SettingsSection(
                        title = "📅 Routine Management",
                        items = listOf(
                            SwipeSettingsItem(
                                icon = Icons.Default.Schedule,
                                title = "Routine Selection",
                                subtitle = "Choose your daily routines",
                                onClick = onNavigateToSettings
                            ),
                            SwipeSettingsItem(
                                icon = Icons.Default.Add,
                                title = "Create Custom Routine",
                                subtitle = "Build your own routine",
                                onClick = { /* TODO */ }
                            )
                        )
                    )
                }
                
                // AI & Predictions Section
                item {
                    SettingsSection(
                        title = "🧠 AI & Predictions",
                        items = listOf(
                            SwipeSettingsItem(
                                icon = Icons.Default.Psychology,
                                title = "Brain Settings",
                                subtitle = "Configure AI behavior",
                                onClick = onNavigateToBrain
                            ),
                            SwipeSettingsItem(
                                icon = Icons.Default.Description,
                                title = "Document Analysis",
                                subtitle = "Manage document insights",
                                onClick = onNavigateToDocumentAnalysis
                            )
                        )
                    )
                }
                
                // App Management Section
                item {
                    SettingsSection(
                        title = "📱 App Management",
                        items = listOf(
                            SwipeSettingsItem(
                                icon = Icons.Default.Apps,
                                title = "Quick Actions",
                                subtitle = "Manage routine apps",
                                onClick = { /* TODO */ }
                            ),
                            SwipeSettingsItem(
                                icon = Icons.Default.Notifications,
                                title = "Notifications",
                                subtitle = "Configure alerts",
                                onClick = { /* TODO */ }
                            )
                        )
                    )
                }
                
                // Privacy & Data Section
                item {
                    SettingsSection(
                        title = "🔒 Privacy & Data",
                        items = listOf(
                            SwipeSettingsItem(
                                icon = Icons.Default.Security,
                                title = "Local Processing",
                                subtitle = "All AI runs on device",
                                onClick = { /* TODO */ }
                            ),
                            SwipeSettingsItem(
                                icon = Icons.Default.Download,
                                title = "Data Export",
                                subtitle = "Export your patterns",
                                onClick = { /* TODO */ }
                            )
                        )
                    )
                }
                
                // System Section
                item {
                    SettingsSection(
                        title = "⚙️ System",
                        items = listOf(
                            SwipeSettingsItem(
                                icon = Icons.Default.Info,
                                title = "About",
                                subtitle = "App version and info",
                                onClick = { /* TODO */ }
                            ),
                            SwipeSettingsItem(
                                icon = Icons.AutoMirrored.Filled.Help,
                                title = "Help & Support",
                                subtitle = "Get assistance",
                                onClick = { /* TODO */ }
                            )
                        )
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
        
        // Show error message if any
        if (settingsUiState.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { settingsViewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(settingsUiState.error!!)
            }
        }
        
        // Show success message if any
        if (settingsUiState.message != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { settingsViewModel.clearMessage() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(settingsUiState.message!!)
            }
        }
    }
    
    // Login Dialog
    if (showLoginDialog) {
        LoginDialog(
            isLoading = settingsUiState.isLoading,
            onDismiss = { showLoginDialog = false },
            onSignIn = { email, password ->
                settingsViewModel.signIn(email, password)
                showLoginDialog = false
            },
            onForgotPassword = {
                showLoginDialog = false
                showResetPasswordDialog = true
            }
        )
    }
    
    // Sign Up Dialog
    if (showSignUpDialog) {
        SignUpDialog(
            isLoading = settingsUiState.isLoading,
            onDismiss = { showSignUpDialog = false },
            onSignUp = { email, password, displayName ->
                settingsViewModel.signUp(email, password, displayName)
                showSignUpDialog = false
            }
        )
    }
    
    // Reset Password Dialog
    if (showResetPasswordDialog) {
        ResetPasswordDialog(
            isLoading = settingsUiState.isLoading,
            onDismiss = { showResetPasswordDialog = false },
            onResetPassword = { email ->
                settingsViewModel.resetPassword(email)
                showResetPasswordDialog = false
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    items: List<SwipeSettingsItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SettingsItemRow(
                        item = item,
                        showDivider = index < items.size - 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsItemRow(
    item: SwipeSettingsItem,
    showDivider: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { item.onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        }
    }
}

data class SwipeSettingsItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)



@Composable
private fun Header(
    routineType: RoutineType,
    routineName: String,
    currentTime: String,
    currentDate: String,
    onRoutineClick: () -> Unit,
    onRoutineTypeChanged: (RoutineType) -> Unit,
    onNavigateToCreateRoutine: () -> Unit,
    onNavigateToBrain: () -> Unit,
    onNavigateToPromptefy: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
                Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentTime,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Long press for settings",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
        
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
        RoutineType.MORNING -> "Based on your documents, you're most productive when reviewing project requirements first thing"
        RoutineType.AFTERNOON -> "Your technical documentation suggests taking breaks during complex coding sessions"
        RoutineType.EVENING -> "Research notes indicate better sleep when reviewing non-technical content before bed"
        RoutineType.WEEKEND -> "Document analysis shows you prefer outdoor activities when weather permits"
        else -> "RAG system is analyzing your documents to provide personalized insights"
    }
}

// Mock data functions (these would come from your data layer)
private fun getPredictionCards(routineType: RoutineType): List<PredictionData> {
    return when (routineType) {
        RoutineType.MORNING -> listOf(
            PredictionData(
                icon = "📋",
                title = "Review Requirements",
                subtitle = "Project Requirements.pdf - Key points",
                confidence = 94,
                isLearning = true,
                primaryAction = "Open Document",
                secondaryAction = "Quick Summary"
            ),
            PredictionData(
                icon = "📊",
                title = "Project Analysis",
                subtitle = "Based on your recent conversations",
                confidence = 87,
                isLearning = false,
                primaryAction = "View Analysis",
                secondaryAction = "Ask Questions"
            ),
            PredictionData(
                icon = "📝",
                title = "Document Summary",
                subtitle = "Technical Documentation.docx - Main concepts",
                confidence = 72,
                isLearning = false,
                primaryAction = "Read Summary",
                secondaryAction = "Full Document"
            ),
            PredictionData(
                icon = "🔍",
                title = "Research Insights",
                subtitle = "Research Notes.txt - Key findings",
                confidence = 89,
                isLearning = false,
                primaryAction = "View Insights",
                secondaryAction = "Explore More"
            )
        )
        RoutineType.AFTERNOON -> listOf(
            PredictionData(
                icon = "🔧",
                title = "Technical Deep Dive",
                subtitle = "Authentication system analysis",
                confidence = 98,
                isLearning = false,
                primaryAction = "Open Discussion",
                secondaryAction = "View Code"
            ),
            PredictionData(
                icon = "📋",
                title = "Requirements Check",
                subtitle = "Project Requirements.pdf - Updates needed",
                confidence = 91,
                isLearning = false,
                primaryAction = "Review Changes",
                secondaryAction = "Compare Versions"
            ),
            PredictionData(
                icon = "💡",
                title = "Solution Ideas",
                subtitle = "Based on your research notes",
                confidence = 76,
                isLearning = false,
                primaryAction = "View Ideas",
                secondaryAction = "Brainstorm"
            ),
            PredictionData(
                icon = "📊",
                title = "Progress Report",
                subtitle = "Document analysis summary",
                confidence = 82,
                isLearning = false,
                primaryAction = "Generate Report",
                secondaryAction = "Share Insights"
            )
        )
        RoutineType.EVENING -> listOf(
            PredictionData(
                icon = "📖",
                title = "Research Review",
                subtitle = "Research Notes.txt - Evening summary",
                confidence = 96,
                isLearning = false,
                primaryAction = "Read Summary",
                secondaryAction = "Save Notes"
            ),
            PredictionData(
                icon = "💭",
                title = "Reflection Session",
                subtitle = "Based on today's document analysis",
                confidence = 78,
                isLearning = false,
                primaryAction = "Start Reflection",
                secondaryAction = "View Insights"
            ),
            PredictionData(
                icon = "📝",
                title = "Learning Notes",
                subtitle = "Technical Documentation.docx - Key takeaways",
                confidence = 89,
                isLearning = false,
                primaryAction = "Review Notes",
                secondaryAction = "Add Comments"
            ),
            PredictionData(
                icon = "🌙",
                title = "Tomorrow's Prep",
                subtitle = "Project Requirements.pdf - Next steps",
                confidence = 93,
                isLearning = false,
                primaryAction = "Plan Tomorrow",
                secondaryAction = "Set Reminders"
            )
        )
        RoutineType.WEEKEND -> listOf(
            PredictionData(
                icon = "📚",
                title = "Document Review",
                subtitle = "Weekend reading - Research Notes.txt",
                confidence = 85,
                isLearning = false,
                primaryAction = "Start Reading",
                secondaryAction = "Bookmark"
            ),
            PredictionData(
                icon = "💡",
                title = "Creative Analysis",
                subtitle = "Project Requirements.pdf - New ideas",
                confidence = 71,
                isLearning = false,
                primaryAction = "Explore Ideas",
                secondaryAction = "Save Concepts"
            ),
            PredictionData(
                icon = "🔍",
                title = "Deep Research",
                subtitle = "Technical Documentation.docx - Advanced topics",
                confidence = 68,
                isLearning = false,
                primaryAction = "Begin Research",
                secondaryAction = "Set Goals"
            ),
            PredictionData(
                icon = "📊",
                title = "Weekly Summary",
                subtitle = "Document analysis and insights",
                confidence = 74,
                isLearning = false,
                primaryAction = "View Summary",
                secondaryAction = "Export Report"
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

@Composable
private fun LoginDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSignIn: (String, String) -> Unit,
    onForgotPassword: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sign In")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                TextButton(
                    onClick = onForgotPassword,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSignIn(email, password) },
                enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign In")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ResetPasswordDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onResetPassword: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Reset Password")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Enter your email address and we'll send you a link to reset your password.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onResetPassword(email) },
                enabled = email.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Send Reset Link")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SignUpDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSignUp: (String, String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Account")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSignUp(email, password, displayName) },
                enabled = email.isNotEmpty() && password.isNotEmpty() && displayName.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign Up")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
            onNavigateToBrain = {},
            onNavigateToPromptefy = {},
            onRoutineTypeChanged = {},
            onAddAppClick = {},
            onActionLongClick = {},
            selectedApps = emptyList(),
            isLoadingApps = false
        )
    }
}
