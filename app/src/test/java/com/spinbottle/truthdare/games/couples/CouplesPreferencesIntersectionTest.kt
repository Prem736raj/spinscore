package com.spinbottle.truthdare.games.couples

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CouplesPreferencesIntersectionTest {

    @Test
    fun `intersectionUsesMoreRestrictiveSettings`() {
        val partnerA = CouplesPreferences(
            maxTier = IntimacyTier.AFTER_DARK,
            allowFlirtyConversation = true,
            allowLightTouch = true,
            allowAffection = true,
            allowKissing = true,
            allowMassage = true,
            allowAfterDarkConversation = true,
            blockedTags = setOf(CouplesPromptTag.HUMOR)
        )

        val partnerB = CouplesPreferences(
            maxTier = IntimacyTier.ROMANTIC,
            allowFlirtyConversation = true,
            allowLightTouch = true,
            allowAffection = false, // B disagrees on affection
            allowKissing = false,   // B disagrees on kissing
            allowMassage = false,  // B disagrees on massage
            allowAfterDarkConversation = false, // B disagrees on after dark
            blockedTags = setOf(CouplesPromptTag.FUTURE)
        )

        val effective = partnerA.intersect(partnerB)

        // Effective must strictly take lower/more restrictive
        assertEquals(IntimacyTier.ROMANTIC, effective.maxTier)
        assertTrue(effective.allowFlirtyConversation)
        assertTrue(effective.allowLightTouch)
        assertFalse(effective.allowAffection)
        assertFalse(effective.allowKissing)
        assertFalse(effective.allowMassage)
        assertFalse(effective.allowAfterDarkConversation)

        // Blocked tags must be unioned
        assertTrue(effective.blockedTags.contains(CouplesPromptTag.HUMOR))
        assertTrue(effective.blockedTags.contains(CouplesPromptTag.FUTURE))
    }
}

