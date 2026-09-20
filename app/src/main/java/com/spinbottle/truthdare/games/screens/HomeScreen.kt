package com.spinbottle.truthdare.games.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.spinbottle.truthdare.games.ui.components.*
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onStartGame: () -> Unit,
    onResumeGame: () -> Unit,
    canResumeGame: Boolean,
    onKidsMode: () -> Unit,
    onHowToPlay: () -> Unit,
    onSettings: () -> Unit,
    onMyPrompts: () -> Unit = {},
    isPremium: Boolean = false,
    onPremiumClick: () -> Unit = {}
) {
    // Animation states for enter animations
    var showLogo by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var showBottomButtons by remember { mutableStateOf(false) }
    
    // Trigger enter animations
    LaunchedEffect(Unit) {
        delay(100)
        showLogo = true
        delay(300)
        showButtons = true
        delay(200)
        showBottomButtons = true
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Animated particle background
        ParticleBackground(
            modifier = Modifier.fillMaxSize(),
            particleCount = 60
        )
        
        // Premium Button at Top Right
        if (!isPremium) {
            AnimatedVisibility(
                visible = showLogo,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .statusBarsPadding(),
                enter = fadeIn(animationSpec = tween(800))
            ) {
                androidx.compose.material3.IconButton(
                    onClick = onPremiumClick,
                    modifier = Modifier.size(56.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Premium",
                        tint = AccentOrange,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Animated Logo Section
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(animationSpec = tween(800)) + 
                        slideInVertically(
                            animationSpec = tween(800, easing = FastOutSlowInEasing),
                            initialOffsetY = { -50 }
                        )
            ) {
                GlowingLogo()
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Main Action Buttons
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(animationSpec = tween(600)) + 
                        slideInVertically(
                            animationSpec = tween(600, easing = FastOutSlowInEasing),
                            initialOffsetY = { 100 }
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Start Game Button - Primary CTA with pulse
                    PulsingButton(
                        text = "Start Game",
                        icon = Icons.Default.PlayArrow,
                        gradient = Brush.horizontalGradient(
                            colors = listOf(AccentPink, AccentOrange)
                        ),
                        onClick = onStartGame
                    )
                    
                    if (canResumeGame) {
                        GlassButton(
                            text = "Resume Game",
                            icon = Icons.Default.Refresh,
                            iconTint = AccentTeal,
                            onClick = onResumeGame
                        )
                    }
                    
                    // Kids Mode Button - Glassmorphic with friendly colors
                    GlassButton(
                        text = "Kids Mode",
                        icon = Icons.Default.ChildCare,
                        iconTint = KidsModeGreen,
                        onClick = onKidsMode
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Bottom Navigation Buttons
            AnimatedVisibility(
                visible = showBottomButtons,
                enter = fadeIn(animationSpec = tween(500)) + 
                        slideInVertically(
                            animationSpec = tween(500, easing = FastOutSlowInEasing),
                            initialOffsetY = { 50 }
                        )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassIconButton(
                        text = "My Prompts",
                        icon = Icons.Default.Edit,
                        iconTint = AccentOrange,
                        onClick = onMyPrompts
                    )
                    GlassIconButton(
                        text = "How to Play",
                        icon = Icons.Default.Help,
                        iconTint = AccentBlue,
                        onClick = onHowToPlay
                    )
                    GlassIconButton(
                        text = "Settings",
                        icon = Icons.Default.Settings,
                        iconTint = AccentPurple,
                        onClick = onSettings
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
