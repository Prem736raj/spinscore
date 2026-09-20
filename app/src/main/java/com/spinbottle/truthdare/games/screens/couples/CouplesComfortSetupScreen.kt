package com.spinbottle.truthdare.games.screens.couples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.spinbottle.truthdare.games.couples.CouplesPreferences
import com.spinbottle.truthdare.games.couples.IntimacyTier
import com.spinbottle.truthdare.games.couples.intersect
import com.spinbottle.truthdare.games.data.GameSessionHolder
import com.spinbottle.truthdare.games.ui.components.ComfortChip
import com.spinbottle.truthdare.games.ui.components.GlassButton
import com.spinbottle.truthdare.games.ui.theme.*

private enum class SetupStep {
    PLAYER_A,
    PASS_PHONE,
    PLAYER_B,
    SUMMARY
}

@Composable
fun CouplesComfortSetupScreen(
    onBack: () -> Unit,
    onSetupComplete: (CouplesPreferences) -> Unit
) {
    val players = GameSessionHolder.players
    val playerA = players.getOrNull(0)?.name ?: "Player 1"
    val playerB = players.getOrNull(1)?.name ?: "Player 2"

    var currentStep by remember { mutableStateOf(SetupStep.PLAYER_A) }
    var prefsA by remember { mutableStateOf(CouplesPreferences()) }
    var prefsB by remember { mutableStateOf(CouplesPreferences()) }

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
            // Top App Bar
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
                Text(
                    text = "Couples Comfort",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            when (currentStep) {
                SetupStep.PLAYER_A -> {
                    PartnerPreferenceForm(
                        partnerName = playerA,
                        preferences = prefsA,
                        onPreferencesChanged = { prefsA = it },
                        onNext = { currentStep = SetupStep.PASS_PHONE }
                    )
                }

                SetupStep.PASS_PHONE -> {
                    PassPhoneCard(
                        nextPartnerName = playerB,
                        onReady = { currentStep = SetupStep.PLAYER_B }
                    )
                }

                SetupStep.PLAYER_B -> {
                    PartnerPreferenceForm(
                        partnerName = playerB,
                        preferences = prefsB,
                        onPreferencesChanged = { prefsB = it },
                        onNext = { currentStep = SetupStep.SUMMARY }
                    )
                }

                SetupStep.SUMMARY -> {
                    val effective = remember(prefsA, prefsB) { prefsA.intersect(prefsB) }
                    EffectiveSummaryCard(
                        effectivePrefs = effective,
                        onConfirm = { onSetupComplete(effective) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PartnerPreferenceForm(
    partnerName: String,
    preferences: CouplesPreferences,
    onPreferencesChanged: (CouplesPreferences) -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set your comfort, $partnerName",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Choose what feels comfortable for you. The game only includes options both partners allow.",
            fontSize = 14.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Comfort Chips
        ComfortChip(
            label = "Flirty Conversation",
            isSelected = preferences.allowFlirtyConversation,
            onToggle = {
                onPreferencesChanged(preferences.copy(allowFlirtyConversation = !preferences.allowFlirtyConversation))
            },
            emoji = "💬",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComfortChip(
            label = "Light Touch (Hand holding, gentle touch)",
            isSelected = preferences.allowLightTouch,
            onToggle = {
                onPreferencesChanged(preferences.copy(allowLightTouch = !preferences.allowLightTouch))
            },
            emoji = "🤝",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComfortChip(
            label = "Affection (Hugs & cuddling)",
            isSelected = preferences.allowAffection,
            onToggle = {
                onPreferencesChanged(preferences.copy(allowAffection = !preferences.allowAffection))
            },
            emoji = "🫂",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComfortChip(
            label = "Kissing Prompts",
            isSelected = preferences.allowKissing,
            onToggle = {
                onPreferencesChanged(preferences.copy(allowKissing = !preferences.allowKissing))
            },
            emoji = "💋",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComfortChip(
            label = "Massage Prompts",
            isSelected = preferences.allowMassage,
            onToggle = {
                onPreferencesChanged(preferences.copy(allowMassage = !preferences.allowMassage))
            },
            emoji = "💆",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComfortChip(
            label = "After Dark (Late-night romantic intimacy)",
            isSelected = preferences.allowAfterDarkConversation,
            onToggle = {
                val next = !preferences.allowAfterDarkConversation
                onPreferencesChanged(
                    preferences.copy(
                        allowAfterDarkConversation = next,
                        maxTier = if (next) IntimacyTier.AFTER_DARK else IntimacyTier.AFFECTIONATE
                    )
                )
            },
            emoji = "🌙",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        GlassButton(
            text = "Next",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PassPhoneCard(
    nextPartnerName: String,
    onReady: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "📱", fontSize = 54.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pass the phone to $nextPartnerName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Your individual choices remain private. Next, $nextPartnerName will choose what they are comfortable with.",
                    fontSize = 14.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))
                GlassButton(
                    text = "I am $nextPartnerName",
                    onClick = onReady,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun EffectiveSummaryCard(
    effectivePrefs: CouplesPreferences,
    onConfirm: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Shared Vibe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Here is what both of you agreed to include in this session:",
            fontSize = 14.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SummaryRow("Flirty conversation", effectivePrefs.allowFlirtyConversation)
                SummaryRow("Light physical touch", effectivePrefs.allowLightTouch)
                SummaryRow("Affection & cuddling", effectivePrefs.allowAffection)
                SummaryRow("Kissing prompts", effectivePrefs.allowKissing)
                SummaryRow("Massage prompts", effectivePrefs.allowMassage)
                SummaryRow("After Dark conversation", effectivePrefs.allowAfterDarkConversation)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        GlassButton(
            text = "Start Couples Game",
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SummaryRow(
    title: String,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 15.sp, color = TextWhite)
        Text(
            text = if (isEnabled) "✓ Included" else "✕ Excluded",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isEnabled) AccentGreen else SkipRed
        )
    }
}

