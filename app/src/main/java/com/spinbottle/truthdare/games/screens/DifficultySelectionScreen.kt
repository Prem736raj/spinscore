package com.spinbottle.truthdare.games.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import com.spinbottle.truthdare.games.data.CustomPromptsManager
import com.spinbottle.truthdare.games.data.Difficulty
import com.spinbottle.truthdare.games.data.FavoritesManager
import com.spinbottle.truthdare.games.data.GameSessionHolder
import com.spinbottle.truthdare.games.data.PinManager
import com.spinbottle.truthdare.games.data.PromptPack
import com.spinbottle.truthdare.games.data.PromptPackManager
import com.spinbottle.truthdare.games.ui.components.DifficultyCard
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelectionScreen(
    onBack: () -> Unit,
    onStartGame: () -> Unit
) {
    val context = LocalContext.current
    val pinManager = remember { PinManager(context) }
    val scope = rememberCoroutineScope()
    
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var showPinScreen by remember { mutableStateOf(false) }
    var pendingDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    
    // Pack selection states
    var packStates by remember { 
        mutableStateOf(PromptPack.values().associateWith { PromptPackManager.isPackEnabled(it) })
    }
    var showPackPinScreen by remember { mutableStateOf(false) }
    var pendingPackToggle by remember { mutableStateOf<PromptPack?>(null) }
    
    // Get custom prompts count
    val customPromptsCount = remember { CustomPromptsManager.getEnabledPrompts().size }
    
    // Get favorites count
    val favoritesCount = remember { FavoritesManager.getFavoriteCount() }
    
    // PIN screen for adult pack toggle
    if (showPackPinScreen && pendingPackToggle != null) {
        PinScreen(
            mode = PinScreenMode.VERIFY,
            onBack = {
                showPackPinScreen = false
                pendingPackToggle = null
            },
            onSuccess = {
                pendingPackToggle?.let { pack ->
                    val newState = PromptPackManager.togglePack(pack)
                    packStates = packStates.toMutableMap().apply { put(pack, newState) }
                }
                showPackPinScreen = false
                pendingPackToggle = null
            }
        )
        return
    }
    
    // PIN screen for Extreme difficulty
    if (showPinScreen) {
        PinScreen(
            mode = PinScreenMode.VERIFY,
            onBack = {
                showPinScreen = false
                pendingDifficulty = null
            },
            onSuccess = {
                showPinScreen = false
                selectedDifficulty = pendingDifficulty
                pendingDifficulty = null
            }
        )
        return
    }
    
    fun handleDifficultySelect(difficulty: Difficulty) {
        // Don't allow CUSTOM if no custom prompts
        if (difficulty == Difficulty.CUSTOM && customPromptsCount == 0) {
            return
        }
        // Don't allow FAVORITES if no favorites
        if (difficulty == Difficulty.FAVORITES && favoritesCount == 0) {
            return
        }
        
        if (difficulty.requiresPin) {
            pendingDifficulty = difficulty
            scope.launch {
                val isPinSet = pinManager.isPinSet.first()
                if (isPinSet) {
                    showPinScreen = true
                } else {
                    // No PIN set, can't access Extreme without adult verification
                    // They should set up PIN in mode selection first
                    selectedDifficulty = null
                }
            }
        } else {
            selectedDifficulty = difficulty
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
                        text = "Select Difficulty",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "How intense do you want it?",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            }
            
            // Difficulty cards
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                
                Difficulty.values().forEach { difficulty ->
                    val isCustomDisabled = difficulty == Difficulty.CUSTOM && customPromptsCount == 0
                    val isFavoritesDisabled = difficulty == Difficulty.FAVORITES && favoritesCount == 0
                    val isDisabled = isCustomDisabled || isFavoritesDisabled
                    
                    val extraLabel = when (difficulty) {
                        Difficulty.CUSTOM -> {
                            if (customPromptsCount > 0) "($customPromptsCount prompts)" else "(No prompts yet)"
                        }
                        Difficulty.FAVORITES -> {
                            if (favoritesCount > 0) "($favoritesCount favorites)" else "(No favorites yet)"
                        }
                        else -> null
                    }
                    
                    DifficultyCard(
                        difficulty = difficulty,
                        isSelected = selectedDifficulty == difficulty,
                        onClick = { handleDifficultySelect(difficulty) },
                        isDisabled = isDisabled,
                        extraLabel = extraLabel
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tournament Mode Section
                Text(
                    text = "🏆 Tournament Mode",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                
                var isTournament by remember { mutableStateOf(GameSessionHolder.isTournament) }
                var targetScore by remember { mutableIntStateOf(GameSessionHolder.targetScore) }
                var eliminationMode by remember { mutableStateOf(GameSessionHolder.eliminationMode) }
                
                // Tournament toggle
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isTournament) AccentOrange.copy(alpha = 0.2f) else GlassWhite.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Tournament",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                            Text(
                                text = "Race to target score with leaderboard",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        Switch(
                            checked = isTournament,
                            onCheckedChange = { 
                                isTournament = it
                                GameSessionHolder.isTournament = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AccentOrange,
                                checkedTrackColor = AccentOrange.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
                
                // Tournament options (shown when enabled)
                if (isTournament) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Target Score: $targetScore pts",
                        fontSize = 14.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    
                    // Score slider with +/- buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Minus button
                        IconButton(
                            onClick = { 
                                if (targetScore > 1) {
                                    targetScore--
                                    GameSessionHolder.targetScore = targetScore
                                }
                            }
                        ) {
                            Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        
                        // Slider
                        Slider(
                            value = targetScore.toFloat(),
                            onValueChange = { 
                                targetScore = it.toInt()
                                GameSessionHolder.targetScore = targetScore
                            },
                            valueRange = 1f..100f,
                            steps = 0,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = AccentOrange,
                                activeTrackColor = AccentOrange
                            )
                        )
                        
                        // Plus button
                        IconButton(
                            onClick = { 
                                if (targetScore < 100) {
                                    targetScore++
                                    GameSessionHolder.targetScore = targetScore
                                }
                            }
                        ) {
                            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Elimination mode toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GlassWhite.copy(alpha = 0.05f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚔️ Elimination Mode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                            Text(
                                text = "Lowest scorer each round is out!",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        Switch(
                            checked = eliminationMode,
                            onCheckedChange = { 
                                eliminationMode = it
                                GameSessionHolder.eliminationMode = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DareOrange,
                                checkedTrackColor = DareOrange.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Game summary and Start button
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Game summary card
                if (selectedDifficulty != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassWhite.copy(alpha = 0.1f))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Game Summary",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Text("Difficulty: ", fontSize = 14.sp, color = TextMuted)
                                Text(
                                    text = "${selectedDifficulty!!.emoji} ${selectedDifficulty!!.displayName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = selectedDifficulty!!.color
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Start Game button
                Button(
                    onClick = {
                        selectedDifficulty?.let { difficulty ->
                            GameSessionHolder.difficulty = difficulty
                            onStartGame()
                        }
                    },
                    enabled = selectedDifficulty != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(32.dp)),
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
                                if (selectedDifficulty != null) {
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
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (selectedDifficulty != null) Color.White else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Game!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedDifficulty != null) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
