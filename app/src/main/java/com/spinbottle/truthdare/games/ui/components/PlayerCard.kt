package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.data.Player
import com.spinbottle.truthdare.games.ui.theme.*

@Composable
fun PlayerCard(
    player: Player,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    // Entry animation
    LaunchedEffect(player.id) {
        isVisible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .padding(6.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        // Main card
        Column(
            modifier = Modifier
                .width(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            player.color.copy(alpha = 0.3f),
                            player.color.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    color = player.color.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onRemove() }
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(player.color.copy(alpha = 0.2f))
                    .border(2.dp, player.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.avatar,
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Name
            Text(
                text = player.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
        
        // Remove button
        Box(
            modifier = Modifier
                .offset(x = 4.dp, y = (-4).dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(SkipRed.copy(alpha = 0.9f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun EmptyPlayerSlot(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .width(100.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite.copy(alpha = 0.05f))
            .border(
                width = 2.dp,
                color = GlassBorder.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            fontSize = 32.sp,
            color = TextMuted.copy(alpha = 0.3f)
        )
    }
}
