package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.data.Difficulty
import com.spinbottle.truthdare.games.ui.theme.*

@Composable
fun DifficultyCard(
    difficulty: Difficulty,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDisabled: Boolean = false,
    extraLabel: String? = null
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val borderColor = if (isSelected) difficulty.color else GlassBorder
    val borderWidth = if (isSelected) 3.dp else 1.5.dp
    val contentAlpha = if (isDisabled) 0.4f else 1f
    
    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .alpha(contentAlpha)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        difficulty.color.copy(alpha = if (isSelected) 0.25f else 0.1f),
                        difficulty.color.copy(alpha = if (isSelected) 0.15f else 0.05f)
                    )
                )
            )
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !isDisabled) { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji circle
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(difficulty.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = difficulty.emoji,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = difficulty.displayName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    
                    if (difficulty.requiresPin) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "PIN required",
                            tint = AccentOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    // Extra label (e.g., "(5 prompts)")
                    if (extraLabel != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = extraLabel,
                            fontSize = 12.sp,
                            color = if (isDisabled) SkipRed else AccentGreen
                        )
                    }
                }
                
                Text(
                    text = difficulty.description,
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
            
            // Selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(difficulty.color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
