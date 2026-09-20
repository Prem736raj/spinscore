package com.spinbottle.truthdare.games.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.spinbottle.truthdare.games.audio.rememberHapticManager
import com.spinbottle.truthdare.games.audio.rememberSoundManager
import com.spinbottle.truthdare.games.data.*
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFireGameScreen(
    onBack: () -> Unit,
    onGameComplete: () -> Unit
) {
    val soundManager = rememberSoundManager()
    val hapticManager = rememberHapticManager()
    
    val players = remember { GameSessionHolder.players }
    val difficulty = remember { GameSessionHolder.difficulty }
    
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
    
    // Timer state
    var timeRemaining by remember {
        mutableIntStateOf(GameSessionHolder.quickFireSecondsRemaining)
    }
    var isTimerRunning by remember { mutableStateOf(false) }
    var showChoosePrompt by remember { mutableStateOf(true) }

    val lifecycleOwner = LocalLifecycleOwner.current
    var isResumed by remember {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> isResumed = true
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP -> isResumed = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler {
        showExitDialog = true
    }
    
    // Start game timer
    LaunchedEffect(Unit) {
        if (GameSessionHolder.gameStartTime == 0L) {
            GameSessionHolder.startGame()
        }
    }
    
    // Countdown timer
    LaunchedEffect(isTimerRunning, currentPlayerIndex, isResumed) {
        if (isTimerRunning && isResumed) {
            while (timeRemaining > 0 && isResumed) {
                delay(1000)
                if (!isResumed || !isTimerRunning) break
                timeRemaining--
                GameSessionHolder.quickFireSecondsRemaining = timeRemaining
                
                // Urgent haptic at 10, 5, 3, 2, 1 seconds
                if (timeRemaining <= 5) {
                    hapticManager.lightTap()
                }
            }
            
            // Time's up! Auto-skip
            if (timeRemaining == 0) {
                soundManager.playSkip()
                hapticManager.heavyTap()
                
                val currentPlayer = players[currentPlayerIndex]
                GameSessionHolder.updatePlayerStats(currentPlayer.id, false, false, true)
                GameSessionHolder.incrementRound()
                
                // Move to next player
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size
                GameSessionHolder.currentPlayerIndex = currentPlayerIndex
                round++
                timeRemaining = 30
                GameSessionHolder.quickFireSecondsRemaining = 30
                isTimerRunning = false
                showChoosePrompt = true
                currentPrompt = ""
                promptType = null
            }
        }
    }
    
    // Check if game should end (after 3 rounds per player minimum)
    if (round > players.size * 3 && currentPlayerIndex == 0) {
        LaunchedEffect(Unit) {
            onGameComplete()
        }
        return
    }
    
    val currentPlayer = players.getOrNull(currentPlayerIndex) ?: return
    
    // Timer color based on remaining time
    val timerColor = when {
        timeRemaining <= 5 -> SkipRed
        timeRemaining <= 10 -> DareOrange
        timeRemaining <= 20 -> DifficultyMedium
        else -> AccentGreen
    }
    
    // Background theme
    val selectedBackground = remember { ThemeManager.selectedBackground }
    
    // Exit dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("End Quick Fire?", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text("Your progress will be saved.", color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = { onBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = SkipRed)
                ) { Text("End Game") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitDialog = false }) {
                    Text("Continue", color = TextWhite)
                }
            },
            containerColor = DarkCard
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(selectedBackground.colors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar with Quick Fire badge
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
                        .background(GlassWhite)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = TextWhite
                    )
                }
                
                // Quick Fire Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(DareOrange, SkipRed)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "⚡ QUICK FIRE!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                // Round counter
                Text(
                    text = "Round $round",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }
            
            // Timer display - prominent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Animated timer
                val animatedSize by animateFloatAsState(
                    targetValue = if (timeRemaining <= 5 && isTimerRunning) 1.1f else 1f,
                    animationSpec = if (timeRemaining <= 5) {
                        infiniteRepeatable(
                            animation = tween(500),
                            repeatMode = RepeatMode.Reverse
                        )
                    } else spring(),
                    label = "timerPulse"
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer(scaleX = animatedSize, scaleY = animatedSize)
                ) {
                    Text(
                        text = if (isTimerRunning) "$timeRemaining" else "30",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTimerRunning) timerColor else TextMuted
                    )
                    Text(
                        text = "seconds",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Current player display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentPlayer.avatar,
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentPlayer.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Your turn!",
                        fontSize = 14.sp,
                        color = AccentTeal
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Game area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (showChoosePrompt) {
                    // Choose Truth or Dare
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Choose quickly!",
                            fontSize = 16.sp,
                            color = TextMuted
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    hapticManager.mediumTap()
                                    soundManager.playTruthReveal()
                                    currentPrompt = GamePrompts.getRandomTruth(difficulty)
                                    promptType = PromptType.TRUTH
                                    showChoosePrompt = false
                                    timeRemaining = 30
                                    isTimerRunning = true
                                },
                                modifier = Modifier.size(120.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TruthBlue),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🤔", fontSize = 32.sp)
                                    Text("TRUTH", fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Button(
                                onClick = {
                                    hapticManager.mediumTap()
                                    soundManager.playDareReveal()
                                    currentPrompt = GamePrompts.getRandomDare(difficulty)
                                    promptType = PromptType.DARE
                                    showChoosePrompt = false
                                    timeRemaining = 30
                                    isTimerRunning = true
                                },
                                modifier = Modifier.size(120.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DareOrange),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🔥", fontSize = 32.sp)
                                    Text("DARE", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Show prompt with timer
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (promptType == PromptType.TRUTH) 
                                TruthBlue.copy(alpha = 0.2f) else DareOrange.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (promptType == PromptType.TRUTH) "🤔 TRUTH" else "🔥 DARE",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (promptType == PromptType.TRUTH) TruthBlue else DareOrange
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = currentPrompt,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        hapticManager.lightTap()
                                        soundManager.playSkip()
                                        isTimerRunning = false
                                        
                                        GameSessionHolder.updatePlayerStats(
                                            currentPlayer.id, false, false, true
                                        )
                                        GameSessionHolder.incrementRound()
                                        
                                        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
                                        GameSessionHolder.currentPlayerIndex = currentPlayerIndex
                                        round++
                                        timeRemaining = 30
                                        GameSessionHolder.quickFireSecondsRemaining = 30
                                        showChoosePrompt = true
                                        currentPrompt = ""
                                        promptType = null
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Skip", color = SkipRed)
                                }
                                
                                Button(
                                    onClick = {
                                        hapticManager.successPattern()
                                        soundManager.playSuccess()
                                        isTimerRunning = false
                                        
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
                                        timeRemaining = 30
                                        GameSessionHolder.quickFireSecondsRemaining = 30
                                        showChoosePrompt = true
                                        currentPrompt = ""
                                        promptType = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Done! ✓", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            
            // Player rotation preview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Next: ", fontSize = 12.sp, color = TextMuted)
                val nextPlayer = players[(currentPlayerIndex + 1) % players.size]
                Text(
                    text = "${nextPlayer.avatar} ${nextPlayer.name}",
                    fontSize = 12.sp,
                    color = TextWhite
                )
            }
        }
    }
}
