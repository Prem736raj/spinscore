package com.spinbottle.truthdare.games.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.ui.components.ParticleBackground
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    // Animation states
    var startAnimations by remember { mutableStateOf(false) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    
    // Logo scale animation
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.5f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "scale"
    )
    
    // Logo alpha animation
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )
    
    // Glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    // Start animations and navigate after delay
    LaunchedEffect(Unit) {
        startAnimations = true
        delay(2500)
        onSplashComplete()
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Animated particle background
        ParticleBackground(
            modifier = Modifier.fillMaxSize(),
            particleCount = 40
        )
        
        // Logo content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(logoAlpha)
                .scale(logoScale)
        ) {
            // Bottle emoji with glow
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "🍾",
                    fontSize = 100.sp,
                    modifier = Modifier
                        .scale(1.3f)
                        .blur(25.dp)
                        .alpha(glowAlpha * 0.6f)
                )
                Text(
                    text = "🍾",
                    fontSize = 100.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App name with gradient
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Spin Bottle",
                    style = TextStyle(
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        brush = Brush.horizontalGradient(
                            colors = listOf(GlowPurple, GlowPink, GlowOrange)
                        )
                    ),
                    modifier = Modifier.blur(20.dp)
                )
                Text(
                    text = "Spin Bottle",
                    style = TextStyle(
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        brush = Brush.horizontalGradient(
                            colors = listOf(AccentPurple, AccentPink, AccentOrange)
                        )
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Truth or Dare",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = AccentPurpleLight
            )
        }
    }
}
