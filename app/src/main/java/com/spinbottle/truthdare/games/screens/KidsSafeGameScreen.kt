package com.spinbottle.truthdare.games.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.audio.rememberHapticManager
import com.spinbottle.truthdare.games.audio.rememberSoundManager
import com.spinbottle.truthdare.games.data.*
import com.spinbottle.truthdare.games.ui.theme.*
import kotlin.random.Random

// Kids mode colors - bright and cheerful
val KidsYellow = Color(0xFFFFEB3B)
val KidsOrange = Color(0xFFFF9800)
val KidsBlue = Color(0xFF2196F3)
val KidsPurple = Color(0xFF9C27B0)
val KidsGreen = Color(0xFF4CAF50)
val KidsPink = Color(0xFFE91E63)
val KidsBackground = Color(0xFF1A237E) // Dark blue for contrast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsSafeGameScreen(
    onExitWithPin: () -> Unit,
    onGameComplete: () -> Unit
) {
    val soundManager = rememberSoundManager()
    val hapticManager = rememberHapticManager()
    val players = remember { GameSessionHolder.players }
    
    var currentPlayerIndex by remember {
        mutableIntStateOf(
            if (players.isNotEmpty()) GameSessionHolder.totalRounds % players.size else 0
        )
    }
    var currentPrompt by remember { mutableStateOf("") }
    var promptType by remember { mutableStateOf<PromptType?>(null) }
    var round by remember { mutableIntStateOf(GameSessionHolder.totalRounds + 1) }
    var showPrompt by remember { mutableStateOf(false) }
    var showPinScreen by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }

    BackHandler {
        showPinScreen = true
    }
    
    // Fun bouncing animation
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    
    // Start game
    LaunchedEffect(Unit) {
        if (GameSessionHolder.gameStartTime == 0L) {
            GameSessionHolder.startGame()
        }
    }
    
    // Check if game should end (after 5 rounds per player)
    if (round > players.size * 5) {
        LaunchedEffect(Unit) {
            onGameComplete()
        }
        return
    }
    
    // PIN screen for parental exit
    if (showPinScreen) {
        PinScreen(
            mode = PinScreenMode.VERIFY,
            onBack = { showPinScreen = false },
            onSuccess = {
                showPinScreen = false
                showExitConfirm = true
            },
            allowSetupWhenMissing = false
        )
        return
    }
    
    // Exit confirmation dialog
    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text("Parent Menu", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text("Would you like to exit Kids Mode?", color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = { onExitWithPin() },
                    colors = ButtonDefaults.buttonColors(containerColor = KidsGreen)
                ) { Text("Exit Kids Mode") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitConfirm = false }) {
                    Text("Continue Playing", color = TextWhite)
                }
            },
            containerColor = DarkCard
        )
    }
    
    val currentPlayer = players.getOrNull(currentPlayerIndex) ?: return
    
    // Fun background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        KidsBackground,
                        Color(0xFF303F9F),
                        KidsBackground
                    )
                )
            )
    ) {
        // Floating emoji decorations
        FloatingEmojis()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar - simplified, only parent button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Kids Mode badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(KidsGreen, KidsBlue)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🎈 Kids Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                // Parental control button (small, discreet)
                IconButton(
                    onClick = { showPinScreen = true },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Parent Controls",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Round indicator - fun style
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⭐ Round $round ⭐",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsYellow
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (!showPrompt) {
                // Player turn display - big and fun!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big emoji avatar with bounce
                    Text(
                        text = currentPlayer.avatar,
                        fontSize = 120.sp,
                        modifier = Modifier.scale(bounce)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "${currentPlayer.name}'s Turn!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Big colorful buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Truth button - big and blue
                        Button(
                            onClick = {
                                hapticManager.mediumTap()
                                soundManager.playTruthReveal()
                                currentPrompt = getKidsSafePrompt(PromptType.TRUTH)
                                promptType = PromptType.TRUTH
                                showPrompt = true
                            },
                            modifier = Modifier
                                .size(140.dp)
                                .scale(bounce),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KidsBlue
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🤔", fontSize = 48.sp)
                                Text(
                                    "Truth",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                        
                        // Dare button - big and orange
                        Button(
                            onClick = {
                                hapticManager.mediumTap()
                                soundManager.playDareReveal()
                                currentPrompt = getKidsSafePrompt(PromptType.DARE)
                                promptType = PromptType.DARE
                                showPrompt = true
                            },
                            modifier = Modifier
                                .size(140.dp)
                                .scale(bounce),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KidsOrange
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎯", fontSize = 48.sp)
                                Text(
                                    "Dare",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Show kid-friendly prompt
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
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (promptType == PromptType.TRUTH) "🤔 Truth" else "🎯 Dare",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (promptType == PromptType.TRUTH) KidsBlue else KidsOrange
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = currentPrompt,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 32.sp
                            )
                            
                            Spacer(modifier = Modifier.height(40.dp))
                            
                            // Big "Done" button
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
                                    round++
                                    showPrompt = false
                                    currentPrompt = ""
                                    promptType = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = KidsGreen),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                            ) {
                                Text(
                                    "Done! ✓",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next player preview
            val nextPlayer = players[(currentPlayerIndex + 1) % players.size]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next up: ${nextPlayer.avatar} ${nextPlayer.name}",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun FloatingEmojis() {
    val emojis = listOf("🎈", "⭐", "🌟", "🎉", "🎊", "🌈", "🦄", "🎪")
    
    repeat(8) { index ->
        val infiniteTransition = rememberInfiniteTransition(label = "emoji$index")
        val yOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -30f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000 + index * 200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "y$index"
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = (index * 50 + 20).dp,
                    y = (100 + index * 80 + yOffset).dp
                )
        ) {
            Text(
                text = emojis[index % emojis.size],
                fontSize = 24.sp,
                modifier = Modifier.alpha(0.3f)
            )
        }
    }
}

// Kid-safe prompts only
fun getKidsSafePrompt(type: PromptType): String {
    val prompts = when (type) {
        PromptType.TRUTH -> listOf(
            "What's your favorite animal and why?",
            "What's the funniest thing that happened to you this week?",
            "If you could have any superpower, what would it be?",
            "What's your favorite food?",
            "What do you want to be when you grow up?",
            "What's your favorite game to play?",
            "Who is your best friend and why?",
            "What's your favorite movie?",
            "If you could visit anywhere, where would you go?",
            "What makes you happy?",
            "What's your favorite color and why?",
            "What's the nicest thing someone did for you?",
            "What's your favorite subject in school?",
            "What's your favorite TV show?"
        )
        PromptType.DARE -> listOf(
            "Do your best animal impression!",
            "Dance like nobody's watching for 30 seconds!",
            "Sing your favorite song!",
            "Do 5 jumping jacks!",
            "Make a silly face and hold it for 10 seconds!",
            "Tell a joke!",
            "Pretend to move in slow motion for 10 seconds!",
            "Act like your favorite animal!",
            "Give everyone a high five!",
            "Do your best superhero pose!",
            "Hop on one foot for 10 seconds!",
            "Make everyone laugh!",
            "Do a funny walk across the room!",
            "Pretend you're a robot!"
        )
    }
    return prompts.random()
}
