package com.spinbottle.truthdare.games.data

import android.content.Context

/**
 * Orchestrates granular local data reset operations.
 * Never resets Google Play billing purchases.
 */
object AppDataResetManager {

    fun resetPromptHistory() {
        PromptHistoryManager.resetHistory()
    }

    fun deleteAllProofs(): Boolean {
        return DareProofManager.deleteAllProofs()
    }

    fun resetActiveSession() {
        GameSessionHolder.clear()
        com.spinbottle.truthdare.games.couples.CouplesSessionRepository.clearSession()
    }

    suspend fun resetAllData(context: Context): Boolean {
        // Report photo deletion failures before erasing the remaining metadata.
        val proofsDeleted = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { deleteAllProofs() }
        if (!proofsDeleted) return false
        resetActiveSession()
        resetPromptHistory()
        PlayerProfileManager.clearProfiles()
        listOf("custom_prompts_prefs", "favorites_prefs", "prompt_history_prefs", "game_settings",
            "theme_prefs", "prompt_pack_prefs").forEach {
            context.getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().apply()
        }
        PinManager(context).clearPin()
        ContentAccess.revoke()
        CustomPromptsManager.init(context)
        FavoritesManager.init(context)
        PromptHistoryManager.init(context)
        SettingsHolder.init(context)
        ThemeManager.init(context)
        PromptPackManager.init(context)
        return true
    }
}

