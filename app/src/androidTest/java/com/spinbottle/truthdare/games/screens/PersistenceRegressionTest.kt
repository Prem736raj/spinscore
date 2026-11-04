package com.spinbottle.truthdare.games.screens

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.spinbottle.truthdare.games.data.*
import com.spinbottle.truthdare.games.couples.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

/** Also run in the minified QA build to exercise Gson after R8. */
class PersistenceRegressionTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @After fun cleanup() {
        GameSessionHolder.clear()
        CouplesSessionRepository.clearSession()
    }

    @Test fun savedSessionRoundTripsThroughActualStorage() {
        GameSessionHolder.init(context)
        GameSessionHolder.clear()
        GameSessionHolder.players = listOf(
            Player(id = "one", name = "Alice", avatar = "🐶", color = PlayerColors.colors[0]),
            Player(id = "two", name = "Bob", avatar = "🐱", color = PlayerColors.colors[1]))
        GameSessionHolder.gameMode = GameMode.QUICK_FIRE
        GameSessionHolder.startGame()
        GameSessionHolder.updatePlayerStats("one", true, false, false)
        GameSessionHolder.totalRounds = 7
        GameSessionHolder.currentPlayerIndex = 1
        GameSessionHolder.quickFireSecondsRemaining = 12
        val prefs = context.getSharedPreferences("session_state_prefs", Context.MODE_PRIVATE)
        val stored = prefs.getString("active_session", null)!!
        assertTrue(stored.contains("\"schemaVersion\":3"))
        assertTrue(stored.contains("\"checksum\""))
        GameSessionHolder.clear()
        prefs.edit().putString("active_session", stored).commit()
        GameSessionHolder.init(context)
        assertTrue(GameSessionHolder.hasRestorableSession())
        assertEquals(GameMode.QUICK_FIRE, GameSessionHolder.gameMode)
        assertEquals(7, GameSessionHolder.totalRounds)
        assertEquals(1, GameSessionHolder.currentPlayerIndex)
        assertEquals(12, GameSessionHolder.quickFireSecondsRemaining)
        assertEquals(1, GameSessionHolder.players[0].truthsCompleted)
    }

    @Test fun couplesStoragePreservesConsentAndProgressAndRejectsCorruption() {
        CouplesSessionRepository.init(context)
        val session = CouplesSessionState(playerAId = "a", playerBId = "b", preferences = CouplesPreferences(
            maxTier = IntimacyTier.WARM_UP, allowFlirtyConversation = false, blockedTags = setOf(CouplesPromptTag.KISSING)),
            selectedPackIds = setOf("deep_connection"), round = 6, completedPrompts = 3, skippedPrompts = 2,
            currentPlayerIndex = 1, startedAtEpochMs = 1000)
        CouplesSessionRepository.saveSession(session)
        assertEquals(session, CouplesSessionRepository.loadSession())
        context.getSharedPreferences("couples_session_prefs", Context.MODE_PRIVATE).edit()
            .putString("active_couples_session", "{\"preferences\":null}").commit()
        assertNull(CouplesSessionRepository.loadSession())
    }

    @Test fun resetAllClearsContentPreferencesAndPinInMemoryAndStorage() = runBlocking {
        CustomPromptsManager.init(context)
        FavoritesManager.init(context)
        PromptHistoryManager.init(context)
        SettingsHolder.init(context)
        ThemeManager.init(context)
        PromptPackManager.init(context)
        DareProofManager.init(context)
        CustomPromptsManager.addPrompt(CustomPrompt(type = PromptItemType.TRUTH, category = PromptCategory.FAMILY,
            difficulty = Difficulty.EASY, text = "Reset fixture"))
        FavoritesManager.addFavorite("Reset fixture", PromptItemType.TRUTH, AudienceClass.FAMILY)
        PromptHistoryManager.markAsSeen("Reset fixture")
        SettingsHolder.soundEnabled = false
        ThemeManager.totalGamesPlayed = 20
        PinManager(context).setPin("1234")
        ContentAccess.grantAdultAccess()
        assertTrue(AppDataResetManager.resetAllData(context))
        assertTrue(CustomPromptsManager.getAllPrompts().isEmpty())
        assertEquals(0, FavoritesManager.getFavoriteCount())
        assertEquals(0, PromptHistoryManager.getSeenCount())
        assertTrue(SettingsHolder.soundEnabled)
        assertEquals(0, ThemeManager.totalGamesPlayed)
        assertFalse(ContentAccess.adultAuthorized)
        assertFalse(PinManager(context).isPinSet.first())
        assertEquals(setOf(PromptPack.PARTY_STARTER, PromptPack.FAMILY_GAME_NIGHT), PromptPackManager.getEnabledPacks())
        CustomPromptsManager.init(context)
        FavoritesManager.init(context)
        assertTrue(CustomPromptsManager.getAllPrompts().isEmpty())
        assertEquals(0, FavoritesManager.getFavoriteCount())
    }
}
