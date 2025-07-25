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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrainScreen(
    onNavigateBack: () -> Unit,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: () -> Unit = {}
) {
    var showUploadDialog by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var selectedDocument by remember { mutableStateOf<DocumentInfo?>(null) }
    var conversations by remember { mutableStateOf(sampleConversations) }
    var documents by remember { mutableStateOf(sampleDocuments) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "RAG Brain",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showUploadDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload Document"
                        )
                    }
                    IconButton(onClick = onPrimaryAction) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewChatDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "New Chat"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                RAGHeader()
            }
            
            item {
                DocumentUploadSection(
                    documents = documents,
                    onDocumentClick = { document ->
                        selectedDocument = document
                    },
                    onUploadClick = { showUploadDialog = true }
                )
            }
            
            item {
                RecentConversationsSection(
                    conversations = conversations.take(3),
                    onConversationClick = { conversation ->
                        // Navigate to conversation detail
                    }
                )
            }
            
            item {
                AIInsightsSection()
            }
            
            item {
                RAGMetricsSection()
            }
        }
        
        // Upload Document Dialog
        if (showUploadDialog) {
            UploadDocumentDialog(
                onDismiss = { showUploadDialog = false },
                onDocumentUploaded = { documentName ->
                    documents = documents + DocumentInfo(
                        id = documents.size.toString(),
                        name = documentName,
                        type = "PDF",
                        uploadDate = Date(),
                        size = "2.3 MB",
                        isProcessed = false
                    )
                    showUploadDialog = false
                }
            )
        }
        
        // New Chat Dialog
        if (showNewChatDialog) {
            NewChatDialog(
                documents = documents.filter { it.isProcessed },
                onDismiss = { showNewChatDialog = false },
                onChatCreated = { documentId, query ->
                    val newConversation = Conversation(
                        id = conversations.size.toString(),
                        title = "New Chat",
                        documentName = documents.find { it.id == documentId }?.name ?: "Unknown",
                        lastMessage = query,
                        timestamp = Date(),
                        messageCount = 1
                    )
                    conversations = listOf(newConversation) + conversations
                    showNewChatDialog = false
                }
            )
        }
    }
}

