package com.spinbottle.truthdare.games.game

import com.google.gson.Gson
import com.google.gson.JsonObject

interface SessionMigration {
    val fromVersion: Int
    val toVersion: Int
    fun migrate(rawJson: String): String
}

object SessionMigrationPipeline {
    private val gson = Gson()

    fun migrateToLatest(rawJson: String): String {
        val trimmed = rawJson.trim()
        if (trimmed.isBlank()) return ""

        return runCatching {
            // Check if already an envelope
            val jsonObject = gson.fromJson(trimmed, JsonObject::class.java)
            if (jsonObject.has("schemaVersion") && jsonObject.has("payload")) {
                val version = jsonObject.get("schemaVersion").asInt
                val payload = jsonObject.get("payload").asString
                when (version) {
                    CURRENT_SESSION_SCHEMA -> payload
                    else -> payload // Already in modern payload format
                }
            } else {
                // Legacy unversioned payload (Schema 1 or 2) - upgrade to modern payload
                trimmed
            }
        }.getOrDefault(trimmed)
    }
}

object SessionValidator {
    fun isValid(playersSize: Int, currentPlayerIndex: Int, round: Int): Boolean {
        if (playersSize <= 0) return false
        if (currentPlayerIndex !in 0 until playersSize) return false
        if (round < 0) return false
        return true
    }
}

