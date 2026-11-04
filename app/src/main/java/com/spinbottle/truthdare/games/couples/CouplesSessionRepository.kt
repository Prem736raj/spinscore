package com.spinbottle.truthdare.games.couples

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Storage repository for active CouplesMode domain sessions.
 * Completely local-first with graceful corrupt-json recovery.
 */
object CouplesSessionRepository {
    private const val PREFS_NAME = "couples_session_prefs"
    private const val KEY_SESSION = "active_couples_session"

    private val gson = Gson()
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(session: CouplesSessionState) {
        prefs?.edit()?.putString(KEY_SESSION, gson.toJson(session))?.apply()
    }

    fun loadSession(): CouplesSessionState? {
        val json = prefs?.getString(KEY_SESSION, null) ?: return null
        return runCatching {
            requireNotNull(gson.fromJson(json, CouplesSessionState::class.java)).also {
                require(it.playerAId.isNotBlank() && it.playerBId.isNotBlank() && it.playerAId != it.playerBId)
                require(it.currentPlayerIndex in 0..1 && it.round >= 0 && it.completedPrompts >= 0 && it.skippedPrompts >= 0)
                require(it.preferences.maxTier in IntimacyTier.values())
                require(it.preferences.blockedTags.all { tag -> tag in CouplesPromptTag.values() })
                require(it.selectedPackIds.all { id -> id in setOf("date_night", "deep_connection", "flirty", "affection", "after_dark") })
                require(it.recentPromptIds.all { id -> id.isNotBlank() })
                require(it.sessionBlockedTags.all { tag -> tag in CouplesPromptTag.values() })
            }
        }.onFailure {
            clearSession()
        }.getOrNull()
    }

    fun clearSession() {
        prefs?.edit()?.remove(KEY_SESSION)?.apply()
    }
}

