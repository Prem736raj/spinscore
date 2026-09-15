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
import com.spinbottle.truthdare.games.data.PinManager
import com.spinbottle.truthdare.games.data.PromptPack
import com.spinbottle.truthdare.games.data.PromptPackManager
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun PackSelectionScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val pinManager = remember { PinManager(context) }
    val scope = rememberCoroutineScope()
    
    // Pack selection states
    var packStates by remember { 
        mutableStateOf(PromptPack.values().associateWith { PromptPackManager.isPackEnabled(it) })
    }
    var showPackPinScreen by remember { mutableStateOf(false) }
    var pendingPackToggle by remember { mutableStateOf<PromptPack?>(null) }
    
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
                        text = "📦 Prompt Packs",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${PromptPackManager.getTotalPromptCount()} prompts selected",
                        fontSize = 14.sp,
                        color = AccentTeal
                    )
                }
            }
            
            // Pack cards
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select which packs to include in your game",
                    fontSize = 14.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // In Kids Mode flow, show only kid-safe packs
                val isKidsModeFlow = com.spinbottle.truthdare.games.data.GameSessionHolder.isKidsModeFlow
                val availablePacks = if (isKidsModeFlow) {
                    PromptPack.values().filter { it.isKidsSafe }
                } else {
                    PromptPack.values().toList()
                }
                
                availablePacks.forEach { pack ->
                    val isEnabled = packStates[pack] ?: false
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (pack.requiresPin) {
                                    scope.launch {
                                        val isPinSet = pinManager.isPinSet.first()
                                        if (isPinSet) {
                                            pendingPackToggle = pack
                                            showPackPinScreen = true
                                        }
                                    }
                                } else {
                                    val newState = PromptPackManager.togglePack(pack)
                                    packStates = packStates.toMutableMap().apply { put(pack, newState) }
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEnabled) pack.color.copy(alpha = 0.2f) else GlassWhite.copy(alpha = 0.08f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pack.emoji,
                                fontSize = 36.sp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = pack.displayName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    if (pack.requiresPin) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "🔒", fontSize = 14.sp)
                                    }
                                }
                                Text(
                                    text = pack.description,
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "${pack.promptCount} prompts • ${pack.difficultyRange}",
                                    fontSize = 12.sp,
                                    color = if (isEnabled) pack.color else TextMuted
                                )
                            }
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = null,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = pack.color,
                                    checkedTrackColor = pack.color.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Bottom button
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = onNext,
                    enabled = PromptPackManager.getEnabledPackCount() > 0,
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
                                Brush.horizontalGradient(
                                    colors = listOf(AccentPurple, AccentPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Choose Difficulty",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
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
