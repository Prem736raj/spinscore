package com.spinbottle.truthdare.games.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.data.BackgroundTheme
import com.spinbottle.truthdare.games.data.BottleDesign
import com.spinbottle.truthdare.games.data.DareProofManager
import com.spinbottle.truthdare.games.data.FavoritesManager
import com.spinbottle.truthdare.games.data.PromptHistoryManager
import com.spinbottle.truthdare.games.data.SettingsHolder
import com.spinbottle.truthdare.games.data.ThemeManager
import com.spinbottle.truthdare.games.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onViewGallery: () -> Unit = {}
) {
    var soundEnabled by remember { mutableStateOf(SettingsHolder.soundEnabled) }
    var hapticEnabled by remember { mutableStateOf(SettingsHolder.hapticEnabled) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkBackgroundSecondary)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
            
            // Settings content - SCROLLABLE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                // ===== GAME PREFERENCES =====
                SettingsSectionHeader("⚙️ Game Preferences")
                
                // Spin Speed
                var spinSpeed by remember { mutableStateOf(SettingsHolder.spinSpeed) }
                SettingsSliderRow(
                    emoji = "🎡",
                    title = "Spin Speed",
                    description = when {
                        spinSpeed < 0.4f -> "Slow"
                        spinSpeed < 0.7f -> "Normal"
                        else -> "Fast"
                    },
                    value = spinSpeed,
                    onValueChange = {
                        spinSpeed = it
                        SettingsHolder.spinSpeed = it
                    },
                    accentColor = AccentTeal
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Default Difficulty
                var defaultDifficulty by remember { mutableStateOf(SettingsHolder.defaultDifficulty) }
                SettingsOptionRow(
                    emoji = "🎯",
                    title = "Default Difficulty",
                    options = listOf("Easy", "Medium", "Hard"),
                    selectedOption = defaultDifficulty,
                    onOptionSelected = {
                        defaultDifficulty = it
                        SettingsHolder.defaultDifficulty = it
                    },
                    accentColor = AccentPurple
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // ===== AUDIO & HAPTICS =====
                SettingsSectionHeader("🔊 Audio & Haptics")
                
                // Sound Effects Toggle
                SettingsToggleRow(
                    icon = Icons.Default.MusicNote,
                    title = "Sound Effects",
                    description = "Play sounds during gameplay",
                    isChecked = soundEnabled,
                    onCheckedChange = { 
                        soundEnabled = it
                        SettingsHolder.soundEnabled = it
                    },
                    accentColor = AccentTeal
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Haptic Feedback Toggle
                SettingsToggleRow(
                    icon = Icons.Default.Vibration,
                    title = "Haptic Feedback",
                    description = "Vibration on taps and events",
                    isChecked = hapticEnabled,
                    onCheckedChange = { 
                        hapticEnabled = it
                        SettingsHolder.hapticEnabled = it
                    },
                    accentColor = AccentOrange
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // ===== CUSTOMIZATION =====
                SettingsSectionHeader("🎨 Customization")
                
                // Bottle Design Picker
                var selectedBottle by remember { mutableStateOf(ThemeManager.selectedBottle) }
                Text(
                    text = "Bottle Design",
                    fontSize = 14.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(BottleDesign.values().toList()) { bottle ->
                        val isUnlocked = ThemeManager.isBottleUnlocked(bottle)
                        val isSelected = selectedBottle == bottle
                        
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) bottle.primaryColor.copy(alpha = 0.3f)
                                    else GlassWhite.copy(alpha = 0.08f)
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        2.dp,
                                        bottle.primaryColor,
                                        RoundedCornerShape(12.dp)
                                    ) else Modifier
                                )
                                .clickable(enabled = isUnlocked) {
                                    selectedBottle = bottle
                                    ThemeManager.selectedBottle = bottle
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = bottle.emoji,
                                    fontSize = 24.sp,
                                    modifier = Modifier.alpha(if (isUnlocked) 1f else 0.4f)
                                )
                                Text(
                                    text = if (!isUnlocked) "🔒" else "",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Background Theme Picker
                var selectedBackground by remember { mutableStateOf(ThemeManager.selectedBackground) }
                Text(
                    text = "Background Theme",
                    fontSize = 14.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(BackgroundTheme.values().toList()) { theme ->
                        val isUnlocked = ThemeManager.isBackgroundUnlocked(theme)
                        val isSelected = selectedBackground == theme
                        
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.verticalGradient(theme.colors))
                                .then(
                                    if (isSelected) Modifier.border(
                                        2.dp,
                                        AccentTeal,
                                        RoundedCornerShape(12.dp)
                                    ) else Modifier
                                )
                                .clickable(enabled = isUnlocked) {
                                    selectedBackground = theme
                                    ThemeManager.selectedBackground = theme
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = theme.emoji,
                                    fontSize = 20.sp,
                                    modifier = Modifier.alpha(if (isUnlocked) 1f else 0.4f)
                                )
                                if (!isUnlocked) {
                                    Text(text = "🔒", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // ===== DATA & STORAGE =====
                SettingsSectionHeader("📁 Data & Storage")
                
                // Dare Gallery
                val proofCount = DareProofManager.getProofCount()
                SettingsNavigationRow(
                    emoji = "📸",
                    title = "Dare Gallery",
                    subtitle = if (proofCount > 0) "$proofCount proof${if (proofCount != 1) "s" else ""} saved" else "No proofs captured yet",
                    onClick = onViewGallery
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Prompt Statistics
                val seenCount = remember { PromptHistoryManager.getSeenCount() }
                val favoritesCount = remember { FavoritesManager.getFavoriteCount() }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassWhite.copy(alpha = 0.08f))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "📊 Statistics",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Prompts Seen", fontSize = 14.sp, color = TextMuted)
                            Text("$seenCount / 1200+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentTeal)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Favorites Saved", fontSize = 14.sp, color = TextMuted)
                            Text("$favoritesCount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentPink)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Dare Photos", fontSize = 14.sp, color = TextMuted)
                            Text("$proofCount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Avoid Recently Played Toggle
                var avoidRecent by remember { mutableStateOf(PromptHistoryManager.avoidRecentlyPlayed) }
                SettingsToggleRow(
                    icon = Icons.Default.History,
                    title = "Avoid Recently Played",
                    description = "Prioritize prompts you haven't seen",
                    isChecked = avoidRecent,
                    onCheckedChange = { 
                        avoidRecent = it
                        PromptHistoryManager.avoidRecentlyPlayed = it
                    },
                    accentColor = AccentGreen
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Reset History Button
                var showResetDialog by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🔄 Reset Prompt History", color = TextMuted)
                }
                
                if (showResetDialog) {
                    AlertDialog(
                        onDismissRequest = { showResetDialog = false },
                        title = { Text("Reset History?", color = TextWhite) },
                        text = { Text("This will mark all prompts as unseen. You'll get fresh prompts!", color = TextMuted) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    PromptHistoryManager.resetHistory()
                                    showResetDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SkipRed)
                            ) {
                                Text("Reset")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { showResetDialog = false }) {
                                Text("Cancel", color = TextWhite)
                            }
                        },
                        containerColor = DarkCard
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // ===== ABOUT =====
                SettingsSectionHeader("ℹ️ About")
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassWhite.copy(alpha = 0.08f))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🍾", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Spin Bottle: Truth or Dare",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Version 1.0.0",
                                    fontSize = 12.sp,
                                    color = AccentTeal
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "The ultimate party game for friends and family! 🎉",
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = AccentPurple,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun SettingsNavigationRow(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium, color = TextWhite)
                Text(subtitle, fontSize = 12.sp, color = TextMuted)
            }
            Text("›", fontSize = 24.sp, color = TextMuted)
        }
    }
}

@Composable
fun SettingsSliderRow(
    emoji: String,
    title: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite.copy(alpha = 0.08f))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Medium, color = TextWhite)
                    Text(description, fontSize = 12.sp, color = accentColor)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = GlassBorder
                )
            )
        }
    }
}

@Composable
fun SettingsOptionRow(
    emoji: String,
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite.copy(alpha = 0.08f))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Medium, color = TextWhite)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { option ->
                    val isSelected = option == selectedOption
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) accentColor else GlassBorder)
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TextWhite else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite.copy(alpha = 0.08f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhite
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
            
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = accentColor,
                    checkedTrackColor = accentColor.copy(alpha = 0.3f),
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = GlassBorder
                )
            )
        }
    }
}
