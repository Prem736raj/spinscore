package com.spinbottle.truthdare.games.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.data.GameMode
import com.spinbottle.truthdare.games.data.PinManager
import com.spinbottle.truthdare.games.ui.components.ModeCard
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun ModeSelectionScreen(
    onBack: () -> Unit,
    onModeSelected: () -> Unit
) {
    val context = LocalContext.current
    val pinManager = remember { PinManager(context) }
    val scope = rememberCoroutineScope()
    
    var selectedMode by remember { mutableStateOf<GameMode?>(null) }
    var showAgeDialog by remember { mutableStateOf(false) }
    var showPinScreen by remember { mutableStateOf(false) }
    var pinScreenMode by remember { mutableStateOf(PinScreenMode.SETUP) }
    var pendingMode by remember { mutableStateOf<GameMode?>(null) }
    
    // Age verification dialog
    if (showAgeDialog) {
        AgeVerificationDialog(
            onConfirm = {
                showAgeDialog = false
                scope.launch {
                    val isPinSet = pinManager.isPinSet.first()
                    pinManager.setAgeVerified(true)
                    pinScreenMode = if (isPinSet) {
                        PinScreenMode.VERIFY
                    } else {
                        PinScreenMode.SETUP
                    }
                    showPinScreen = true
                }
            },
            onDismiss = {
                showAgeDialog = false
                pendingMode = null
            }
        )
    }
    
    // PIN screen overlay
    if (showPinScreen) {
        PinScreen(
            mode = pinScreenMode,
            onBack = {
                showPinScreen = false
                pendingMode = null
            },
            onSuccess = {
                showPinScreen = false
                selectedMode = pendingMode
                // Update game session with selected mode
                pendingMode?.let { com.spinbottle.truthdare.games.data.GameSessionHolder.gameMode = it }
                pendingMode = null
            },
            markAgeVerifiedOnSetup = pendingMode != GameMode.KIDS_SAFE
        )
        return
    }
    
    fun handleModeSelect(mode: GameMode) {
        when {
            mode == GameMode.KIDS_SAFE -> {
                pendingMode = mode
                scope.launch {
                    val isPinSet = pinManager.isPinSet.first()
                    if (isPinSet) {
                        selectedMode = mode
                        com.spinbottle.truthdare.games.data.GameSessionHolder.gameMode = mode
                        pendingMode = null
                    } else {
                        // Establish a parent PIN before entering a mode whose exit is PIN-gated.
                        pinScreenMode = PinScreenMode.SETUP
                        showPinScreen = true
                    }
                }
            }

            mode.requiresPin -> {
                pendingMode = mode
                scope.launch {
                    val isPinSet = pinManager.isPinSet.first()
                    val isAgeVerified = pinManager.isAgeVerified.first()

                    when {
                        !isAgeVerified -> showAgeDialog = true
                        isPinSet -> {
                            pinScreenMode = PinScreenMode.VERIFY
                            showPinScreen = true
                        }
                        else -> {
                            // Recover cleanly if age state exists but the PIN was cleared.
                            pinScreenMode = PinScreenMode.SETUP
                            showPinScreen = true
                        }
                    }
                }
            }

            else -> {
                selectedMode = mode
                // Update game session with selected mode
                com.spinbottle.truthdare.games.data.GameSessionHolder.gameMode = mode
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkBackgroundSecondary, DarkBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Choose Game Mode",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Select how you want to play",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            }
            
            // Mode cards
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Defense in depth: a Kids Safe flow may only select the dedicated
                // Kids Safe mode. Do not rely on PIN flags or UI hiding alone.
                val isKidsModeFlow =
                    com.spinbottle.truthdare.games.data.GameSessionHolder.isKidsModeFlow

                val availableModes = if (isKidsModeFlow) {
                    listOf(GameMode.KIDS_SAFE)
                } else {
                    GameMode.values().toList()
                }
                
                availableModes.forEach { mode ->
                    ModeCard(
                        mode = mode,
                        isSelected = selectedMode == mode,
                        onClick = { handleModeSelect(mode) }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Bottom section
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Next button
                Button(
                    onClick = onModeSelected,
                    enabled = selectedMode != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(30.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (selectedMode != null) {
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            selectedMode!!.color,
                                            selectedMode!!.color.copy(alpha = 0.7f)
                                        )
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            AccentPurple.copy(alpha = 0.3f),
                                            AccentPink.copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (selectedMode != null) "Choose Difficulty" else "Select a Mode",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMode != null) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                            if (selectedMode != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
