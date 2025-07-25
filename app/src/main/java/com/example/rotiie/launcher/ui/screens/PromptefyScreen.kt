package com.example.rotiie.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptefyScreen(
    onNavigateBack: () -> Unit,
    onGeneratePrompt: (String) -> Unit = {}
) {
    var inputConcept by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("Gemini 2.5 Flash") }
    var cfgScale by remember { mutableStateOf(0.7f) }
    var customElements by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Simple") }
    var selectedCameraStyle by remember { mutableStateOf("None") }
    var selectedCameraDirection by remember { mutableStateOf("None") }
    var selectedPacing by remember { mutableStateOf("None") }
    var selectedSpecialEffects by remember { mutableStateOf("None") }
    var selectedPromptLength by remember { mutableStateOf("Medium") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = ">",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "-",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Promptefy",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                                            navigationIcon = {
                                IconButton(onClick = onNavigateBack) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2A2A2A))
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Create detailed video prompts, optionally with image references.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Input Concept
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Input Concept",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                BasicTextField(
                    value = inputConcept,
                    onValueChange = { inputConcept = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    decorationBox = { innerTextField ->
                        if (inputConcept.isEmpty()) {
                            Text(
                                text = "e.g., A futuristic city at dusk",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // Select Model
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Model",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                DropdownField(
                    value = selectedModel,
                    onValueChange = { selectedModel = it },
                    options = listOf("Gemini 2.5 Flash", "Gemini 2.0", "DALL-E 3", "Midjourney")
                )
            }

            // CFG Scale
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cfg Scale",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "%.1f".format(cfgScale),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
                Slider(
                    value = cfgScale,
                    onValueChange = { cfgScale = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Black,
                        activeTrackColor = Color.Black,
                        inactiveTrackColor = Color.Gray
                    )
                )
            }

            // Upload Images
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Upload Images (Optional - Max 10)",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "No file chosen",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    Button(
                        onClick = { /* File picker */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Choose files",
                            color = Color.White
                        )
                    }
                }
            }

            // Style
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Style",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                DropdownField(
                    value = selectedStyle,
                    onValueChange = { selectedStyle = it },
                    options = listOf("Simple", "Cinematic", "Artistic", "Photorealistic", "Animated")
                )
            }

            // Camera Style
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Camera Style",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                DropdownField(
                    value = selectedCameraStyle,
                    onValueChange = { selectedCameraStyle = it },
                    options = listOf("None", "Close-up", "Wide shot", "Aerial", "Tracking", "Handheld")
                )
            }

            // Camera Direction
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Camera Direction",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                DropdownField(
                    value = selectedCameraDirection,
                    onValueChange = { selectedCameraDirection = it },
                    options = listOf("None", "Forward", "Backward", "Left", "Right", "Up", "Down")
                )
            }

            // Pacing
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Pacing",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                DropdownField(
                    value = selectedPacing,
                    onValueChange = { selectedPacing = it },
                    options = listOf("None", "Slow", "Medium", "Fast", "Dynamic")
                )
            }

            // Special Effects
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Special Effects",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                DropdownField(
                    value = selectedSpecialEffects,
                    onValueChange = { selectedSpecialEffects = it },
                    options = listOf("None", "Lens flare", "Motion blur", "Depth of field", "Color grading", "Particle effects")
                )
            }

            // Prompt Length
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Prompt Length",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                DropdownField(
                    value = selectedPromptLength,
                    onValueChange = { selectedPromptLength = it },
                    options = listOf("Short", "Medium", "Long", "Detailed")
                )
            }

            // Custom Elements
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Custom Elements (Optional)",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                BasicTextField(
                    value = customElements,
                    onValueChange = { customElements = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    decorationBox = { innerTextField ->
                        if (customElements.isEmpty()) {
                            Text(
                                text = "e.g., neon signs, flying cars",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // Generate Button
            Button(
                onClick = { onGeneratePrompt(inputConcept) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Generate Video Prompt",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = Color.Black,
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown",
                tint = Color.Black
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PromptefyScreenPreview() {
    MaterialTheme {
        PromptefyScreen(
            onNavigateBack = {},
            onGeneratePrompt = {}
        )
    }
} 