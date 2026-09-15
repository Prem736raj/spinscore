package com.spinbottle.truthdare.games.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.spinbottle.truthdare.games.data.GameSessionHolder
import com.spinbottle.truthdare.games.data.Player
import com.spinbottle.truthdare.games.ui.theme.*

/**
 * Tournament scoreboard showing player progress toward target score
 */
@Composable
fun TournamentScoreboard(
    players: List<Player>,
    targetScore: Int,
    modifier: Modifier = Modifier
) {
    val leader = players.maxByOrNull { it.score }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 Tournament",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange
                )
                Text(
                    text = "First to $targetScore",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Player scores
            players.sortedByDescending { it.score }.forEach { player ->
                val isLeader = player.id == leader?.id
                val isMatchPoint = player.score == targetScore - 1
                val progress = player.score.toFloat() / targetScore.coerceAtLeast(1)
                
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar and name
                        Text(
                            text = player.avatar,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = player.name,
                            fontSize = 13.sp,
                            fontWeight = if (isLeader) FontWeight.Bold else FontWeight.Normal,
                            color = if (isLeader) AccentOrange else TextWhite,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Match point indicator
                        if (isMatchPoint) {
                            MatchPointBadge()
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        
                        // Score
                        Text(
                            text = "${player.score}/$targetScore",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLeader) AccentOrange else TextWhite
                        )
                    }
                    
                    // Progress bar
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = progress.coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isMatchPoint) DareOrange else player.color,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MatchPointBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(DareOrange.copy(alpha = alpha))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "MATCH POINT!",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * Winner celebration overlay with crown animation
 */
@Composable
fun TournamentWinnerOverlay(
    winner: Player,
    onContinue: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "crown")
    val crownScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Crown
            Text(
                text = "👑",
                fontSize = (80 * crownScale).sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Winner avatar
            Text(
                text = winner.avatar,
                fontSize = 100.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Winner name
            Text(
                text = "${winner.name} WINS!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AccentOrange
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "🏆 Tournament Champion 🏆",
                fontSize = 18.sp,
                color = TextWhite
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Final Score: ${winner.score} points",
                fontSize = 16.sp,
                color = TextMuted
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange
                ),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "See Full Results",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Eliminated player notification
 */
@Composable 
fun EliminationBanner(
    eliminatedPlayer: Player,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("💀", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "ELIMINATED!",
                    fontWeight = FontWeight.Bold,
                    color = DareOrange
                )
            }
        },
        text = {
            Text(
                "${eliminatedPlayer.avatar} ${eliminatedPlayer.name} has been eliminated with ${eliminatedPlayer.score} points!",
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
            ) {
                Text("Continue")
            }
        },
        containerColor = DarkCard
    )
}

/**
 * Challenge Mode level indicator banner
 */
@Composable
fun ChallengeLevelBanner(
    currentLevel: Int,
    roundsUntilLevelUp: Int,
    modifier: Modifier = Modifier
) {
    val levelColor = when (currentLevel) {
        1 -> DifficultyEasy
        2 -> DifficultyMedium
        3 -> DifficultyHard
        else -> DifficultyExtreme
    }
    
    val levelName = when (currentLevel) {
        1 -> "EASY"
        2 -> "MEDIUM"
        3 -> "HARD"
        else -> "EXTREME"
    }
    
    val isWarning = roundsUntilLevelUp == 1 && currentLevel < 4
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isWarning) DareOrange.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏔️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Level $currentLevel: $levelName",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = levelColor
                    )
                }
                
                if (currentLevel < 4) {
                    Text(
                        text = if (isWarning) "⚠️ LEVEL UP NEXT!" else "$roundsUntilLevelUp rounds left",
                        fontSize = 12.sp,
                        fontWeight = if (isWarning) FontWeight.Bold else FontWeight.Normal,
                        color = if (isWarning) DareOrange else TextMuted
                    )
                } else {
                    Text(
                        text = "MAX LEVEL!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DifficultyExtreme
                    )
                }
            }
            
            // Progress bar to next level
            if (currentLevel < 4) {
                Spacer(modifier = Modifier.height(8.dp))
                val progress = (5 - roundsUntilLevelUp).toFloat() / 5f
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = levelColor,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}

/**
 * Level Up celebration overlay
 */
@Composable
fun LevelUpOverlay(
    newLevel: Int,
    onDismiss: () -> Unit
) {
    val levelColor = when (newLevel) {
        2 -> DifficultyMedium
        3 -> DifficultyHard
        else -> DifficultyExtreme
    }
    
    val levelName = when (newLevel) {
        2 -> "MEDIUM"
        3 -> "HARD"
        else -> "EXTREME"
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "levelup")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⬆️",
                    fontSize = (48 * scale).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LEVEL UP!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = levelColor
                )
            }
        },
        text = {
            Text(
                text = "Difficulty now: $levelName\nHarder prompts = More points!",
                color = TextWhite,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = levelColor)
            ) {
                Text("Bring it on! 💪", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkCard
    )
}
