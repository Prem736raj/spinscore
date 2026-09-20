package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.data.Player
import com.spinbottle.truthdare.games.ui.theme.AccentPink
import com.spinbottle.truthdare.games.ui.theme.DarkCard
import com.spinbottle.truthdare.games.ui.theme.TextMuted
import com.spinbottle.truthdare.games.ui.theme.TextWhite

@Composable
fun AdaptiveTwoPlayerHeader(
    playerA: Player,
    playerB: Player,
    activePlayerIndex: Int,
    modifier: Modifier = Modifier
) {
    val activePlayerName = if (activePlayerIndex == 0) playerA.name else playerB.name

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                stateDescription = "Active turn: $activePlayerName"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Player A
        PlayerAvatarBadge(
            player = playerA,
            isActive = activePlayerIndex == 0
        )

        // Center connection symbol
        Text(
            text = "💞",
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // Player B
        PlayerAvatarBadge(
            player = playerB,
            isActive = activePlayerIndex == 1
        )
    }
}

@Composable
private fun PlayerAvatarBadge(
    player: Player,
    isActive: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "avatarScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(if (isActive) AccentPink.copy(alpha = 0.3f) else DarkCard)
                .border(
                    width = if (isActive) 2.5.dp else 1.dp,
                    color = if (isActive) AccentPink else player.color.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = player.avatar, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = player.name,
            fontSize = 13.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) TextWhite else TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
