package com.spinbottle.truthdare.games.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.audio.rememberSoundManager
import com.spinbottle.truthdare.games.data.GameSessionHolder
import com.spinbottle.truthdare.games.data.Player
import com.spinbottle.truthdare.games.data.PlayerProfileManager
import com.spinbottle.truthdare.games.ui.components.ConfettiAnimation
import com.spinbottle.truthdare.games.ui.theme.*

@Composable
fun GameCompletionScreen(
    players: List<Player>,
    totalRounds: Int,
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit
) {
    val soundManager = rememberSoundManager()
    var showConfetti by remember { mutableStateOf(true) }
    
    // Get game stats
    val gameDuration = remember { GameSessionHolder.getGameDurationFormatted() }
    val mostDaring = remember { GameSessionHolder.getMostDaring() }
    val mostHonest = remember { GameSessionHolder.getMostHonest() }
    
    // Play celebration sound and save player stats on screen load
    LaunchedEffect(Unit) {
        soundManager.playCelebration()
        
        // Completion side effects must run once per finished session, even if
        // this screen is recreated after rotation/process restoration.
        if (GameSessionHolder.markCompletionRecorded()) {
            players.forEach { player ->
                PlayerProfileManager.recordGamePlayed(
                    name = player.name,
                    truthsAnswered = player.truthsCompleted,
                    daresCompleted = player.daresCompleted,
                    skips = player.skips
                )
            }

            ThemeManager.incrementGamesPlayed()
            ThemeManager.addTruthsAnswered(players.sumOf { it.truthsCompleted })
            ThemeManager.addDaresCompleted(players.sumOf { it.daresCompleted })
        }
    }
    
    // Sort players by score (truths + dares completed)
    val rankedPlayers = remember(players) {
        players.sortedByDescending { it.truthsCompleted + it.daresCompleted }
    }
    
    val mvp = rankedPlayers.firstOrNull()
    
    // Trophy bounce animation
    val infiniteTransition = rememberInfiniteTransition(label = "trophy")
    val trophyScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkBackgroundSecondary, DarkBackground)
                )
            )
    ) {
        // Confetti overlay
        ConfettiAnimation(
            isPlaying = showConfetti,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            
            // Trophy and title
            Box(
                modifier = Modifier.scale(trophyScale),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = AccentYellow,
                    modifier = Modifier.size(70.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "🎉 Game Complete! 🎉",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            
            // Game stats row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "⏱️ $gameDuration",
                    fontSize = 14.sp,
                    color = TextMuted
                )
                Text(
                    text = "🎯 $totalRounds rounds",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // MVP Section
            if (mvp != null && (mvp.truthsCompleted + mvp.daresCompleted) > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    AccentYellow.copy(alpha = 0.2f),
                                    AccentOrange.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .border(2.dp, AccentYellow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(mvp.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mvp.avatar,
                                fontSize = 34.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "👑 MVP",
                                fontSize = 14.sp,
                                color = AccentYellow
                            )
                            Text(
                                text = mvp.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "${mvp.truthsCompleted + mvp.daresCompleted} challenges!",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Special Awards Row
            if (mostDaring != null || mostHonest != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Most Daring
                    if (mostDaring != null && mostDaring.daresCompleted > 0) {
                        AwardCard(
                            title = "Most Daring",
                            emoji = "🔥",
                            player = mostDaring,
                            color = DareOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Most Honest
                    if (mostHonest != null && mostHonest.truthsCompleted > 0) {
                        AwardCard(
                            title = "Most Honest",
                            emoji = "🤔",
                            player = mostHonest,
                            color = TruthBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Player Rankings
            Text(
                text = "Final Standings",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(rankedPlayers) { index, player ->
                    PlayerRankCard(
                        rank = index + 1,
                        player = player,
                        isMvp = index == 0 && (player.truthsCompleted + player.daresCompleted) > 0
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Home button
                OutlinedButton(
                    onClick = onGoHome,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", color = TextMuted, fontSize = 14.sp)
                }
                
                // Play again button
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play Again", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun AwardCard(
    title: String,
    emoji: String,
    player: Player,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$emoji $title",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = player.avatar,
                fontSize = 28.sp
            )
            Text(
                text = player.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PlayerRankCard(
    rank: Int,
    player: Player,
    isMvp: Boolean
) {
    val rankEmoji = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isMvp) AccentYellow.copy(alpha = 0.1f)
                else GlassWhite.copy(alpha = 0.08f)
            )
            .then(
                if (isMvp) Modifier.border(1.dp, AccentYellow.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                else Modifier
            )
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = rankEmoji,
                fontSize = if (rank <= 3) 20.sp else 14.sp,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center
            )
            
            // Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(player.color.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.avatar,
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(10.dp))
            
            // Name and stats
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = player.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhite
                )
                Row {
                    Text(
                        text = "🤔${player.truthsCompleted}",
                        fontSize = 11.sp,
                        color = TruthBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🔥${player.daresCompleted}",
                        fontSize = 11.sp,
                        color = DareOrange
                    )
                    if (player.skips > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⏭️${player.skips}",
                            fontSize = 11.sp,
                            color = SkipRed
                        )
                    }
                }
            }
            
            // Total score
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AccentPurple.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${player.truthsCompleted + player.daresCompleted}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple
                )
            }
        }
    }
}
