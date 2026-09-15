package com.spinbottle.truthdare.games.data

import androidx.compose.ui.graphics.Color
import com.spinbottle.truthdare.games.ui.theme.*

/**
 * Represents a player in the game
 */
data class Player(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val avatar: String,
    val color: Color,
    val score: Int = 0,
    val truthsCompleted: Int = 0,
    val daresCompleted: Int = 0,
    val skips: Int = 0
)

/**
 * Available avatars for players - fun mix of animals, emojis, and objects
 */
object Avatars {
    val animals = listOf(
        "🐶", "🐱", "🐼", "🦊", "🦁", "🐯", "🐸", "🐵",
        "🐰", "🐻", "🐨", "🐷", "🐮", "🐺", "🦄", "🐲"
    )
    
    val faces = listOf(
        "😎", "🤩", "😈", "👻", "🤖", "👽", "🎃", "💀"
    )
    
    val objects = listOf(
        "⭐", "🌟", "💎", "🔥", "💫", "🌈", "🎯", "🎪"
    )
    
    val food = listOf(
        "🍕", "🍔", "🌮", "🍩", "🍪", "🧁", "🍉", "🍓"
    )
    
    val all = animals + faces + objects + food
    
    fun random(): String = all.random()
    
    /**
     * Get a random avatar excluding already used ones
     */
    fun randomExcluding(usedAvatars: Set<String>): String {
        val available = all.filter { it !in usedAvatars }
        return if (available.isNotEmpty()) available.random() else all.random()
    }
}

/**
 * Avatar colors for player cards
 */
object PlayerColors {
    val colors = listOf(
        AccentPurple,
        AccentPink,
        AccentOrange,
        AccentTeal,
        AccentGreen,
        AccentBlue,
        AccentYellow,
        Color(0xFFE91E63),  // Pink
        Color(0xFF9C27B0),  // Deep Purple
        Color(0xFF2196F3),  // Blue
        Color(0xFF00BCD4),  // Cyan
        Color(0xFF4CAF50),  // Green
        Color(0xFFFF5722),  // Deep Orange
        Color(0xFF795548),  // Brown
        Color(0xFF607D8B),  // Blue Grey
        Color(0xFFFF1744),  // Red Accent
    )
    
    private var colorIndex = 0
    
    fun next(): Color {
        val color = colors[colorIndex % colors.size]
        colorIndex++
        return color
    }
    
    fun reset() {
        colorIndex = 0
    }
    
    fun random(): Color = colors.random()
}
