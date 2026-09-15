package com.spinbottle.truthdare.games.data

import androidx.compose.ui.graphics.Color
import com.spinbottle.truthdare.games.ui.theme.*

/**
 * Difficulty levels for gameplay
 */
enum class Difficulty(
    val displayName: String,
    val description: String,
    val emoji: String,
    val color: Color,
    val requiresPin: Boolean = false
) {
    EASY(
        displayName = "Easy",
        description = "Light, casual, nothing embarrassing",
        emoji = "🟢",
        color = DifficultyEasy
    ),
    MEDIUM(
        displayName = "Medium",
        description = "Moderate challenges, some personal questions",
        emoji = "🟡",
        color = DifficultyMedium
    ),
    HARD(
        displayName = "Hard",
        description = "Spicy dares, deep truths, push your limits",
        emoji = "🔴",
        color = DifficultyHard
    ),
    EXTREME(
        displayName = "Extreme",
        description = "Only for the fearless! Maximum intensity",
        emoji = "🟣",
        color = DifficultyExtreme,
        requiresPin = true
    ),
    MIXED(
        displayName = "Mixed",
        description = "Randomly varies between Easy/Medium/Hard",
        emoji = "🌈",
        color = AccentTeal
    ),
    CUSTOM(
        displayName = "Custom Only",
        description = "Play with your custom prompts only! ✨",
        emoji = "✨",
        color = AccentOrange
    ),
    FAVORITES(
        displayName = "Favorites Only",
        description = "Play with your favorite prompts! ❤️",
        emoji = "❤️",
        color = AccentPink
    )
}
