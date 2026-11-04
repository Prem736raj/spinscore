package com.spinbottle.truthdare.games.couples

import com.spinbottle.truthdare.games.screens.couples.CouplesGameViewModel
import org.junit.Assert.*
import org.junit.Test

class CouplesSessionFlowTest {
    @Test fun `setup preferences and selected pack stay enforced after repeated completions`() {
        val vm = CouplesGameViewModel()
        val preferences = CouplesPreferences(maxTier = IntimacyTier.WARM_UP, allowFlirtyConversation = false)
        vm.initialize(CouplesSessionState(playerAId = "a", playerBId = "b", preferences = preferences,
            selectedPackIds = setOf("date_night")))
        assertEquals(1, vm.uiState.value.session!!.round)
        repeat(80) {
            val prompt = vm.uiState.value.prompt!!
            assertTrue(prompt.id.startsWith("couples_date_night_"))
            assertTrue(CouplesContentPolicy.isAllowed(prompt, preferences))
            vm.completePrompt()
        }
        assertEquals(80, vm.uiState.value.session!!.completedPrompts)
        assertEquals(81, vm.uiState.value.session!!.round)
    }

    @Test fun `no matching selected pack gives empty state instead of an unselected prompt`() {
        val vm = CouplesGameViewModel()
        vm.initialize(CouplesSessionState(playerAId = "a", playerBId = "b", selectedPackIds = setOf("after_dark"),
            preferences = CouplesPreferences(maxTier = IntimacyTier.WARM_UP)))
        assertNull(vm.uiState.value.prompt)
        assertNotNull(vm.uiState.value.emptyStateMessage)
    }

    @Test fun `engine repeats exhausted selected pack before using unseen unselected packs`() {
        val selected = CouplesPromptCatalog.allPrompts.filter { CouplesPromptTag.DATE_NIGHT in it.tags }.map { it.id }.toSet()
        repeat(30) {
            val prompt = CouplesPromptEngine().nextPrompt(CouplesPromptCatalog.allPrompts, CouplesPreferences(),
                recentPromptIds = selected, preferredPackTags = setOf(CouplesPromptTag.DATE_NIGHT))!!
            assertTrue(CouplesPromptTag.DATE_NIGHT in prompt.tags)
        }
    }
}
