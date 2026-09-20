package com.spinbottle.truthdare.games.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptDatabaseCountTest {
    @Test
    fun uniqueCountIsPositiveAndNotGreaterThanRawCount() {
        val raw = PromptsDatabase.getPromptCount()
        val unique = PromptsDatabase.getUniquePromptCount()
        assertTrue(unique > 0)
        assertTrue(unique <= raw)
    }

    @Test
    fun normalizationCollapsesWhitespaceAndCase() {
        assertEquals(
            PromptsDatabase.normalizePromptText("  Hello   WORLD "),
            PromptsDatabase.normalizePromptText("hello world")
        )
    }

    @Test
    fun everyKidsPromptIsMarkedKidsSafe() {
        val kids = PromptsDatabase.getPrompts(category = PromptCategory.KIDS)
        assertTrue(kids.isNotEmpty())
        assertTrue(kids.all { it.isSafeForKids })
    }
}
