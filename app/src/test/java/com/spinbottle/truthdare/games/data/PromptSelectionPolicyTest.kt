package com.spinbottle.truthdare.games.data

import org.junit.After
import org.junit.Assert.*
import org.junit.Test

class PromptSelectionPolicyTest {
    @After fun cleanUp() {
        GameSessionHolder.clear(); ContentAccess.revoke()
        PromptPack.values().forEach { PromptPackManager.disablePack(it) }
    }

    @Test fun `family rejects adult general and unclassified legacy favorites even with adult access`() {
        val categories = setOf(PromptCategory.FAMILY, PromptCategory.KIDS)
        listOf(false, true).forEach { access ->
            assertTrue(PromptSelectionPolicy.allows(AudienceClass.FAMILY, categories, access))
            listOf(AudienceClass.ADULT_COUPLES, AudienceClass.GENERAL, null).forEach {
                assertFalse(PromptSelectionPolicy.allows(it, categories, access))
            }
        }
    }

    @Test fun `adult and unknown favorite prompts require explicit process access`() {
        val categories = setOf(PromptCategory.FRIENDS, PromptCategory.COUPLES)
        assertFalse(PromptSelectionPolicy.allows(AudienceClass.ADULT_COUPLES, categories, false))
        assertFalse(PromptSelectionPolicy.allows(null, categories, false))
        assertTrue(PromptSelectionPolicy.allows(AudienceClass.ADULT_COUPLES, categories, true))
    }

    @Test fun `date night never enables spicy difficulty and mixed excludes extreme`() {
        PromptPack.values().forEach { PromptPackManager.disablePack(it) }
        PromptPackManager.enablePack(PromptPack.DATE_NIGHT)
        assertTrue(PromptPackManager.isPromptEnabled(PromptCategory.COUPLES, Difficulty.MEDIUM))
        assertFalse(PromptPackManager.isPromptEnabled(PromptCategory.COUPLES, Difficulty.HARD))
        assertFalse(PromptPackManager.isPromptEnabled(PromptCategory.COUPLES, Difficulty.EXTREME))
        val allowed = PromptsDatabase.getTruths().filter { it.category == PromptCategory.COUPLES && it.difficulty in listOf(Difficulty.EASY, Difficulty.MEDIUM) }.map { it.text }
        ContentAccess.grantAdultAccess()
        repeat(100) { assertTrue(GamePrompts.getRandomTruth(Difficulty.MIXED) in allowed) }
    }

    @Test fun `favorites and custom selectors reject adult prompts in family games`() {
        PromptPack.values().forEach { PromptPackManager.disablePack(it) }
        PromptPackManager.enablePack(PromptPack.FAMILY_GAME_NIGHT)
        FavoritesManager.addFavorite("adult fixture", PromptItemType.TRUTH, AudienceClass.ADULT_COUPLES)
        CustomPromptsManager.addPrompt(CustomPrompt(id = "adult-fixture", type = PromptItemType.TRUTH,
            category = PromptCategory.COUPLES, difficulty = Difficulty.HARD, text = "adult custom fixture"))
        ContentAccess.grantAdultAccess()
        assertFalse(GamePrompts.getRandomTruth(Difficulty.FAVORITES).contains("adult fixture"))
        assertFalse(GamePrompts.getRandomTruth(Difficulty.CUSTOM).contains("adult custom fixture"))
        FavoritesManager.removeFavorite("adult fixture")
        CustomPromptsManager.deletePrompt("adult-fixture")
    }

    @Test fun `all family difficulties draw from family content including empty harder pools`() {
        GameSessionHolder.gameMode = GameMode.KIDS_SAFE
        val truths = (PromptsDatabase.getTruths(category = PromptCategory.FAMILY) + PromptsDatabase.getTruths(category = PromptCategory.KIDS)).map { it.text }
        val dares = (PromptsDatabase.getDares(category = PromptCategory.FAMILY) + PromptsDatabase.getDares(category = PromptCategory.KIDS)).map { it.text }
        // Use the actual selector, including its fallback path.
        PromptPack.values().forEach { PromptPackManager.disablePack(it) }
        PromptPackManager.enablePack(PromptPack.FAMILY_GAME_NIGHT)
        listOf(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.MIXED).forEach { difficulty ->
            repeat(25) {
                assertTrue(GamePrompts.getRandomTruth(difficulty) in truths)
                assertTrue(GamePrompts.getRandomDare(difficulty) in dares)
                assertEquals(AudienceClass.FAMILY, GamePrompts.lastAudience)
            }
        }
    }
}
