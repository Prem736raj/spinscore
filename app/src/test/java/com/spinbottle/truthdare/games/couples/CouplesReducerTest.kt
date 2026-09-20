package com.spinbottle.truthdare.games.couples

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CouplesReducerTest {

    private val reducer = CouplesReducer()
    private val initialState = CouplesSessionState(
        playerAId = "p1",
        playerBId = "p2",
        currentPlayerIndex = 0,
        round = 1,
        completedPrompts = 0,
        skippedPrompts = 0
    )

    @Test
    fun `completePrompt increments completed count and advances turn`() {
        val nextState = reducer.reduce(initialState, CouplesAction.CompletePrompt)
        assertEquals(1, nextState.completedPrompts)
        assertEquals(2, nextState.round)
        assertEquals(1, nextState.currentPlayerIndex)
    }

    @Test
    fun `skipPrompt increments skipped count and advances turn without score penalty`() {
        val nextState = reducer.reduce(initialState, CouplesAction.SkipPrompt)
        assertEquals(0, nextState.completedPrompts)
        assertEquals(1, nextState.skippedPrompts)
        assertEquals(2, nextState.round)
        assertEquals(1, nextState.currentPlayerIndex)
    }

    @Test
    fun `blockTag adds to sessionBlockedTags`() {
        val nextState = reducer.reduce(initialState, CouplesAction.BlockTag(CouplesPromptTag.MASSAGE))
        assertTrue(nextState.sessionBlockedTags.contains(CouplesPromptTag.MASSAGE))
    }
}
