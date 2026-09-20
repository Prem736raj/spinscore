package com.spinbottle.truthdare.games.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.audio.rememberHapticManager
import com.spinbottle.truthdare.games.audio.rememberSoundManager
import com.spinbottle.truthdare.games.data.*
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

// Romantic color palette
val RomanticPink = Color(0xFFE91E63)
val RomanticRose = Color(0xFFFF4081)
val RomanticDark = Color(0xFF1A0A10)
val RomanticDarkSecondary = Color(0xFF2D1420)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouplesGameScreen(
    onBack: () -> Unit,
    onGameComplete: () -> Unit
) {
    val soundManager = rememberSoundManager()
    val hapticManager = rememberHapticManager()
    
    val players = remember { GameSessionHolder.players }
    val intimacyLevel = remember {
        mutableIntStateOf(GameSessionHolder.couplesIntimacyLevel)
    }
    
    var currentPlayerIndex by remember {
        mutableIntStateOf(
            GameSessionHolder.currentPlayerIndex
                .coerceIn(0, (players.size - 1).coerceAtLeast(0))
        )
    }
    var currentPrompt by remember { mutableStateOf("") }
    var promptType by remember { mutableStateOf<PromptType?>(null) }
    var round by remember { mutableIntStateOf(GameSessionHolder.totalRounds + 1) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showPrompt by remember { mutableStateOf(false) }
    var showIntimacySelector by remember { mutableStateOf(true) }
    
    
    if (players.size != 2) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RomanticDark)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Couples Mode requires exactly 2 players.",
                    color = TextWhite,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Back to setup")
                }
            }
        }
        return
    }

    // Floating hearts animation
    val hearts = remember { 
        List(15) { 
            FloatingHeart(
                x = Random.nextFloat(),
                delay = Random.nextInt(5000),
                duration = 3000 + Random.nextInt(2000)
            )
        }
    }
    
    // Start game
    LaunchedEffect(Unit) {
        if (GameSessionHolder.gameStartTime == 0L) {
            GameSessionHolder.startGame()
        }
    }
    
    // Check if game should end (after 5 rounds minimum)
    if (round > 10) {
        LaunchedEffect(Unit) {
            onGameComplete()
        }
        return
    }
    
    val currentPlayer = players.getOrNull(currentPlayerIndex) ?: return
    val partnerPlayer = players.getOrNull((currentPlayerIndex + 1) % players.size) ?: return
    
    // Exit dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("End Date Night?", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text("Your romantic session will end.", color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = { onBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = RomanticPink)
                ) { Text("End") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitDialog = false }) {
                    Text("Continue 💕", color = TextWhite)
                }
            },
            containerColor = RomanticDark
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(RomanticDark, RomanticDarkSecondary, RomanticDark)
                )
            )
    ) {
        // Floating hearts background
        hearts.forEach { heart ->
            FloatingHeartAnimation(heart)
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar with romantic theme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(RomanticPink.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit game",
                        tint = TextWhite
                    )
                }
                
                // Romantic badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(RomanticPink, RomanticRose)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Date Night",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                // Round
                Text(
                    text = "💕 $round",
                    fontSize = 14.sp,
                    color = RomanticRose
                )
            }
            
            if (showIntimacySelector) {
                // Intimacy Level Selector
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "💕",
                        fontSize = 64.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Choose Your Intimacy Level",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = when (intimacyLevel.intValue) {
                            1 -> "Sweet & Innocent"
                            2 -> "Getting Closer"
                            3 -> "Personal & Deep"
                            4 -> "Intimate & Romantic"
                            5 -> "🔥 Spicy & Flirty"
                            else -> ""
                        },
                        fontSize = 16.sp,
                        color = RomanticRose
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Slider
                    Slider(
                        value = intimacyLevel.intValue.toFloat(),
                        onValueChange = {
                            val level = it.toInt().coerceIn(1, 5)
                            intimacyLevel.intValue = level
                            GameSessionHolder.couplesIntimacyLevel = level
                        },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = RomanticPink,
                            activeTrackColor = RomanticRose,
                            inactiveTrackColor = RomanticPink.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    
                    // Level indicators
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (1..5).forEach { level ->
                            Text(
                                text = "$level",
                                fontSize = 12.sp,
                                color = if (level == intimacyLevel.intValue) RomanticRose else TextMuted
                            )
                        }
                    }
                    
                    if (intimacyLevel.intValue == 5) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "🔒 Requires PIN",
                            fontSize = 12.sp,
                            color = RomanticRose
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Button(
                        onClick = { showIntimacySelector = false },
                        colors = ButtonDefaults.buttonColors(containerColor = RomanticPink),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                    ) {
                        Text(
                            text = "Start Date Night 💕",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            } else if (!showPrompt) {
                // Player turn display
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Current player
                    Text(
                        text = currentPlayer.avatar,
                        fontSize = 80.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "${currentPlayer.name}'s turn",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    
                    Text(
                        text = "for ${partnerPlayer.name} 💕",
                        fontSize = 16.sp,
                        color = RomanticRose
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Truth/Dare buttons - romantic style
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Button(
                            onClick = {
                                hapticManager.mediumTap()
                                soundManager.playTruthReveal()
                                currentPrompt = getCouplesPrompt(intimacyLevel.intValue, PromptType.TRUTH)
                                promptType = PromptType.TRUTH
                                showPrompt = true
                            },
                            modifier = Modifier.size(130.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanticPink.copy(alpha = 0.8f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💭", fontSize = 36.sp)
                                Text("Truth", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        
                        Button(
                            onClick = {
                                hapticManager.mediumTap()
                                soundManager.playDareReveal()
                                currentPrompt = getCouplesPrompt(intimacyLevel.intValue, PromptType.DARE)
                                promptType = PromptType.DARE
                                showPrompt = true
                            },
                            modifier = Modifier.size(130.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanticRose
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💋", fontSize = 36.sp)
                                Text("Dare", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            } else {
                // Show romantic prompt
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = RomanticPink.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (promptType == PromptType.TRUTH) "💭 Truth" else "💋 Dare",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = RomanticRose
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Text(
                                text = currentPrompt,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp
                            )
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Button(
                                onClick = {
                                    hapticManager.successPattern()
                                    soundManager.playSuccess()
                                    
                                    val isTruth = promptType == PromptType.TRUTH
                                    GameSessionHolder.updatePlayerStats(
                                        currentPlayer.id,
                                        truthCompleted = isTruth,
                                        dareCompleted = !isTruth,
                                        skipped = false
                                    )
                                    GameSessionHolder.incrementRound()
                                    
                                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size
                                    GameSessionHolder.currentPlayerIndex = currentPlayerIndex
                                    round++
                                    showPrompt = false
                                    currentPrompt = ""
                                    promptType = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RomanticPink),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Done 💕", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Floating heart data class
data class FloatingHeart(
    val x: Float,
    val delay: Int,
    val duration: Int
)

@Composable
fun FloatingHeartAnimation(heart: FloatingHeart) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = -0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(heart.duration, delayMillis = heart.delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "y"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = heart.duration
                0f at 0
                0.6f at heart.duration / 3
                0.6f at heart.duration * 2 / 3
                0f at heart.duration
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                translationX = heart.x * 400f,
                translationY = yOffset * 800f
            )
            .alpha(alpha)
    ) {
        Text(
            text = listOf("💕", "❤️", "💗", "💖").random(),
            fontSize = (16 + Random.nextInt(16)).sp
        )
    }
}

// Get couples-specific prompts based on intimacy
fun getCouplesPrompt(intimacyLevel: Int, type: PromptType): String {
    val prompts = when (type) {
        PromptType.TRUTH -> when (intimacyLevel) {
            1 -> listOf(
                "What was your first impression of your partner?",
                "What's your favorite thing about spending time together?",
                "What's a small thing your partner does that makes you smile?",
                "What's your favorite memory of us?",
                "What made you realize you liked me?"
            )
            2 -> listOf(
                "What's something you've never told me before?",
                "What's your favorite physical feature of mine?",
                "When do you feel most connected to me?",
                "What's a dream you want us to achieve together?",
                "What do you love most about our relationship?"
            )
            3 -> listOf(
                "What's your favorite romantic memory of us?",
                "What do you think makes our relationship special?",
                "What's something you want to improve in our relationship?",
                "When did you first know you loved me?",
                "What's a fear you have about our future?"
            )
            4 -> listOf(
                "What's your deepest desire in our relationship?",
                "What makes you feel most loved by me?",
                "What's a fantasy you've had about us?",
                "What moment made you feel closest to me?",
                "What vulnerability have you been afraid to share?"
            )
            else -> listOf(
                "What's your most intimate fantasy involving us?",
                "What's the most attractive thing I do without realizing?",
                "What would make our intimate life even better?",
                "What's something daring you want to try together?",
                "What's your favorite way to be affectionate?"
            )
        }
        PromptType.DARE -> when (intimacyLevel) {
            1 -> listOf(
                "Give your partner a 30-second hug",
                "Look into each other's eyes for 1 minute without talking",
                "Give your partner a genuine compliment",
                "Hold hands for the next round",
                "Tell your partner 3 things you appreciate about them"
            )
            2 -> listOf(
                "Give your partner a forehead kiss",
                "Give your partner a 2-minute shoulder massage",
                "Whisper something sweet in your partner's ear",
                "Dance together to an imaginary slow song",
                "Feed your partner something sweet"
            )
            3 -> listOf(
                "Give your partner a passionate kiss",
                "Write 'I love you' on your partner's hand with your finger",
                "Recreate your first kiss",
                "Give your partner a back massage for 3 minutes",
                "Slow dance together for 2 minutes"
            )
            4 -> listOf(
                "Give your partner a kiss on their neck",
                "Cup your partner's face and kiss them deeply",
                "Tell your partner exactly why you find them attractive",
                "Create a romantic moment right now",
                "Play with your partner's hair while looking into their eyes"
            )
            else -> listOf(
                "Give your partner the most passionate kiss you can",
                "Whisper your deepest desire in your partner's ear",
                "Create a moment that will make your partner blush",
                "Show your partner how much you desire them",
                "Do something that drives your partner wild"
            )
        }
    }
    return prompts.random()
}
