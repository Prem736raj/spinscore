package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import com.spinbottle.truthdare.games.ui.theme.*

data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val color: Color,
    val speedX: Float,
    val speedY: Float,
    val alpha: Float,
    val type: ParticleType = ParticleType.CIRCLE
)

enum class ParticleType {
    CIRCLE, STAR, SPARKLE
}

@Composable
fun ParticleBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 50
) {
    val particles = remember { mutableStateListOf<Particle>() }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleAnimation"
    )
    
    val particleColors = listOf(
        ParticlePurple,
        ParticlePink,
        ParticleOrange,
        ParticleTeal,
        ParticleYellow,
        ParticleBlue
    )
    
    LaunchedEffect(Unit) {
        particles.clear()
        repeat(particleCount) {
            particles.add(
                Particle(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    radius = Random.nextFloat() * 4f + 2f,
                    color = particleColors.random(),
                    speedX = (Random.nextFloat() - 0.5f) * 0.002f,
                    speedY = Random.nextFloat() * 0.001f + 0.0005f,
                    alpha = Random.nextFloat() * 0.5f + 0.2f,
                    type = ParticleType.values().random()
                )
            )
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        // Draw gradient background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    DarkBackgroundDeep,
                    DarkBackground,
                    DarkBackgroundSecondary,
                    DarkBackground
                )
            )
        )
        
        // Update and draw particles
        particles.forEachIndexed { index, particle ->
            // Derive position from animation time rather than mutating model state
            // inside the draw pass. The 600 factor approximates 10 seconds at
            // 60 Hz while remaining refresh-rate independent.
            val timeUnits = animationProgress * 600f
            val rawX = particle.x + particle.speedX * timeUnits
            val rawY = particle.y + particle.speedY * timeUnits
            val normalizedX = ((rawX % 1f) + 1f) % 1f
            val normalizedY = ((rawY % 1f) + 1f) % 1f
            
            val actualX = normalizedX * canvasWidth
            val actualY = normalizedY * canvasHeight
            
            // Twinkle effect
            val twinkle = (sin(animationProgress * 6.28f + index) + 1f) / 2f
            val currentAlpha = particle.alpha * (0.5f + twinkle * 0.5f)
            
            when (particle.type) {
                ParticleType.CIRCLE -> {
                    // Outer glow
                    drawCircle(
                        color = particle.color.copy(alpha = currentAlpha * 0.3f),
                        radius = particle.radius * 3f,
                        center = Offset(actualX, actualY)
                    )
                    // Inner circle
                    drawCircle(
                        color = particle.color.copy(alpha = currentAlpha),
                        radius = particle.radius,
                        center = Offset(actualX, actualY)
                    )
                }
                ParticleType.STAR -> {
                    // Draw a simple star shape using lines
                    val starSize = particle.radius * 2.5f
                    for (i in 0 until 4) {
                        val angle = (i * 45f) * (3.14159f / 180f)
                        drawLine(
                            color = particle.color.copy(alpha = currentAlpha),
                            start = Offset(
                                actualX - cos(angle) * starSize,
                                actualY - sin(angle) * starSize
                            ),
                            end = Offset(
                                actualX + cos(angle) * starSize,
                                actualY + sin(angle) * starSize
                            ),
                            strokeWidth = 2f
                        )
                    }
                }
                ParticleType.SPARKLE -> {
                    // Diamond sparkle
                    val sparkleSize = particle.radius * 2f
                    drawCircle(
                        color = Color.White.copy(alpha = currentAlpha * 0.8f),
                        radius = particle.radius * 0.5f,
                        center = Offset(actualX, actualY)
                    )
                    // Rays
                    listOf(0f, 90f).forEach { angle ->
                        val rad = angle * (3.14159f / 180f)
                        drawLine(
                            color = Color.White.copy(alpha = currentAlpha * 0.6f),
                            start = Offset(
                                actualX - cos(rad) * sparkleSize,
                                actualY - sin(rad) * sparkleSize
                            ),
                            end = Offset(
                                actualX + cos(rad) * sparkleSize,
                                actualY + sin(rad) * sparkleSize
                            ),
                            strokeWidth = 1.5f
                        )
                    }
                }
            }
        }
    }
}
