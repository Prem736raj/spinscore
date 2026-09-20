package com.spinbottle.truthdare.games.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.couples.CouplesPrompt
import com.spinbottle.truthdare.games.couples.CouplesPromptType
import com.spinbottle.truthdare.games.ui.theme.*

@Composable
fun PromptCard(
    prompt: CouplesPrompt,
    modifier: Modifier = Modifier
) {
    val typeLabel = when (prompt.type) {
        CouplesPromptType.QUESTION -> "💬 Relationship Question"
        CouplesPromptType.CHALLENGE -> "🎯 Playful Challenge"
        CouplesPromptType.AFFECTION -> "💖 Tender Affection"
        CouplesPromptType.CHOICE -> "⚖️ Couples Choice"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(AccentPink.copy(alpha = 0.6f), AccentPurple.copy(alpha = 0.3f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Type badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentPink.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = typeLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentPink
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main prompt text
            Text(
                text = prompt.text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Explicit consent reminder for physical / affectionate prompts
            if (prompt.requiresExplicitConsent) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentTeal.copy(alpha = 0.12f))
                        .border(1.dp, AccentTeal.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🤝 Only continue if both partners want to.",
                        fontSize = 12.sp,
                        color = AccentTeal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
