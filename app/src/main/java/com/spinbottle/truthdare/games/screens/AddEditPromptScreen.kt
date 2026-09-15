package com.spinbottle.truthdare.games.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.data.*
import com.spinbottle.truthdare.games.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPromptScreen(
    promptId: String? = null,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit
) {
    val existingPrompt = promptId?.let { CustomPromptsManager.getPromptById(it) }
    val isEditing = existingPrompt != null
    
    var promptType by remember { mutableStateOf(existingPrompt?.type ?: PromptItemType.TRUTH) }
    var promptText by remember { mutableStateOf(existingPrompt?.text ?: "") }
    var difficulty by remember { mutableStateOf(existingPrompt?.difficulty ?: Difficulty.MEDIUM) }
    var category by remember { mutableStateOf(existingPrompt?.category ?: PromptCategory.FRIENDS) }
    
    val maxChars = 200
    val isValid = promptText.trim().length >= 10 && promptText.length <= maxChars
    
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
                        if (isEditing) "Edit Prompt" else "Create Prompt",
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
                    IconButton(
                        onClick = {
                            if (isValid) {
                                val prompt = CustomPrompt(
                                    id = existingPrompt?.id ?: java.util.UUID.randomUUID().toString(),
                                    type = promptType,
                                    category = category,
                                    difficulty = difficulty,
                                    text = promptText.trim(),
                                    isEnabled = existingPrompt?.isEnabled ?: true,
                                    createdAt = existingPrompt?.createdAt ?: System.currentTimeMillis()
                                )
                                if (isEditing) {
                                    CustomPromptsManager.updatePrompt(prompt)
                                } else {
                                    CustomPromptsManager.addPrompt(prompt)
                                }
                                onSave()
                            }
                        },
                        enabled = isValid
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save",
                            tint = if (isValid) AccentGreen else TextGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Type selector
                SectionTitle("Type")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TypeOption(
                        text = "Truth",
                        emoji = "🤔",
                        isSelected = promptType == PromptItemType.TRUTH,
                        color = TruthBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { promptType = PromptItemType.TRUTH }
                    )
                    TypeOption(
                        text = "Dare",
                        emoji = "🔥",
                        isSelected = promptType == PromptItemType.DARE,
                        color = DareOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { promptType = PromptItemType.DARE }
                    )
                }
                
                // Prompt text
                SectionTitle("Prompt Text")
                OutlinedTextField(
                    value = promptText,
                    onValueChange = { if (it.length <= maxChars) promptText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { 
                        Text(
                            if (promptType == PromptItemType.TRUTH) 
                                "What's your most embarrassing..." 
                            else 
                                "Do 10 jumping jacks while...",
                            color = TextGray
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = if (promptType == PromptItemType.TRUTH) TruthBlue else DareOrange,
                        unfocusedBorderColor = CardDark,
                        cursorColor = AccentGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Text(
                    "${promptText.length}/$maxChars characters",
                    fontSize = 12.sp,
                    color = if (promptText.length < 10) SkipRed else TextGray
                )
                
                // Difficulty selector
                SectionTitle("Difficulty")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Difficulty.EASY to AccentGreen,
                        Difficulty.MEDIUM to AccentYellow,
                        Difficulty.HARD to DareOrange
                    ).forEach { (diff, color) ->
                        DifficultyChip(
                            text = diff.name.lowercase().replaceFirstChar { it.uppercase() },
                            isSelected = difficulty == diff,
                            color = color,
                            modifier = Modifier.weight(1f),
                            onClick = { difficulty = diff }
                        )
                    }
                }
                
                // Category selector
                SectionTitle("Category")
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryChip(
                            category = PromptCategory.FRIENDS,
                            isSelected = category == PromptCategory.FRIENDS,
                            modifier = Modifier.weight(1f),
                            onClick = { category = PromptCategory.FRIENDS }
                        )
                        CategoryChip(
                            category = PromptCategory.COUPLES,
                            isSelected = category == PromptCategory.COUPLES,
                            modifier = Modifier.weight(1f),
                            onClick = { category = PromptCategory.COUPLES }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryChip(
                            category = PromptCategory.FAMILY,
                            isSelected = category == PromptCategory.FAMILY,
                            modifier = Modifier.weight(1f),
                            onClick = { category = PromptCategory.FAMILY }
                        )
                        CategoryChip(
                            category = PromptCategory.PARTY,
                            isSelected = category == PromptCategory.PARTY,
                            modifier = Modifier.weight(1f),
                            onClick = { category = PromptCategory.PARTY }
                        )
                    }
                }
                
                // Preview
                SectionTitle("Preview")
                PromptPreview(
                    type = promptType,
                    text = promptText.ifEmpty { "(Enter your prompt text above)" },
                    difficulty = difficulty
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = TextGray,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun TypeOption(
    text: String,
    emoji: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(2.dp, color, RoundedCornerShape(16.dp))
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.2f) else CardDark
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else TextGray
            )
        }
    }
}

@Composable
private fun DifficultyChip(
    text: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(
                if (isSelected) color.copy(alpha = 0.2f) else CardDark,
                RoundedCornerShape(12.dp)
            )
            .then(
                if (isSelected) Modifier.border(1.dp, color, RoundedCornerShape(12.dp))
                else Modifier
            )
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) color else TextGray
        )
    }
}

@Composable
private fun CategoryChip(
    category: PromptCategory,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val emoji = when (category) {
        PromptCategory.FRIENDS -> "👫"
        PromptCategory.COUPLES -> "💕"
        PromptCategory.FAMILY -> "👨‍👩‍👧"
        PromptCategory.PARTY -> "🎉"
        PromptCategory.KIDS -> "🧒"
        PromptCategory.STUDENTS -> "🎓"
    }
    
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(
                if (isSelected) AccentGreen.copy(alpha = 0.2f) else CardDark,
                RoundedCornerShape(12.dp)
            )
            .then(
                if (isSelected) Modifier.border(1.dp, AccentGreen, RoundedCornerShape(12.dp))
                else Modifier
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                category.displayName,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) AccentGreen else TextGray
            )
        }
    }
}

@Composable
private fun PromptPreview(
    type: PromptItemType,
    text: String,
    difficulty: Difficulty
) {
    val color = if (type == PromptItemType.TRUTH) TruthBlue else DareOrange
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    if (type == PromptItemType.TRUTH) "TRUTH" else "DARE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    "✨ Custom!",
                    fontSize = 12.sp,
                    color = AccentGreen
                )
            }
            
            Text(
                text,
                fontSize = 16.sp,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}
