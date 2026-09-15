package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.audio.SoundManager
import com.spinbottle.truthdare.games.data.BottleDesign
import com.spinbottle.truthdare.games.data.ThemeManager
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SpinningBottle(
    onSpinComplete: (Float) -> Unit,
    onTapToSpin: () -> Unit,
    soundManager: SoundManager? = null,
    modifier: Modifier = Modifier
) {
    var isSpinning by remember { mutableStateOf(false) }
    var rotation by remember { mutableFloatStateOf(0f) }
    
    // Get selected bottle design
    val selectedBottle = remember { ThemeManager.selectedBottle }
    
    // Animatable for smooth spin animation
    val animatedRotation = remember { Animatable(0f) }
    
    // Glow pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    // Coroutine scope for animation
    val coroutineScope = rememberCoroutineScope()
    
    // Function to start the spin
    fun startSpin() {
        if (isSpinning) return
        
        isSpinning = true
        soundManager?.playSpinStart()
        onTapToSpin()
        
        coroutineScope.launch {
            // Calculate random spin: 5-8 full rotations + random endpoint
            val extraRotations = Random.nextInt(5, 9) * 360f
            val randomEnd = Random.nextFloat() * 360f
            val currentValue = animatedRotation.value
            val targetRotation = currentValue + extraRotations + randomEnd
            
            // Animate the spin with fast start, slow end
            animatedRotation.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(
                    durationMillis = 4000,
                    easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1.0f)
                )
            )
            
            // Update current rotation and notify
            rotation = targetRotation % 360f
            isSpinning = false
            soundManager?.playSpinComplete()
            onSpinComplete(rotation)
        }
    }
    
    Box(
        modifier = modifier
            .size(200.dp)
            .clickable(enabled = !isSpinning) { 
                startSpin()
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer glow - use bottle colors
        Box(
            modifier = Modifier
                .size(180.dp)
                .blur(30.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            selectedBottle.primaryColor.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        
        // Bottle container - use animatedRotation.value for smooth animation
        Box(
            modifier = Modifier
                .size(160.dp)
                .rotate(animatedRotation.value),
            contentAlignment = Alignment.Center
        ) {
            // Bottle emoji - use selected bottle's emoji
            Text(
                text = selectedBottle.emoji,
                fontSize = 100.sp,
                modifier = Modifier.rotate(-45f) // Adjust bottle orientation
            )
        }
        
        // Center tap hint (when not spinning)
        if (!isSpinning) {
            Box(
                modifier = Modifier
                    .offset(y = 90.dp)
                    .clip(CircleShape)
                    .background(GlassWhite.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "TAP TO SPIN",
                    fontSize = 12.sp,
                    color = TextWhite.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun PlayerCircle(
    players: List<com.spinbottle.truthdare.games.data.Player>,
    selectedPlayerIndex: Int?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val angleStep = 360f / players.size
        
        players.forEachIndexed { index, player ->
            val angle = (angleStep * index - 90) // Start from top
            val isSelected = selectedPlayerIndex == index
            
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.3f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "playerScale"
            )
            
            Box(
                modifier = Modifier
                    .offset(
                        x = (140 * kotlin.math.cos(Math.toRadians(angle.toDouble()))).dp,
                        y = (140 * kotlin.math.sin(Math.toRadians(angle.toDouble()))).dp
                    )
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Player avatar
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 60.dp else 50.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) player.color
                                else player.color.copy(alpha = 0.6f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.avatar,
                            fontSize = if (isSelected) 32.sp else 26.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Player name
                    Text(
                        text = player.name,
                        fontSize = if (isSelected) 14.sp else 11.sp,
                        color = if (isSelected) TextWhite else TextMuted,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
