package com.spinbottle.truthdare.games.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.data.Avatars
import com.spinbottle.truthdare.games.data.GameSessionHolder
import com.spinbottle.truthdare.games.data.Player
import com.spinbottle.truthdare.games.data.PlayerColors
import com.spinbottle.truthdare.games.data.PlayerProfileManager
import com.spinbottle.truthdare.games.data.AvatarCategories
import com.spinbottle.truthdare.games.ui.components.EmojiPickerDialog
import com.spinbottle.truthdare.games.ui.components.PlayerCard
import com.spinbottle.truthdare.games.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSetupScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var players by remember { mutableStateOf(listOf<Player>()) }
    var selectedEmoji by remember { mutableStateOf<String?>(null) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var welcomeBackMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    
    val canContinue = players.size >= 2
    val canAddMore = players.size < 16
    
    // Track used emojis
    val usedEmojis = remember(players) { 
        players.map { it.avatar }.toSet() 
    }
    
    // Auto-dismiss welcome message
    LaunchedEffect(welcomeBackMessage) {
        if (welcomeBackMessage != null) {
            kotlinx.coroutines.delay(3000)
            welcomeBackMessage = null
        }
    }
    
    // Save players when navigating
    fun onNextClicked() {
        GameSessionHolder.players = players
        onNext()
    }
    
    fun addPlayer() {
        val name = playerName.trim()
        val duplicateName = players.any { it.name.equals(name, ignoreCase = true) }
        if (duplicateName) {
            welcomeBackMessage = "That player name is already in this game."
            return
        }
        if (name.isNotEmpty() && canAddMore) {
            // Check if returning player
            val existingProfile = PlayerProfileManager.getProfile(name)
            val avatar: String
            
            if (existingProfile != null) {
                // Returning player - use their saved avatar
                avatar = if (selectedEmoji != null) selectedEmoji!! else existingProfile.avatar
                welcomeBackMessage = "Welcome back, ${existingProfile.name}! 🎉\n" +
                    "🎮 ${existingProfile.gamesPlayed} games • " +
                    "🤔 ${existingProfile.truthsAnswered} truths • " +
                    "🔥 ${existingProfile.daresCompleted} dares"
            } else {
                // New player - get random or selected avatar
                avatar = selectedEmoji ?: AvatarCategories.getRandomExcluding(usedEmojis.toList())
            }
            
            // Create/update profile
            PlayerProfileManager.getOrCreateProfile(name, avatar)
            if (selectedEmoji != null) {
                PlayerProfileManager.updateAvatar(name, selectedEmoji!!)
            }
            
            players = players + Player(
                name = name,
                avatar = avatar,
                color = PlayerColors.random()
            )
            playerName = ""
            selectedEmoji = null
        }
    }
    
    fun removePlayer(player: Player) {
        players = players.filter { it.id != player.id }
    }
    
    // Emoji picker dialog
    if (showEmojiPicker) {
        EmojiPickerDialog(
            selectedEmoji = selectedEmoji,
            usedEmojis = usedEmojis,
            onEmojiSelected = { emoji ->
                selectedEmoji = emoji
                showEmojiPicker = false
            },
            onRandomSelected = {
                selectedEmoji = null
                showEmojiPicker = false
            },
            onDismiss = { showEmojiPicker = false }
        )
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
                        text = "Add Players",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${players.size}/16 players • min 2 required",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            }
            
            // Player name input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji picker button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (selectedEmoji != null) AccentPurple.copy(alpha = 0.3f)
                            else GlassWhite.copy(alpha = 0.2f)
                        )
                        .border(
                            width = if (selectedEmoji != null) 2.dp else 1.dp,
                            color = if (selectedEmoji != null) AccentPurple else GlassBorder,
                            shape = CircleShape
                        )
                        .clickable { showEmojiPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedEmoji ?: "🎲",
                        fontSize = 28.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(10.dp))
                
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { if (it.length <= 20) playerName = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { 
                        Text("Enter player name", color = TextMuted) 
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            addPlayer()
                            focusManager.clearFocus()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = AccentPurple
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                
                Spacer(modifier = Modifier.width(10.dp))
                
                // Add button
                Button(
                    onClick = { addPlayer() },
                    enabled = playerName.trim().isNotEmpty() && canAddMore,
                    modifier = Modifier
                        .size(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen,
                        disabledContainerColor = AccentGreen.copy(alpha = 0.3f)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Players grid
            if (players.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "👥",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No players yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Text(
                            text = "Add at least 2 players to start",
                            fontSize = 14.sp,
                            color = TextMuted.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Player cards grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(
                        items = players,
                        key = { it.id }
                    ) { player ->
                        PlayerCard(
                            player = player,
                            onRemove = { removePlayer(player) }
                        )
                    }
                }
            }
            
            // Bottom section
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Hint text
                if (!canContinue) {
                    Text(
                        text = "Add ${2 - players.size} more player${if (2 - players.size > 1) "s" else ""} to continue",
                        fontSize = 14.sp,
                        color = AccentOrange,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Next button
                Button(
                    onClick = { onNextClicked() },
                    enabled = canContinue,
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
                                if (canContinue) {
                                    Brush.horizontalGradient(
                                        colors = listOf(AccentPink, AccentOrange)
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            AccentPink.copy(alpha = 0.3f),
                                            AccentOrange.copy(alpha = 0.3f)
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
                                text = "Choose Game Mode",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canContinue) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = if (canContinue) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
        
        // Welcome back message overlay
        AnimatedVisibility(
            visible = welcomeBackMessage != null,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        ) {
            welcomeBackMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AccentGreen.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
