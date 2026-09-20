package com.spinbottle.truthdare.games.screens.couples

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.couples.CouplesPreferences
import com.spinbottle.truthdare.games.ui.components.GlassButton
import com.spinbottle.truthdare.games.ui.theme.*

data class CouplesPackInfo(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val requiresAfterDark: Boolean = false
)

val availableCouplesPacks = listOf(
    CouplesPackInfo("date_night", "Date Night", "Romantic memories, compliments & warm conversation", "🍷"),
    CouplesPackInfo("deep_connection", "Deep Connection", "Trust, future dreams & emotional vulnerability", "💬"),
    CouplesPackInfo("flirty", "Flirty & Playful", "Charm, playful teasing & magnetic banter", "✨"),
    CouplesPackInfo("affection", "Affection & Cuddles", "Gentle touch, hugs, massages & sweet kisses", "🫂"),
    CouplesPackInfo("after_dark", "After Dark", "Late-night romantic tension & sensual intimacy", "🌙", requiresAfterDark = true)
)

@Composable
fun CouplesPackSelectionScreen(
    preferences: CouplesPreferences,
    onBack: () -> Unit,
    onStartGame: (Set<String>) -> Unit
) {
    var selectedPackIds by remember {
        mutableStateOf(
            if (preferences.allowAfterDarkConversation) {
                setOf("date_night", "deep_connection", "flirty", "affection", "after_dark")
            } else {
                setOf("date_night", "deep_connection", "flirty", "affection")
            }
        )
    }

    val scrollState = rememberScrollState()

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
                Column {
                    Text(
                        text = "Choose Packs",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${selectedPackIds.size} packs active",
                        fontSize = 12.sp,
                        color = AccentPink
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                availableCouplesPacks.forEach { pack ->
                    val isLocked = pack.requiresAfterDark && !preferences.allowAfterDarkConversation
                    val isSelected = pack.id in selectedPackIds && !isLocked

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(enabled = !isLocked) {
                                selectedPackIds = if (isSelected) {
                                    if (selectedPackIds.size > 1) selectedPackIds - pack.id else selectedPackIds
                                } else {
                                    selectedPackIds + pack.id
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) AccentPink.copy(alpha = 0.2f) else DarkCard
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.5.dp,
                            color = if (isSelected) AccentPink else GlassBorder.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = pack.emoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pack.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLocked) TextMuted else TextWhite
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isLocked) "Excluded by comfort preferences" else pack.description,
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(AccentPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlassButton(
                    text = "Start Romance",
                    onClick = { onStartGame(selectedPackIds) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

