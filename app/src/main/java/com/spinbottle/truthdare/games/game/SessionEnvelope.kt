package com.spinbottle.truthdare.games.game

/**
 * Versioned envelope for persisted game sessions.
 * Guarantees safe schema evolutions and migrations across app updates.
 */
data class SessionEnvelope(
    val schemaVersion: Int = CURRENT_SESSION_SCHEMA,
    val payload: String,
    val checksum: String? = checksumOf(payload)
)

const val CURRENT_SESSION_SCHEMA = 3

internal fun checksumOf(payload: String): String = java.security.MessageDigest
    .getInstance("SHA-256").digest(payload.toByteArray(Charsets.UTF_8))
    .joinToString("") { "%02x".format(it) }

