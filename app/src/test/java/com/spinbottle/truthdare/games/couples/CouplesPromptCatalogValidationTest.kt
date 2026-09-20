package com.spinbottle.truthdare.games.couples

import org.junit.Assert.*
import org.junit.Test

class CouplesPromptCatalogValidationTest {

    @Test
    fun `validate built-in catalog contains at least 300 prompts`() {
        val total = CouplesPromptCatalog.allPrompts.size
        assertTrue("Catalog must have at least 300 prompts, got $total", total >= 300)
    }

    @Test
    fun `validate every prompt passes CouplesPromptValidator rules`() {
        val failures = mutableListOf<String>()
        for (prompt in CouplesPromptCatalog.allPrompts) {
            val errors = CouplesPromptValidator.validate(prompt)
            if (errors.isNotEmpty()) {
                failures.addAll(errors.map { "${prompt.id}: $it" })
            }
        }
        assertTrue("Prompts with validation failures:\n${failures.joinToString("\n")}", failures.isEmpty())
    }

    @Test
    fun `no duplicate prompt IDs in catalog`() {
        val ids = CouplesPromptCatalog.allPrompts.map { it.id }
        val duplicates = ids.groupBy { it }.filter { it.value.size > 1 }.keys
        assertTrue("Duplicate prompt IDs detected: $duplicates", duplicates.isEmpty())
    }

    @Test
    fun `no duplicate prompt text in catalog`() {
        val texts = CouplesPromptCatalog.allPrompts.map { it.text.trim().lowercase() }
        val duplicates = texts.groupBy { it }.filter { it.value.size > 1 }.keys
        assertTrue("Duplicate prompt texts detected: $duplicates", duplicates.isEmpty())
    }

    @Test
    fun `every physical and kissing prompt requires explicit consent`() {
        for (prompt in CouplesPromptCatalog.allPrompts) {
            if (prompt.touchLevel != TouchLevel.NONE ||
                CouplesPromptTag.KISSING in prompt.tags ||
                CouplesPromptTag.MASSAGE in prompt.tags
            ) {
                assertTrue(
                    "Prompt '${prompt.id}' is physical/kissing/massage but lacks requiresExplicitConsent=true",
                    prompt.requiresExplicitConsent
                )
            }
        }
    }

    @Test
    fun `each pack has at least 50 prompts`() {
        val packs = listOf(
            CouplesPromptTag.DATE_NIGHT,
            CouplesPromptTag.EMOTIONAL_INTIMACY,
            CouplesPromptTag.FLIRTING,
            CouplesPromptTag.PHYSICAL_AFFECTION,
            CouplesPromptTag.AFTER_DARK
        )

        for (pack in packs) {
            val count = CouplesPromptCatalog.promptsForPack(pack).size
            assertTrue("Pack $pack must have >= 50 prompts, found $count", count >= 50)
        }
    }
}
