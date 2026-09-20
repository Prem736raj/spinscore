package com.spinbottle.truthdare.games.data

import com.spinbottle.truthdare.games.couples.CouplesPromptCatalog
import com.spinbottle.truthdare.games.couples.CouplesPromptTag
import com.spinbottle.truthdare.games.couples.IntimacyTier
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FamilyAdultIsolationTest {

    @Test
    fun `familyPoolNeverContainsAdultCouplesPrompt`() {
        // Built-in Kids and Family prompts
        val kidsPrompts = PromptsDatabase.getPrompts(category = PromptCategory.KIDS) +
                PromptsDatabase.getPrompts(category = PromptCategory.FAMILY)
        val couplesPromptIds = CouplesPromptCatalog.allPrompts.map { it.id }.toSet()

        // Verify no kids prompt shares an ID or adult text with Couples
        for (kp in kidsPrompts) {
            assertFalse(
                "Family prompt '${kp.id}' must never be from Couples catalog",
                couplesPromptIds.contains(kp.id)
            )
        }
    }

    @Test
    fun `afterDarkPromptsAreExclusivelyAdultCouples`() {
        val afterDarkPrompts = CouplesPromptCatalog.allPrompts.filter {
            it.tier == IntimacyTier.AFTER_DARK || CouplesPromptTag.AFTER_DARK in it.tags
        }

        assertTrue("After Dark prompts must exist", afterDarkPrompts.isNotEmpty())

        val familyPromptsText = (PromptsDatabase.getPrompts(category = PromptCategory.KIDS) +
                PromptsDatabase.getPrompts(category = PromptCategory.FAMILY))
            .map { it.text.trim().lowercase() }.toSet()

        for (ad in afterDarkPrompts) {
            assertFalse(
                "After Dark prompt '${ad.id}' must never appear in Family Friendly prompts",
                familyPromptsText.contains(ad.text.trim().lowercase())
            )
        }
    }
}
