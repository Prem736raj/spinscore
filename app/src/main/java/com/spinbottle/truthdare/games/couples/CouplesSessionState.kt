package com.spinbottle.truthdare.games.couples

/**
 * Pure domain state for an active Couples game session.
 * Excludes transient UI state (animations, dialogs, timers).
 */
data class CouplesSessionState(
    val playerAId: String = "",
    val playerBId: String = "",
    val preferences: CouplesPreferences = CouplesPreferences(),
    val selectedPackIds: Set<String> = emptySet(),
    val currentPlayerIndex: Int = 0,
    val round: Int = 0,
    val currentPromptId: String? = null,
    val recentPromptIds: List<String> = emptyList(),
    val sessionBlockedTags: Set<CouplesPromptTag> = emptySet(),
    val completedPrompts: Int = 0,
    val skippedPrompts: Int = 0,
    val startedAtEpochMs: Long = 0L
)
