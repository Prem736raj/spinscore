package com.spinbottle.truthdare.games.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.spinbottle.truthdare.games.data.Avatars
import com.spinbottle.truthdare.games.ui.theme.*

@Composable
fun EmojiPickerDialog(
    selectedEmoji: String?,
    usedEmojis: Set<String> = emptySet(), // Emojis already used by other players
    onEmojiSelected: (String) -> Unit,
    onRandomSelected: () -> Unit,
    onDismiss: () -> Unit
) {
    // Available emojis (excluding already used ones)
    val availableEmojis = Avatars.all.filter { it !in usedEmojis }
    
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DarkCard, DarkSurface)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Choose Avatar",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Pick an emoji or use random",
                    fontSize = 14.sp,
                    color = TextMuted
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Random button
                Button(
                    onClick = onRandomSelected,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPurple.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🎲 Random Avatar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Or pick one:",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                    if (usedEmojis.isNotEmpty()) {
                        Text(
                            text = "${availableEmojis.size} available",
                            fontSize = 12.sp,
                            color = AccentPurple
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Emoji grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Avatars.all) { emoji ->
                        val isSelected = emoji == selectedEmoji
                        val isUsed = emoji in usedEmojis
                        
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .alpha(if (isUsed) 0.3f else 1f)
                                .background(
                                    when {
                                        isUsed -> SkipRed.copy(alpha = 0.2f)
                                        isSelected -> AccentPurple.copy(alpha = 0.3f)
                                        else -> GlassWhite.copy(alpha = 0.1f)
                                    }
                                )
                                .then(
                                    if (isSelected && !isUsed) 
                                        Modifier.border(2.dp, AccentPurple, CircleShape)
                                    else if (isUsed)
                                        Modifier.border(1.dp, SkipRed.copy(alpha = 0.5f), CircleShape)
                                    else Modifier
                                )
                                .clickable(enabled = !isUsed) { onEmojiSelected(emoji) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 26.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cancel button
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", color = TextMuted)
                }
            }
        }
    }
}
