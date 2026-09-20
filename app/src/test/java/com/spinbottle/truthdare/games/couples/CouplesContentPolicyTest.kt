package com.spinbottle.truthdare.games.couples

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CouplesContentPolicyTest {

    private val basePrompt = CouplesPrompt(
        id = "test_1",
        text = "Sample romantic conversation question here.",
        type = CouplesPromptType.QUESTION,
        tier = IntimacyTier.ROMANTIC,
        touchLevel = TouchLevel.NONE,
        tags = setOf(CouplesPromptTag.CONVERSATION, CouplesPromptTag.DATE_NIGHT),
        requiresExplicitConsent = false
    )

    @Test
    fun `afterDarkExcludedWhenDisabled`() {
        val afterDarkPrompt = basePrompt.copy(
            tier = IntimacyTier.AFTER_DARK,
            tags = setOf(CouplesPromptTag.AFTER_DARK)
        )
        val prefs = CouplesPreferences(allowAfterDarkConversation = false, maxTier = IntimacyTier.AFFECTIONATE)
        assertFalse(CouplesContentPolicy.isAllowed(afterDarkPrompt, prefs))

        val allowedPrefs = CouplesPreferences(allowAfterDarkConversation = true, maxTier = IntimacyTier.AFTER_DARK)
        assertTrue(CouplesContentPolicy.isAllowed(afterDarkPrompt, allowedPrefs))
    }

    @Test
    fun `touchExcludedWhenTouchDisabled`() {
        val lightTouchPrompt = basePrompt.copy(
            touchLevel = TouchLevel.LIGHT,
            requiresExplicitConsent = true
        )
        val prefs = CouplesPreferences(allowLightTouch = false)
        assertFalse(CouplesContentPolicy.isAllowed(lightTouchPrompt, prefs))

        val allowedPrefs = CouplesPreferences(allowLightTouch = true)
        assertTrue(CouplesContentPolicy.isAllowed(lightTouchPrompt, allowedPrefs))
    }

    @Test
    fun `kissingExcludedWhenDisabled`() {
        val kissingPrompt = basePrompt.copy(
            tags = setOf(CouplesPromptTag.KISSING),
            requiresExplicitConsent = true
        )
        val prefs = CouplesPreferences(allowKissing = false)
        assertFalse(CouplesContentPolicy.isAllowed(kissingPrompt, prefs))

        val allowedPrefs = CouplesPreferences(allowKissing = true)
        assertTrue(CouplesContentPolicy.isAllowed(kissingPrompt, allowedPrefs))
    }

    @Test
    fun `massageExcludedWhenDisabled`() {
        val massagePrompt = basePrompt.copy(
            tags = setOf(CouplesPromptTag.MASSAGE),
            touchLevel = TouchLevel.AFFECTIONATE,
            requiresExplicitConsent = true
        )
        val prefs = CouplesPreferences(allowMassage = false, allowAffection = true)
        assertFalse(CouplesContentPolicy.isAllowed(massagePrompt, prefs))

        val allowedPrefs = CouplesPreferences(allowMassage = true, allowAffection = true)
        assertTrue(CouplesContentPolicy.isAllowed(massagePrompt, allowedPrefs))
    }

    @Test
    fun `blockedTagExcluded`() {
        val prompt = basePrompt.copy(tags = setOf(CouplesPromptTag.HUMOR))
        val prefs = CouplesPreferences(blockedTags = setOf(CouplesPromptTag.HUMOR))
        assertFalse(CouplesContentPolicy.isAllowed(prompt, prefs))
    }

    @Test
    fun `maxTierRespected`() {
        val highTierPrompt = basePrompt.copy(tier = IntimacyTier.AFFECTIONATE)
        val lowTierPrefs = CouplesPreferences(maxTier = IntimacyTier.ROMANTIC)
        assertFalse(CouplesContentPolicy.isAllowed(highTierPrompt, lowTierPrefs))

        val highTierPrefs = CouplesPreferences(maxTier = IntimacyTier.AFTER_DARK)
        assertTrue(CouplesContentPolicy.isAllowed(highTierPrompt, highTierPrefs))
    }
}
