package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.spinbottle.truthdare.games.ui.theme.*
import kotlin.math.abs
import kotlin.random.Random

data class ConfettiParticle(
    val xFraction: Float,
    val yStartFraction: Float,
    val color: Color,
    val size: Float,
    val driftFraction: Float
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

    val particles = remember(isPlaying) {
        if (!isPlaying) {
            emptyList()
        } else {
            List(150) {
                ConfettiParticle(
                    xFraction = Random.nextFloat(),
                    yStartFraction = -0.35f * Random.nextFloat(),
                    color = colors.random(),
                    size = Random.nextFloat() * 12f + 6f,
                    driftFraction = (Random.nextFloat() - 0.5f) * 0.35f
                )
            }
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 4_000,
                    easing = LinearEasing
                )
            )
        } else {
            progress.snapTo(0f)
        }
    }

    if (isPlaying) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val animationProgress = progress.value

            particles.forEach { particle ->
                val rawX = particle.xFraction + particle.driftFraction * animationProgress
                val wrappedX = rawX - kotlin.math.floor(rawX.toDouble()).toFloat()
                val x = wrappedX * size.width
                val y = (particle.yStartFraction + animationProgress * 1.45f) * size.height

                if (y in -50f..(size.height + 50f)) {
                    val fade = (1f - abs(animationProgress - 0.55f) * 0.6f)
                        .coerceIn(0.45f, 1f)
                    drawCircle(
                        color = particle.color.copy(alpha = fade),
                        radius = particle.size,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}
