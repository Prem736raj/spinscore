package com.spinbottle.truthdare.games.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.spinbottle.truthdare.games.audio.rememberSoundManager
import com.spinbottle.truthdare.games.audio.rememberHapticManager
import com.spinbottle.truthdare.games.data.*
import com.spinbottle.truthdare.games.game.SpinSelection
import com.spinbottle.truthdare.games.ui.components.PlayerCircle
import com.spinbottle.truthdare.games.ui.components.SpinningBottle
import com.spinbottle.truthdare.games.ui.components.InGameMenuSheet
import com.spinbottle.truthdare.games.ui.components.PauseOverlay
import com.spinbottle.truthdare.games.ui.components.TournamentScoreboard
import com.spinbottle.truthdare.games.ui.components.TournamentWinnerOverlay
import com.spinbottle.truthdare.games.ui.components.ChallengeLevelBanner
import com.spinbottle.truthdare.games.ui.components.LevelUpOverlay
import com.spinbottle.truthdare.games.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onBack: () -> Unit,
    onGameEnd: () -> Unit,
    onViewGallery: () -> Unit = {}
) {
    val soundManager = rememberSoundManager()
    val hapticManager = rememberHapticManager()
    var showExitDialog by remember { mutableStateOf(false) }
    var showGameMenu by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var showRemovePlayerDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }
    
    // Tournament state
    var showTournamentWinner by remember { mutableStateOf(false) }
    var tournamentWinner by remember { mutableStateOf<Player?>(null) }
    
    // Challenge Mode state
    var showLevelUp by remember { mutableStateOf(false) }
    var challengeLevel by remember { mutableIntStateOf(GameSessionHolder.currentChallengeLevel) }
    
    // Camera state for dare proof
    val context = LocalContext.current
    var pendingProofDare by remember { mutableStateOf("") }
    var pendingProofPlayer by remember { mutableStateOf<Player?>(null) }
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingPhotoFileName by remember { mutableStateOf<String?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val fileName = pendingPhotoFileName
        if (success && fileName != null && pendingProofPlayer != null) {
            DareProofManager.addProof(
                DareProof(
                    dareText = pendingProofDare,
                    playerName = pendingProofPlayer!!.name,
                    playerEmoji = pendingProofPlayer!!.avatar,
                    fileName = fileName
                )
            )
        } else {
            DareProofManager.deleteUntrackedFile(fileName)
        }
        pendingProofDare = ""
        pendingProofPlayer = null
        pendingPhotoUri = null
        pendingPhotoFileName = null
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val uri = pendingPhotoUri
        if (granted && uri != null) {
            runCatching { cameraLauncher.launch(uri) }
                .onFailure {
                    DareProofManager.deleteUntrackedFile(pendingPhotoFileName)
                    pendingPhotoUri = null
                    pendingPhotoFileName = null
                    pendingProofDare = ""
                    pendingProofPlayer = null
                }
        } else {
            DareProofManager.deleteUntrackedFile(pendingPhotoFileName)
            pendingPhotoUri = null
            pendingPhotoFileName = null
            pendingProofDare = ""
            pendingProofPlayer = null
        }
    }
    
    // Get players from the session holder
    val players = remember { GameSessionHolder.players }
    
    // If no players, show error state
    if (players.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("No players found. Please go back and add players.", color = TextWhite)
        }
        return
    }
    
    // Start game timer when screen opens
    LaunchedEffect(Unit) {
        if (GameSessionHolder.gameStartTime == 0L) {
            GameSessionHolder.startGame()
        }
    }
    
    var gameState by remember {
        mutableStateOf(
            GameState(
                players = players,
                difficulty = GameSessionHolder.difficulty
            )
        )
    }
    
    var currentPrompt by remember { mutableStateOf("") }
    var showTruthDareChoice by remember { mutableStateOf(false) }
    

    // Exit confirmation dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    "End Game?",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column {
                    Text(
                        "End the game and see final results?",
                        color = TextMuted
                    )
                    if (gameState.round > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "${gameState.round - 1} rounds played",
                            fontSize = 14.sp,
                            color = AccentPurple
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onGameEnd()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Text("See Results 🏆", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Keep Playing", color = TextMuted)
                }
            },
            containerColor = DarkCard
        )
    }
    
    // Truth or Dare choice dialog
    if (showTruthDareChoice && gameState.selectedPlayerIndex != null) {
        val selectedPlayer = gameState.players[gameState.selectedPlayerIndex!!]
        
        AlertDialog(
            onDismissRequest = { },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedPlayer.avatar,
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${selectedPlayer.name}'s Turn!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                }
            },
            text = {
                Text(
                    text = "Choose your fate...",
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Truth button
                    Button(
                        onClick = {
                            hapticManager.mediumTap()
                            soundManager.playTruthReveal()
                            currentPrompt = GamePrompts.getRandomTruth(gameState.difficulty)
                            gameState = gameState.copy(
                                currentPromptType = PromptType.TRUTH,
                                showPrompt = true
                            )
                            showTruthDareChoice = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TruthBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🤔 Truth", fontWeight = FontWeight.Bold)
                    }
                    
                    // Dare button
                    Button(
                        onClick = {
                            hapticManager.mediumTap()
                            soundManager.playDareReveal()
                            currentPrompt = GamePrompts.getRandomDare(gameState.difficulty)
                            gameState = gameState.copy(
                                currentPromptType = PromptType.DARE,
                                showPrompt = true
                            )
                            showTruthDareChoice = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DareOrange
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🔥 Dare", fontWeight = FontWeight.Bold)
                    }
                }
            },
            containerColor = DarkCard
        )
    }
    
    // Prompt display dialog
    if (gameState.showPrompt && gameState.selectedPlayerIndex != null) {
        val selectedPlayer = gameState.players[gameState.selectedPlayerIndex!!]
        val isTruth = gameState.currentPromptType == PromptType.TRUTH
        
        AlertDialog(
            onDismissRequest = { },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isTruth) TruthBlue else DareOrange
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isTruth) "🤔 TRUTH" else "🔥 DARE",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${selectedPlayer.name}:",
                        fontSize = 16.sp,
                        color = TextMuted
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentPrompt,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Favorite button
                    val isFavorite = remember(currentPrompt) { 
                        FavoritesManager.isFavorite(currentPrompt.removePrefix("✨ ").removePrefix("❤️ "))
                    }
                    var favoriteState by remember(currentPrompt) { mutableStateOf(isFavorite) }
                    
                    IconButton(
                        onClick = {
                            hapticManager.lightTap()
                            val cleanPrompt = currentPrompt.removePrefix("✨ ").removePrefix("❤️ ")
                            val promptType = if (gameState.currentPromptType == PromptType.TRUTH) 
                                PromptItemType.TRUTH else PromptItemType.DARE
                            favoriteState = FavoritesManager.toggleFavorite(cleanPrompt, promptType)
                        }
                    ) {
                        Icon(
                            imageVector = if (favoriteState) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (favoriteState) "Remove from favorites" else "Add to favorites",
                            tint = if (favoriteState) AccentPink else TextMuted,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    // Camera button for dares only
                    if (!isTruth) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(GlassWhite.copy(alpha = 0.1f))
                                .clickable {
                                    hapticManager.lightTap()
                                    // Create photo file and launch camera
                                    try {
                                        val photoFile = DareProofManager.createProofFile(context)
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            photoFile
                                        )
                                        pendingPhotoUri = uri
                                        pendingPhotoFileName = photoFile.name
                                        pendingProofDare = currentPrompt.removePrefix("✨ ").removePrefix("❤️ ")
                                        pendingProofPlayer = selectedPlayer
                                        // Request camera permission
                                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                    } catch (e: Exception) {
                                        // Camera not available
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("📸", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Capture Proof",
                                fontSize = 14.sp,
                                color = TextWhite
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Skip button
                    OutlinedButton(
                        onClick = {
                            hapticManager.lightTap()
                            soundManager.playSkip()
                            // Track skip
                            val selectedPlayer = gameState.players[gameState.selectedPlayerIndex!!]
                            GameSessionHolder.updatePlayerStats(selectedPlayer.id, false, false, true)
                            GameSessionHolder.incrementRound()
                            
                            // Move to next round
                            gameState = gameState.copy(
                                showPrompt = false,
                                selectedPlayerIndex = null,
                                currentSpinnerIndex = (gameState.currentSpinnerIndex + 1) % gameState.players.size,
                                round = gameState.round + 1
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Skip", color = SkipRed)
                    }
                    
                    // Done button
                    Button(
                        onClick = {
                            hapticManager.successPattern()
                            soundManager.playSuccess()
                            // Track completion
                            val selectedPlayer = gameState.players[gameState.selectedPlayerIndex!!]
                            val isTruth = gameState.currentPromptType == PromptType.TRUTH
                            GameSessionHolder.updatePlayerStats(
                                selectedPlayer.id, 
                                truthCompleted = isTruth, 
                                dareCompleted = !isTruth,
                                skipped = false
                            )
                            GameSessionHolder.incrementRound()
                            
                            // Check for tournament winner
                            if (GameSessionHolder.isTournament && GameSessionHolder.hasWinner()) {
                                tournamentWinner = GameSessionHolder.getWinner()
                                showTournamentWinner = true
                            }
                            
                            // Check for Challenge Mode level-up
                            if (GameSessionHolder.gameMode == GameMode.CHALLENGE) {
                                if (GameSessionHolder.advanceChallengeRound()) {
                                    challengeLevel = GameSessionHolder.currentChallengeLevel
                                    showLevelUp = true
                                }
                            }
                            
                            gameState = gameState.copy(
                                showPrompt = false,
                                selectedPlayerIndex = null,
                                currentSpinnerIndex = (gameState.currentSpinnerIndex + 1) % gameState.players.size,
                                round = gameState.round + 1
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done ✓", fontWeight = FontWeight.Bold)
                    }
                }
            },
            containerColor = DarkCard
        )
    }
    
    // Get selected theme
    val selectedBackground = remember { ThemeManager.selectedBackground }
    val selectedBottle = remember { ThemeManager.selectedBottle }
    
    // Main game screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = selectedBackground.colors
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
                
                // Round counter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassWhite.copy(alpha = 0.1f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Round ${gameState.round}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                }
                
                // Current spinner indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            gameState.players.getOrNull(gameState.currentSpinnerIndex)?.color?.copy(alpha = 0.3f) 
                                ?: GlassWhite.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = gameState.players.getOrNull(gameState.currentSpinnerIndex)?.avatar ?: "?",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "spins",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
                
                // Menu button
                IconButton(
                    onClick = { showGameMenu = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = TextWhite
                    )
                }
            }
            
            // Tournament scoreboard (if tournament mode enabled)
            if (GameSessionHolder.isTournament) {
                TournamentScoreboard(
                    players = GameSessionHolder.players,
                    targetScore = GameSessionHolder.targetScore,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Challenge Mode level banner
            if (GameSessionHolder.gameMode == GameMode.CHALLENGE) {
                ChallengeLevelBanner(
                    currentLevel = challengeLevel,
                    roundsUntilLevelUp = GameSessionHolder.getRoundsUntilLevelUp(),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Game area - Player circle with bottle in center
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Player circle
                PlayerCircle(
                    players = gameState.players,
                    selectedPlayerIndex = gameState.selectedPlayerIndex
                )
                
                // Spinning bottle in center
                SpinningBottle(
                    onSpinComplete = { rotation ->
                        val selectedIndex = SpinSelection.playerIndexForRotation(
                            rotation = rotation,
                            playerCount = gameState.players.size
                        )
                        gameState = gameState.copy(
                            selectedPlayerIndex = selectedIndex
                        )
                        showTruthDareChoice = true
                    },
                    onTapToSpin = {
                        gameState = gameState.copy(
                            selectedPlayerIndex = null
                        )
                    },
                    soundManager = soundManager
                )
            }
            
            // Bottom instruction
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val currentSpinner = gameState.players.getOrNull(gameState.currentSpinnerIndex)
                Text(
                    text = "${currentSpinner?.name ?: "Someone"}, tap the bottle to spin!",
                    fontSize = 16.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Pause overlay
        PauseOverlay(
            isPaused = isPaused,
            onResume = { isPaused = false }
        )
    }
    
    // In-game menu bottom sheet
    InGameMenuSheet(
        isVisible = showGameMenu,
        isPaused = isPaused,
        proofCount = DareProofManager.getProofCount(),
        onDismiss = { showGameMenu = false },
        onPauseToggle = { isPaused = !isPaused },
        onAddPlayer = { showAddPlayerDialog = true },
        onRemovePlayer = { showRemovePlayerDialog = true },
        onChangeDifficulty = { showDifficultyDialog = true },
        onViewGallery = onViewGallery,
        onEndGame = { showExitDialog = true }
    )
    
    // Add player dialog
    if (showAddPlayerDialog) {
        AddPlayerDialog(
            usedEmojis = gameState.players.map { it.avatar }.toSet(),
            onAddPlayer = { name, avatar ->
                val newPlayer = Player(
                    name = name,
                    avatar = avatar,
                    color = PlayerColors.random()
                )
                GameSessionHolder.players = GameSessionHolder.players + newPlayer
                gameState = gameState.copy(
                    players = GameSessionHolder.players
                )
                showAddPlayerDialog = false
            },
            onDismiss = { showAddPlayerDialog = false }
        )
    }
    
    // Remove player dialog
    if (showRemovePlayerDialog) {
        RemovePlayerDialog(
            players = gameState.players,
            onRemovePlayer = { player ->
                GameSessionHolder.players = GameSessionHolder.players.filter { it.id != player.id }
                gameState = gameState.copy(
                    players = GameSessionHolder.players,
                    currentSpinnerIndex = if (gameState.currentSpinnerIndex >= GameSessionHolder.players.size) 
                        0 else gameState.currentSpinnerIndex
                )
                showRemovePlayerDialog = false
            },
            onDismiss = { showRemovePlayerDialog = false }
        )
    }
    
    // Change difficulty dialog
    if (showDifficultyDialog) {
        DifficultyPickerDialog(
            currentDifficulty = gameState.difficulty,
            onSelectDifficulty = { difficulty ->
                GameSessionHolder.difficulty = difficulty
                gameState = gameState.copy(difficulty = difficulty)
                showDifficultyDialog = false
            },
            onDismiss = { showDifficultyDialog = false }
        )
    }
    
    // Tournament winner overlay
    if (showTournamentWinner && tournamentWinner != null) {
        TournamentWinnerOverlay(
            winner = tournamentWinner!!,
            onContinue = {
                showTournamentWinner = false
                onGameEnd()
            }
        )
    }
    
    // Challenge Mode level up overlay
    if (showLevelUp) {
        LevelUpOverlay(
            newLevel = challengeLevel,
            onDismiss = { showLevelUp = false }
        )
    }
}

@Composable
fun AddPlayerDialog(
    usedEmojis: Set<String>,
    onAddPlayer: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Player", fontWeight = FontWeight.Bold, color = TextWhite)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 20) name = it },
                    placeholder = { Text("Player name", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Select avatar:", color = TextMuted, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                // Simple emoji row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val availableEmojis = listOf("🐶", "🐱", "🦊", "🐼", "🐸", "🦁").filter { it !in usedEmojis }
                    availableEmojis.take(6).forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedEmoji == emoji) AccentPurple.copy(alpha = 0.3f)
                                    else GlassWhite.copy(alpha = 0.1f)
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 24.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (name.isNotBlank()) {
                        onAddPlayer(name, selectedEmoji ?: com.spinbottle.truthdare.games.data.Avatars.randomExcluding(usedEmojis))
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Text("Add", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        },
        containerColor = DarkCard
    )
}

@Composable
fun RemovePlayerDialog(
    players: List<Player>,
    onRemovePlayer: (Player) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Remove Player", fontWeight = FontWeight.Bold, color = TextWhite)
        },
        text = {
            Column {
                if (players.size <= 2) {
                    Text(
                        "Need at least 2 players to continue!",
                        color = AccentOrange,
                        fontSize = 14.sp
                    )
                } else {
                    Text("Tap a player to remove:", color = TextMuted, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    players.forEach { player ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onRemovePlayer(player) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(player.color.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(player.avatar, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(player.name, color = TextWhite, fontSize = 16.sp)
                        }
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        },
        containerColor = DarkCard
    )
}

@Composable
fun DifficultyPickerDialog(
    currentDifficulty: Difficulty,
    onSelectDifficulty: (Difficulty) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Change Difficulty", fontWeight = FontWeight.Bold, color = TextWhite)
        },
        text = {
            Column {
                Difficulty.entries.filter { !it.requiresPin }.forEach { difficulty ->
                    val isSelected = difficulty == currentDifficulty
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) difficulty.color.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .clickable { onSelectDifficulty(difficulty) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(difficulty.emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                difficulty.displayName,
                                color = if (isSelected) difficulty.color else TextWhite,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                difficulty.description,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        },
        containerColor = DarkCard
    )
}
