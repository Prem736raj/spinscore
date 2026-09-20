package com.spinbottle.truthdare.games.data

import org.junit.Assert.assertFalse
import org.junit.Test

class PromptContentSafetyTest {
    private val disallowedDareFragments = listOf(
        "camera roll",
        "browser history",
        "go through your dms",
        "go through your messages",
        "control your phone",
        "from your phone",
        "call a random contact",
        "text your boss",
        "send a text to your crush",
        "5th person in your contacts",
        "look through your entire phone",
        "go live on social media",
        "call your boss/teacher",
        "call a pizza place",
        "10th contact",
        "follow whoever the group picks",
        "most embarrassing photo in your gallery",
        "most embarrassing search history",
        "eat something blindfolded",
        "mystery combination of foods",
        "blindfold your partner",
        "arm wrestle",
        "plank for as long",
        "without stopping",
        "shot of hot sauce",
        "chug a glass of water",
        "hold your breath as long as",
        "handstand (or try"
    )

    @Test
    fun builtInDaresDoNotRequirePrivateDeviceAccessThirdPartyContactOrUnsafeActions() {
        val dares = PromptsDatabase.getDares().map { it.text.lowercase() }

        disallowedDareFragments.forEach { fragment ->
            assertFalse(
                "Unsafe built-in dare fragment found: $fragment",
                dares.any { fragment in it }
            )
        }
    }

    @Test
    fun fallbackDaresDoNotRequirePrivateDeviceAccessThirdPartyContactOrUnsafeActions() {
        val dares = GamePrompts.dares.values.flatten().map { it.lowercase() }

        disallowedDareFragments.forEach { fragment ->
            assertFalse(
                "Unsafe fallback dare fragment found: $fragment",
                dares.any { fragment in it }
            )
        }
    }

    @Test
    fun builtInTruthsDoNotRequireSearchHistoryDisclosure() {
        val truths = PromptsDatabase.getTruths().map { it.text.lowercase() }

        assertFalse(
            "Built-in truth asks for private search-history disclosure",
            truths.any { "search history" in it }
        )
    }
}
