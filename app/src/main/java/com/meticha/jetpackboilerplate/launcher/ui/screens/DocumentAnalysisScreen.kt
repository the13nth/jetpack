package com.meticha.jetpackboilerplate.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meticha.jetpackboilerplate.launcher.ui.LauncherViewModel
import com.meticha.jetpackboilerplate.launcher.ui.components.AIInsight
import com.meticha.jetpackboilerplate.launcher.ui.components.PredictionCard
import com.meticha.jetpackboilerplate.launcher.ui.components.StatusBar
import com.meticha.jetpackboilerplate.ui.theme.CallBudyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentAnalysisScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Document Insights") },
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
        DocumentAnalysisContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun DocumentAnalysisContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8EAF6),
                        Color(0xFFC5CAE9)
                    )
                )
            )
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
                    text = "Document Insights",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8EAF6)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📄",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "AI Analysis",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3F51B5)
                        )
                    }
                }
            }
            
            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Active Documents Section
                item {
                    Text(
                        text = "📋 Active Documents",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                // Document Preview
                item {
                    DocumentPreview(
                        title = "📋 Project Requirements.pdf",
                        content = "\"The routine-based launcher is an AI-powered minimal Android launcher that anticipates user needs...\"",
                        aiInsight = "AI Extracted Goals: Build AI launcher, focus on user routines, implement RAG system"
                    )
                }
                
                // Generated Action Plan Section
                item {
                    Text(
                        text = "🎯 Generated Action Plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                // Action Plan Cards
                item {
                    getDocumentPredictions().forEach { prediction ->
                        PredictionCard(
                            prediction = prediction,
                            onPrimaryAction = { /* Handle primary action */ },
                            onSecondaryAction = { /* Handle secondary action */ }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Timeline Suggestion
                item {
                    AIInsight(
                        message = "Based on document complexity, estimated 8-12 weeks for full implementation. Focus on MVP first.",
                        modifier = Modifier.padding(vertical = 8.dp)
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
private fun DocumentPreview(
    title: String,
    content: String,
    aiInsight: String,
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
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666),
                lineHeight = androidx.compose.ui.unit.TextUnit(1.4f, androidx.compose.ui.unit.TextUnitType.Sp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667EEA),
                                Color(0xFF764BA2)
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "✨ $aiInsight",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

// Mock data for document predictions
private fun getDocumentPredictions(): List<PredictionData> {
    return listOf(
        PredictionData(
            icon = "📝",
            title = "Design System Creation",
            subtitle = "Based on requirements analysis",
            confidence = 92,
            isLearning = false,
            primaryAction = "Start Design",
            secondaryAction = "View Requirements"
        ),
        PredictionData(
            icon = "🔬",
            title = "RAG Research",
            subtitle = "Study implementation patterns",
            confidence = 88,
            isLearning = false,
            primaryAction = "Research",
            secondaryAction = "Save Resources"
        ),
        PredictionData(
            icon = "⏰",
            title = "Sprint Planning",
            subtitle = "Break down into 2-week sprints",
            confidence = 76,
            isLearning = false,
            primaryAction = "Plan Sprints",
            secondaryAction = "Set Deadlines"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DocumentAnalysisScreenPreview() {
    CallBudyTheme {
        DocumentAnalysisContent()
    }
} 