@Composable
private fun RAGHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2E3440),
                            Color(0xFF3B4252)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF88C0D0)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "RAG Brain System",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                
                Text(
                    text = "Upload documents and get personalized AI responses",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFF88C0D0).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF88C0D0).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF88C0D0)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Upload",
                                color = Color(0xFF88C0D0),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFB48EAD).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFB48EAD).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFB48EAD)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Chat",
                                color = Color(0xFFB48EAD),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFD08770).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD08770).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFD08770)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Search",
                                color = Color(0xFFD08770),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentUploadSection(
    documents: List<DocumentInfo>,
    onDocumentClick: (DocumentInfo) -> Unit,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Uploaded Documents",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                IconButton(onClick = onUploadClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload Document",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (documents.isEmpty()) {
                EmptyDocumentsState(onUploadClick = onUploadClick)
            } else {
                documents.forEach { document ->
                    DocumentItem(
                        document = document,
                        onClick = { onDocumentClick(document) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyDocumentsState(onUploadClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUploadClick() }
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Upload,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No Documents Uploaded",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            
            Text(
                text = "Upload your first document to start getting personalized AI responses",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onUploadClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upload Document")
            }
        }
    }
}

@Composable
private fun DocumentItem(
    document: DocumentInfo,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = when (document.type) {
                    "PDF" -> Icons.Default.PictureAsPdf
                    "DOC" -> Icons.Default.Description
                    "TXT" -> Icons.Default.TextSnippet
                    else -> Icons.Default.InsertDriveFile
                },
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = document.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = document.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = document.size,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (document.isProcessed) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Processed",
                    tint = Color(0xFFA3BE8C)
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun RecentConversationsSection(
    conversations: List<Conversation>,
    onConversationClick: (Conversation) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Recent Conversations",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (conversations.isEmpty()) {
                Text(
                    text = "No conversations yet. Start a new chat to begin!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            } else {
                conversations.forEach { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = { onConversationClick(conversation) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = formatDate(conversation.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = conversation.documentName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = conversation.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${conversation.messageCount} messages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AIInsightsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Insights",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF88C0D0)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InsightItem(
                icon = Icons.Default.Lightbulb,
                title = "Document Analysis",
                description = "Your uploaded documents are being analyzed for key insights and patterns.",
                color = Color(0xFF88C0D0)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InsightItem(
                icon = Icons.Default.Psychology,
                title = "Personalized Responses",
                description = "AI responses are tailored based on your document content and conversation history.",
                color = Color(0xFFB48EAD)
            )
        }
    }
}

@Composable
private fun InsightItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
private fun RAGMetricsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "RAG Metrics",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    icon = Icons.Default.Upload,
                    label = "Documents",
                    value = "3",
                    color = Color(0xFF88C0D0)
                )
                MetricItem(
                    icon = Icons.Default.Chat,
                    label = "Conversations",
                    value = "12",
                    color = Color(0xFFB48EAD)
                )
                MetricItem(
                    icon = Icons.Default.Search,
                    label = "Queries",
                    value = "47",
                    color = Color(0xFFD08770)
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = color
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )
    }
}

@Composable
private fun UploadDocumentDialog(
    onDismiss: () -> Unit,
    onDocumentUploaded: (String) -> Unit
) {
    var documentName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Upload Document")
        },
        text = {
            Column {
                Text(
                    text = "Upload a document to enable RAG-powered conversations",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = documentName,
                    onValueChange = { documentName = it },
                    label = { Text("Document Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* File picker would go here */ }
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Tap to select file",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = "Supports PDF, DOC, TXT",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (documentName.isNotBlank()) {
                        onDocumentUploaded(documentName)
                    }
                }
            ) {
                Text("Upload")
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
private fun NewChatDialog(
    documents: List<DocumentInfo>,
    onDismiss: () -> Unit,
    onChatCreated: (String, String) -> Unit
) {
    var selectedDocumentId by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("New Chat")
        },
        text = {
            Column {
                Text(
                    text = "Select a document and ask a question",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (documents.isNotEmpty()) {
                    Text(
                        text = "Select Document:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    documents.forEach { document ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDocumentId = document.id }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDocumentId == document.id,
                                onClick = { selectedDocumentId = document.id }
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = document.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Text(
                        text = "No processed documents available. Please upload and process a document first.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Your Question") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = documents.isNotEmpty()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedDocumentId.isNotBlank() && query.isNotBlank()) {
                        onChatCreated(selectedDocumentId, query)
                    }
                },
                enabled = selectedDocumentId.isNotBlank() && query.isNotBlank()
            ) {
                Text("Start Chat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Data Classes
data class DocumentInfo(
    val id: String,
    val name: String,
    val type: String,
    val uploadDate: Date,
    val size: String,
    val isProcessed: Boolean
)

data class Conversation(
    val id: String,
    val title: String,
    val documentName: String,
    val lastMessage: String,
    val timestamp: Date,
    val messageCount: Int
)

// Sample Data
private val sampleDocuments = listOf(
    DocumentInfo(
        id = "1",
        name = "Project Requirements.pdf",
        type = "PDF",
        uploadDate = Date(),
        size = "2.3 MB",
        isProcessed = true
    ),
    DocumentInfo(
        id = "2",
        name = "Technical Documentation.docx",
        type = "DOC",
        uploadDate = Date(System.currentTimeMillis() - 86400000),
        size = "1.8 MB",
        isProcessed = true
    ),
    DocumentInfo(
        id = "3",
        name = "Research Notes.txt",
        type = "TXT",
        uploadDate = Date(System.currentTimeMillis() - 172800000),
        size = "0.5 MB",
        isProcessed = false
    )
)

private val sampleConversations = listOf(
    Conversation(
        id = "1",
        title = "Project Analysis",
        documentName = "Project Requirements.pdf",
        lastMessage = "What are the key requirements for the mobile app?",
        timestamp = Date(),
        messageCount = 5
    ),
    Conversation(
        id = "2",
        title = "Technical Questions",
        documentName = "Technical Documentation.docx",
        lastMessage = "How does the authentication system work?",
        timestamp = Date(System.currentTimeMillis() - 3600000),
        messageCount = 3
    ),
    Conversation(
        id = "3",
        title = "Research Discussion",
        documentName = "Research Notes.txt",
        lastMessage = "Can you summarize the main findings?",
        timestamp = Date(System.currentTimeMillis() - 7200000),
        messageCount = 8
    )
)

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
fun BrainScreenPreview() {
    MaterialTheme {
        BrainScreen(
            onNavigateBack = {},
            onPrimaryAction = {},
            onSecondaryAction = {}
        )
    }
} 