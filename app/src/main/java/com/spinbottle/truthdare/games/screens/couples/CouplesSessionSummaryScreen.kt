package com.spinbottle.truthdare.games.screens.couples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.couples.CouplesSessionState
import com.spinbottle.truthdare.games.ui.components.GlassButton
import com.spinbottle.truthdare.games.ui.theme.*

@Composable
fun CouplesSessionSummaryScreen(
    session: CouplesSessionState,
    onHome: () -> Unit
) {
    val scrollState = rememberScrollState()

    val durationMinutes = if (session.startedAtEpochMs > 0L) {
        ((System.currentTimeMillis() - session.startedAtEpochMs) / 60000).toInt().coerceAtLeast(1)
    } else 1

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
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "💖", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Connection Celebrated",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Thank you for spending intimate, intentional time together.",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatRow("Prompts Completed", "${session.completedPrompts} 💕", AccentPink)
                    StatRow("Prompts Skipped", "${session.skippedPrompts}", TextMuted)
                    StatRow("Rounds Played", "${session.round}", AccentTeal)
                    StatRow("Session Duration", "$durationMinutes mins", AccentOrange)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            GlassButton(
                text = "Return to Home",
                onClick = onHome,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 15.sp, color = TextWhite)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
