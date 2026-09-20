package com.spinbottle.truthdare.games.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.couples.IntimacyTier
import com.spinbottle.truthdare.games.ui.theme.AccentPink
import com.spinbottle.truthdare.games.ui.theme.DarkBackgroundDeep
import com.spinbottle.truthdare.games.ui.theme.TextMuted
import com.spinbottle.truthdare.games.ui.theme.TextWhite

@Composable
fun IntimacyMeter(
    currentTier: IntimacyTier,
    modifier: Modifier = Modifier
) {
    val tierName = when (currentTier) {
        IntimacyTier.WARM_UP -> "Warm Up"
        IntimacyTier.ROMANTIC -> "Romantic"
        IntimacyTier.FLIRTY -> "Flirty"
        IntimacyTier.AFFECTIONATE -> "Affectionate"
        IntimacyTier.AFTER_DARK -> "After Dark"
    }

    Column(
        modifier = modifier.semantics {
            stateDescription = "Intimacy level, $tierName, ${currentTier.level} of 5"
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            (1..5).forEach { level ->
                val active = level <= currentTier.level
                Box(
                    modifier = Modifier
                        .size(if (active) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (active) AccentPink else DarkBackgroundDeep
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "✨ $tierName",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextWhite
        )
    }
}

