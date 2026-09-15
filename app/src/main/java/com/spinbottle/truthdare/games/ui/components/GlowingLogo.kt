package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.ui.theme.*

@Composable
fun GlowingLogo(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlow")
    
    // Glow pulse animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    // Subtle scale animation for emoji
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emojiScale"
    )
    
    // Glow blur size
    val glowSize by infiniteTransition.animateFloat(
        initialValue = 15f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowSize"
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated Bottle Emoji with glow
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Glow layer (blurred duplicate)
            Text(
                text = "🍾",
                fontSize = 80.sp,
                modifier = Modifier
                    .scale(emojiScale * 1.2f)
                    .blur(glowSize.dp)
            )
            // Main emoji
            Text(
                text = "🍾",
                fontSize = 80.sp,
                modifier = Modifier.scale(emojiScale)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Main Title with gradient glow
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Glow layer
            Text(
                text = "Spin Bottle",
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.horizontalGradient(
                        colors = listOf(GlowPurple, GlowPink, GlowOrange)
                    )
                ),
                modifier = Modifier
                    .blur(glowSize.dp * 0.8f)
            )
            // Main text with gradient
            Text(
                text = "Spin Bottle",
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.horizontalGradient(
                        colors = listOf(AccentPurple, AccentPink, AccentOrange)
                    )
                )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle with glow
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Truth or Dare",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = AccentPurple.copy(alpha = glowAlpha),
                modifier = Modifier.blur(8.dp)
            )
            Text(
                text = "Truth or Dare",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = AccentPurpleLight
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Tagline
        Text(
            text = "1000+ Free Prompts • No Ads • Pure Fun",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}
