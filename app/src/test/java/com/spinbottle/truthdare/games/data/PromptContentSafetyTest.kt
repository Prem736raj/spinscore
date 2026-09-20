package com.spinbottle.truthdare.games.data

import org.junit.Assert.assertFalse
import org.junit.Test

class PromptContentSafetyTest {
    private val disallowedFragments = listOf(
        "camera roll", "browser history", "go through your dms",
        "go through your messages", "control your phone", "from your phone",
        "prank call", "random contact", "shot of hot sauce",
        "chug a glass of water", "hold your breath as long as", "handstand (or try"
    )

    @Test
    fun builtInDaresDoNotRequirePrivateDeviceAccessOrUnsafeActions() {
        val dares = PromptsDatabase.getDares().map { it.text.lowercase() }
        disallowedFragments.forEach { fragment ->
            assertFalse("Unsafe built-in dare fragment found: $fragment", dares.any { fragment in it })
        }
    }

    @Test
    fun fallbackDaresDoNotRequirePrivateDeviceAccessOrUnsafeActions() {
        val dares = GamePrompts.dares.values.flatten().map { it.lowercase() }
        disallowedFragments.forEach { fragment ->
            assertFalse("Unsafe fallback dare fragment found: $fragment", dares.any { fragment in it })
        }
    }
}
