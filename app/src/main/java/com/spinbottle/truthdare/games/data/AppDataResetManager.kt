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

    fun resetAllData(context: Context): Boolean {
        resetActiveSession()
        resetPromptHistory()
        val proofsDeleted = deleteAllProofs()
        PlayerProfileManager.clearProfiles()
        return proofsDeleted
    }
}
