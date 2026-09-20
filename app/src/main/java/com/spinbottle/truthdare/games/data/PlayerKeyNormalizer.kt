package com.spinbottle.truthdare.games.data

/**
 * Central normalization for player identity keys.
 * Used everywhere player names are compared or used as lookup keys.
 */
object PlayerKeyNormalizer {
    /**
     * Normalizes a player name into a stable lookup key.
     * Trims whitespace and lowercases for case-insensitive matching.
     */
    fun normalizePlayerKey(name: String): String = name.trim().lowercase()
}
