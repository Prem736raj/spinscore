package com.spinbottle.truthdare.games.game

/**
 * Versioned envelope for persisted game sessions.
 * Guarantees safe schema evolutions and migrations across app updates.
 */
data class SessionEnvelope(
    val schemaVersion: Int = CURRENT_SESSION_SCHEMA,
    val payload: String
)

const val CURRENT_SESSION_SCHEMA = 3

