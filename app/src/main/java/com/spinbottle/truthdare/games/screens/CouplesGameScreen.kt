package com.spinbottle.truthdare.games.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spinbottle.truthdare.games.couples.CouplesPreferences
import com.spinbottle.truthdare.games.couples.CouplesSessionRepository
import com.spinbottle.truthdare.games.couples.CouplesSessionState
import com.spinbottle.truthdare.games.couples.IntimacyTier
import com.spinbottle.truthdare.games.data.GameSessionHolder
import com.spinbottle.truthdare.games.screens.couples.CouplesGameViewModel
import com.spinbottle.truthdare.games.ui.components.*
import com.spinbottle.truthdare.games.ui.theme.*
import kotlin.random.Random

val RomanticPink = Color(0xFFE91E63)
val RomanticDark = Color(0xFF1A0A10)
val RomanticDarkSecondary = Color(0xFF2D1420)

@Composable
fun CouplesGameScreen(
    onBack: () -> Unit,
    onGameComplete: () -> Unit,
    preferences: CouplesPreferences = CouplesPreferences(),
    selectedPackIds: Set<String> = setOf("date_night", "deep_connection", "flirty", "affection"),
    viewModel: CouplesGameViewModel = viewModel()
) {
    val players = GameSessionHolder.players

    // Defensive check: exactly 2 players required
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

    val playerA = players[0]
    val playerB = players[1]

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    // Initialize domain session once
    LaunchedEffect(playerA.id, playerB.id) {
        val existing = CouplesSessionRepository.loadSession()
        val sessionToUse = if (existing != null && existing.playerAId == playerA.id && existing.playerBId == playerB.id) {
            existing
        } else {
            CouplesSessionState(
                playerAId = playerA.id,
                playerBId = playerB.id,
                preferences = preferences,
                selectedPackIds = selectedPackIds,
                startedAtEpochMs = System.currentTimeMillis()
            )
        }
        viewModel.initialize(sessionToUse)
    }

    val uiState by viewModel.uiState.collectAsState()
    val currentPrompt = uiState.prompt
    val session = uiState.session ?: return

    val scrollState = rememberScrollState()

    // Exit Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("End Romantic Session?", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text("Your couples progress will be wrapped up.", color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onGameComplete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RomanticPink)
                ) { Text("End Session") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitDialog = false }) {
                    Text("Continue Playing 💕", color = TextWhite)
                }
            },
            containerColor = DarkCard
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(RomanticDark, RomanticDarkSecondary, RomanticDark)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        contentDescription = "Close",
                        tint = TextWhite
                    )
                }

                currentPrompt?.let {
                    IntimacyMeter(currentTier = it.tier)
                } ?: Spacer(modifier = Modifier.width(48.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentPink.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Round ${session.round}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Two-player header
            AdaptiveTwoPlayerHeader(
                playerA = playerA,
                playerB = playerB,
                activePlayerIndex = session.currentPlayerIndex
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Prompt Card
            if (currentPrompt != null) {
                PromptCard(prompt = currentPrompt)

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Complete prompt button
                    GlassButton(
                        text = "Complete 💕 (+1)",
                        onClick = { viewModel.completePrompt() },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ConsentSkipButton(
                            onSkip = { viewModel.skipPrompt() },
                            onBlockTag = { tag -> viewModel.blockTagAndSkip(tag) },
                            promptTags = currentPrompt.tags
                        )

                        // Privacy Guard: Hide photo proof camera completely for AFTER_DARK prompts
                        if (currentPrompt.tier != IntimacyTier.AFTER_DARK) {
                            IconButton(
                                onClick = { /* Dare proof camera launcher hook if user desires */ },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(DarkCard)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Capture dare photo",
                                    tint = AccentTeal
                                )
                            }
                        }
                    }
                }
            } else {
                // Empty state when comfort filters exclude everything
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🕯️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.emptyStateMessage ?: "No prompts match your current comfort settings.",
                            fontSize = 16.sp,
                            color = TextWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        GlassButton(
                            text = "Wrap Up Session",
                            onClick = onGameComplete,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
