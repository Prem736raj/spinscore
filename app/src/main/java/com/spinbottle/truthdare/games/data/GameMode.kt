package com.spinbottle.truthdare.games.data

import androidx.compose.ui.graphics.Color
import com.spinbottle.truthdare.games.ui.theme.*

/**
 * Game modes available in Spin Bottle
 */
enum class GameMode(
    val displayName: String,
    val description: String,
    val icon: String,
    val color: Color,
    val requiresPin: Boolean = false,
    val isKidsSafe: Boolean = false
) {
    CLASSIC(
        displayName = "Classic Mode",
        description = "Standard truth or dare with bottle spin",
        icon = "🍾",
        color = AccentPurple
    ),
    QUICK_FIRE(
        displayName = "Quick Fire",
        description = "Fast 30-second rounds, rapid gameplay",
        icon = "⚡",
        color = AccentOrange
    ),
    COUPLES(
        displayName = "Couples Mode",
        description = "Romantic and flirty prompts for partners",
        icon = "💕",
        color = AccentPink,
        requiresPin = true
    ),
    PARTY(
        displayName = "Party Mode",
        description = "Wild and crazy dares for big groups",
        icon = "🎉",
        color = Color(0xFFFF1744),
        requiresPin = true
    ),
    KIDS_SAFE(
        displayName = "Kids Safe",
        description = "Family-friendly fun for all ages",
        icon = "👨‍👩‍👧‍👦",
        color = KidsModeGreen,
        isKidsSafe = true
    ),
    CHALLENGE(
        displayName = "Challenge Mode",
        description = "Difficulty escalates every 5 rounds!",
        icon = "🏔️",
        color = DifficultyHard
    )
}
