package com.example.rotiie.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rotiie.launcher.ui.components.StatusBar
import com.example.rotiie.ui.theme.CallBudyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            uiState = uiState,
            onSignIn = viewModel::signIn,
            onSignUp = viewModel::signUp,
            onSignOut = viewModel::signOut,
            onResetPassword = viewModel::resetPassword,
            onClearError = viewModel::clearError,
            onClearMessage = viewModel::clearMessage,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String, String) -> Unit,
    onSignOut: () -> Unit,
    onResetPassword: (String) -> Unit,
    onClearError: () -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLoginDialog by remember { mutableStateOf(false) }
    var showSignUpDialog by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    
    // Clear error/message when dialogs are shown
    LaunchedEffect(showLoginDialog, showSignUpDialog, showResetPasswordDialog) {
        if (showLoginDialog || showSignUpDialog || showResetPasswordDialog) {
            onClearError()
            onClearMessage()
        }
    }
    
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
                    text = "Settings",
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
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color(0xFF424242)
                        )
                        Text(
                            text = "Account & Preferences",
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
                // Account Section
                item {
                    SettingsSection(
                        title = "👤 Account",
                        items = if (uiState.isLoggedIn) {
                            listOf(
                                SettingsItem(
                                    title = "User", // TODO: Get from Firebase when re-enabled
                                    subtitle = "", // TODO: Get from Firebase when re-enabled
                                    action = SettingsAction.Status("✓ Signed In", Color(0xFF10B981))
                                ),
                                SettingsItem(
                                    title = "Sign Out",
                                    subtitle = "Sign out of your account",
                                    action = SettingsAction.Button("Sign Out", Color(0xFFDC2626))
                                )
                            )
                        } else {
                            listOf(
                                SettingsItem(
                                    title = "Sign In",
                                    subtitle = "Sign in to sync your data",
                                    action = SettingsAction.Button("Sign In")
                                ),
                                SettingsItem(
                                    title = "Create Account",
                                    subtitle = "Create a new account",
                                    action = SettingsAction.Button("Sign Up")
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
        
        // Error/Message Snackbar
        if (uiState.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = onClearError) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(uiState.error!!)
            }
        }
        
        if (uiState.message != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = onClearMessage) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(uiState.message!!)
            }
        }
    }
    
    // Login Dialog
    if (showLoginDialog) {
        LoginDialog(
            isLoading = uiState.isLoading,
            onDismiss = { showLoginDialog = false },
            onSignIn = { email, password ->
                onSignIn(email, password)
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
            isLoading = uiState.isLoading,
            onDismiss = { showSignUpDialog = false },
            onSignUp = { email, password, displayName ->
                onSignUp(email, password, displayName)
                showSignUpDialog = false
            }
        )
    }
    
    // Reset Password Dialog
    if (showResetPasswordDialog) {
        ResetPasswordDialog(
            isLoading = uiState.isLoading,
            onDismiss = { showResetPasswordDialog = false },
            onResetPassword = { email ->
                onResetPassword(email)
                showResetPasswordDialog = false
            }
        )
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
    var showLoginDialog by remember { mutableStateOf(false) }
    var showSignUpDialog by remember { mutableStateOf(false) }
    
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
                    onClick = { 
                        when (action.text) {
                            "Sign In" -> showLoginDialog = true
                            "Sign Up" -> showSignUpDialog = true
                            "Sign Out" -> {
                                // Handle sign out
                            }
                            else -> { /* Handle other button clicks */ }
                        }
                    },
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

@Composable
private fun LoginDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSignIn: (String, String) -> Unit,
    onForgotPassword: () -> Unit
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

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    CallBudyTheme {
        SettingsContent(
            uiState = SettingsUiState(),
            onSignIn = { _, _ -> },
            onSignUp = { _, _, _ -> },
            onSignOut = { },
            onResetPassword = { },
            onClearError = { },
            onClearMessage = { }
        )
    }
} 