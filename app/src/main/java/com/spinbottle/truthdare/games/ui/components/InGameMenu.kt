package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.ui.theme.*

data class GameMenuOption(
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InGameMenuSheet(
    isVisible: Boolean,
    isPaused: Boolean,
    proofCount: Int = 0,
    onDismiss: () -> Unit,
    onPauseToggle: () -> Unit,
    onAddPlayer: () -> Unit,
    onRemovePlayer: () -> Unit,
    onChangeDifficulty: () -> Unit,
    onViewGallery: () -> Unit = {},
    onEndGame: () -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = DarkCard,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(40.dp, 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GlassBorder)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Game Menu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Pause/Resume
                MenuOptionRow(
                    icon = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    label = if (isPaused) "Resume Game" else "Pause Game",
                    color = AccentTeal,
                    onClick = {
                        onPauseToggle()
                        onDismiss()
                    }
                )
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = GlassBorder.copy(alpha = 0.3f)
                )
                
                // Add Player
                MenuOptionRow(
                    icon = Icons.Default.PersonAdd,
                    label = "Add Player",
                    color = AccentGreen,
                    onClick = {
                        onAddPlayer()
                        onDismiss()
                    }
                )
                
                // Remove Player
                MenuOptionRow(
                    icon = Icons.Default.PersonRemove,
                    label = "Remove Player",
                    color = AccentOrange,
                    onClick = {
                        onRemovePlayer()
                        onDismiss()
                    }
                )
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = GlassBorder.copy(alpha = 0.3f)
                )
                
                // Change Difficulty
                MenuOptionRow(
                    icon = Icons.Default.Speed,
                    label = "Change Difficulty",
                    color = AccentPurple,
                    onClick = {
                        onChangeDifficulty()
                        onDismiss()
                    }
                )
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = GlassBorder.copy(alpha = 0.3f)
                )
                
                // End Game
                MenuOptionRow(
                    icon = Icons.Default.Flag,
                    label = "End Game",
                    color = SkipRed,
                    onClick = {
                        onEndGame()
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun MenuOptionRow(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextWhite
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun PauseOverlay(
    isPaused: Boolean,
    onResume: () -> Unit
) {
    AnimatedVisibility(
        visible = isPaused,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground.copy(alpha = 0.9f))
                .clickable(onClick = onResume),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Paused",
                    tint = AccentPurple,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "GAME PAUSED",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Resume", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Tap anywhere to continue",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }
        }
    }
}
