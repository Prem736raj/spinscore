package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.spinbottle.truthdare.games.ui.theme.*
import kotlin.random.Random

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    val color: Color,
    val size: Float,
    val speedX: Float,
    val speedY: Float,
    val rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiAnimation(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        AccentPurple, AccentPink, AccentOrange, AccentTeal,
        AccentGreen, AccentBlue, AccentYellow, ParticlePurple
    )
    
    var particles by remember { mutableStateOf(listOf<ConfettiParticle>()) }
    var time by remember { mutableFloatStateOf(0f) }
    
    // Initialize particles when animation starts
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            particles = List(150) {
                ConfettiParticle(
                    x = Random.nextFloat() * 1200f,
                    y = Random.nextFloat() * -500f - 100f,
                    color = colors.random(),
                    size = Random.nextFloat() * 12f + 6f,
                    speedX = Random.nextFloat() * 4f - 2f,
                    speedY = Random.nextFloat() * 8f + 4f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = Random.nextFloat() * 10f - 5f
                )
            }
        }
    }
    
    // Animation loop
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val animatedTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    // Update particles
    LaunchedEffect(animatedTime) {
        if (isPlaying) {
            particles = particles.map { p ->
                p.copy(
                    x = p.x + p.speedX,
                    y = p.y + p.speedY
                )
            }
        }
    }
    
    if (isPlaying) {
        Canvas(modifier = modifier.fillMaxSize()) {
            particles.forEach { particle ->
                if (particle.y < size.height + 50) {
                    drawCircle(
                        color = particle.color,
                        radius = particle.size,
                        center = Offset(particle.x, particle.y)
                    )
                }
            }
        }
    }
}
