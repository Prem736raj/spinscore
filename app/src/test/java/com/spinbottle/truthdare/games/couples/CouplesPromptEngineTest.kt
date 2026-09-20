package com.spinbottle.truthdare.games.couples

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class CouplesPromptEngineTest {

    private val engine = CouplesPromptEngine(Random(12345))

    private val catalog = listOf(
        CouplesPrompt(
            id = "p1",
            text = "Tell your partner about your favorite shared memory.",
            type = CouplesPromptType.QUESTION,
            tier = IntimacyTier.WARM_UP,
            tags = setOf(CouplesPromptTag.MEMORIES, CouplesPromptTag.DATE_NIGHT)
        ),
        CouplesPrompt(
            id = "p2",
            text = "Give your partner a sweet compliment about their smile.",
            type = CouplesPromptType.CHALLENGE,
            tier = IntimacyTier.ROMANTIC,
            tags = setOf(CouplesPromptTag.COMPLIMENT, CouplesPromptTag.DATE_NIGHT)
        ),
        CouplesPrompt(
            id = "p3",
            text = "Share one dream for our future together.",
            type = CouplesPromptType.QUESTION,
            tier = IntimacyTier.ROMANTIC,
            tags = setOf(CouplesPromptTag.FUTURE, CouplesPromptTag.EMOTIONAL_INTIMACY)
        )
    )

    @Test
    fun `returns null when no prompt matches preferences`() {
        val restrictivePrefs = CouplesPreferences(maxTier = IntimacyTier.WARM_UP, blockedTags = setOf(CouplesPromptTag.MEMORIES))
        val prompt = engine.nextPrompt(catalog, restrictivePrefs)
        assertNull(prompt)
    }

    @Test
    fun `unseen prompts are selected before recently seen`() {
        val prefs = CouplesPreferences(maxTier = IntimacyTier.ROMANTIC)
        val prompt = engine.nextPrompt(
            catalog = catalog,
            preferences = prefs,
            recentPromptIds = setOf("p1", "p2")
        )
        assertNotNull(prompt)
        assertEquals("p3", prompt?.id)
    }

    @Test
    fun `fallback occurs when all allowed prompts have been seen`() {
        val prefs = CouplesPreferences(maxTier = IntimacyTier.ROMANTIC)
        val prompt = engine.nextPrompt(
            catalog = catalog,
            preferences = prefs,
            recentPromptIds = setOf("p1", "p2", "p3")
        )
        assertNotNull("Must gracefully recycle prompts when pool is exhausted", prompt)
    }
}
