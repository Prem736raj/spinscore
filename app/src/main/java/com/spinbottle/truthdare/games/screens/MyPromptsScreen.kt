package com.spinbottle.truthdare.games.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.data.*
import com.spinbottle.truthdare.games.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPromptsScreen(
    onNavigateBack: () -> Unit,
    onAddPrompt: () -> Unit,
    onEditPrompt: (String) -> Unit
) {
    var customPrompts by remember { mutableStateOf(CustomPromptsManager.getAllPrompts()) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, SurfaceDark)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            TopAppBar(
                title = { 
                    Text(
                        "My Prompts",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddPrompt) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Prompt",
                            tint = AccentGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            
            if (customPrompts.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "✨",
                            fontSize = 64.sp
                        )
                        Text(
                            "No Custom Prompts Yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            "Create your own truth questions and dare challenges!",
                            fontSize = 14.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddPrompt,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Prompt", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Prompts list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(customPrompts, key = { it.id }) { prompt ->
                        CustomPromptCard(
                            prompt = prompt,
                            onToggle = {
                                CustomPromptsManager.togglePrompt(prompt.id)
                                customPrompts = CustomPromptsManager.getAllPrompts()
                            },
                            onEdit = { onEditPrompt(prompt.id) },
                            onDelete = { showDeleteDialog = prompt.id }
                        )
                    }
                }
            }
        }
        
        // Delete confirmation dialog
        showDeleteDialog?.let { promptId ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Prompt?", color = TextWhite) },
                text = { 
                    Text(
                        "This custom prompt will be permanently deleted.",
                        color = TextGray
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            CustomPromptsManager.deletePrompt(promptId)
                            customPrompts = CustomPromptsManager.getAllPrompts()
                            showDeleteDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkipRed
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel", color = TextGray)
                    }
                },
                containerColor = CardDark,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun CustomPromptCard(
    prompt: CustomPrompt,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val typeColor = if (prompt.type == PromptItemType.TRUTH) TruthBlue else DareOrange
    val typeLabel = if (prompt.type == PromptItemType.TRUTH) "TRUTH" else "DARE"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(
            containerColor = if (prompt.isEnabled) CardDark else CardDark.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type badge
                Box(
                    modifier = Modifier
                        .background(typeColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, typeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        typeLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                }
                
                // Difficulty badge
                val diffColor = when (prompt.difficulty) {
                    Difficulty.EASY -> AccentGreen
                    Difficulty.MEDIUM -> AccentYellow
                    Difficulty.HARD -> DareOrange
                    Difficulty.EXTREME -> SkipRed
                    else -> TextGray
                }
                Text(
                    prompt.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = diffColor
                )
            }
            
            // Prompt text
            Text(
                prompt.text,
                fontSize = 16.sp,
                color = if (prompt.isEnabled) TextWhite else TextGray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            // Footer row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category
                Text(
                    prompt.category.displayName,
                    fontSize = 12.sp,
                    color = TextGray
                )
                
                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Toggle
                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (prompt.isEnabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (prompt.isEnabled) "Disable" else "Enable",
                            tint = if (prompt.isEnabled) AccentGreen else TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    // Delete
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = SkipRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
