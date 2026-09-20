package com.spinbottle.truthdare.games.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameSessionHolderTest {

    private val player = Player(
        id = "p1",
        name = "Alex",
        avatar = "🙂",
        color = PlayerColors.colors.first()
    )

    @Before
    fun setUp() {
        GameSessionHolder.clear()
        GameSessionHolder.players = listOf(player)
    }

    @After
    fun tearDown() {
        GameSessionHolder.clear()
    }

    @Test
    fun completedTruthAwardsExactlyOnePointAndOneTruth() {
        GameSessionHolder.updatePlayerStats(
            playerId = "p1",
            truthCompleted = true,
            dareCompleted = false,
            skipped = false
        )

        val updated = GameSessionHolder.players.single()
        assertEquals(1, updated.score)
        assertEquals(1, updated.truthsCompleted)
        assertEquals(0, updated.daresCompleted)
        assertEquals(0, updated.skips)
    }

    @Test
    fun skippedPromptDoesNotAwardScore() {
        GameSessionHolder.updatePlayerStats(
            playerId = "p1",
            truthCompleted = false,
            dareCompleted = false,
            skipped = true
        )

        val updated = GameSessionHolder.players.single()
        assertEquals(0, updated.score)
        assertEquals(1, updated.skips)
    }

    @Test
    fun roundIncrementIsMonotonic() {
        assertEquals(0, GameSessionHolder.totalRounds)
        GameSessionHolder.incrementRound()
        GameSessionHolder.incrementRound()
        assertEquals(2, GameSessionHolder.totalRounds)
    }

    @Test
    fun completionCanOnlyBeRecordedOncePerSession() {
        assertTrue(GameSessionHolder.markCompletionRecorded())
        assertFalse(GameSessionHolder.markCompletionRecorded())

        GameSessionHolder.startGame()

        assertTrue(GameSessionHolder.markCompletionRecorded())
    }

    @Test
    fun clearRemovesActiveGameState() {
        GameSessionHolder.gameMode = GameMode.QUICK_FIRE
        GameSessionHolder.difficulty = Difficulty.HARD
        GameSessionHolder.totalRounds = 7
        GameSessionHolder.gameStartTime = 12345L
        GameSessionHolder.isKidsModeFlow = true
        GameSessionHolder.isTournament = true
        GameSessionHolder.targetScore = 9
        GameSessionHolder.targetRounds = 5
        GameSessionHolder.eliminationMode = true
        GameSessionHolder.eliminatePlayer("p1")

        GameSessionHolder.currentPlayerIndex = 4
        GameSessionHolder.quickFireSecondsRemaining = 12
        GameSessionHolder.couplesIntimacyLevel = 5

        GameSessionHolder.clear()

        assertTrue(GameSessionHolder.players.isEmpty())
        assertEquals(GameMode.CLASSIC, GameSessionHolder.gameMode)
        assertEquals(Difficulty.MEDIUM, GameSessionHolder.difficulty)
        assertEquals(0, GameSessionHolder.totalRounds)
        assertEquals(0L, GameSessionHolder.gameStartTime)
        assertFalse(GameSessionHolder.isKidsModeFlow)
        assertFalse(GameSessionHolder.isTournament)
        assertFalse(GameSessionHolder.completionRecorded)
        assertEquals(50, GameSessionHolder.targetScore)
        assertNull(GameSessionHolder.targetRounds)
        assertFalse(GameSessionHolder.eliminationMode)
        assertTrue(GameSessionHolder.eliminatedPlayers.isEmpty())
        assertEquals(0, GameSessionHolder.currentPlayerIndex)
        assertEquals(30, GameSessionHolder.quickFireSecondsRemaining)
        assertEquals(3, GameSessionHolder.couplesIntimacyLevel)
    }

    @Test
    fun turnStatePropertiesCoerceToBounds() {
        GameSessionHolder.currentPlayerIndex = -5
        assertEquals(0, GameSessionHolder.currentPlayerIndex)

        GameSessionHolder.quickFireSecondsRemaining = 45
        assertEquals(30, GameSessionHolder.quickFireSecondsRemaining)

        GameSessionHolder.quickFireSecondsRemaining = -10
        assertEquals(0, GameSessionHolder.quickFireSecondsRemaining)

        GameSessionHolder.couplesIntimacyLevel = 0
        assertEquals(1, GameSessionHolder.couplesIntimacyLevel)

        GameSessionHolder.couplesIntimacyLevel = 9
        assertEquals(5, GameSessionHolder.couplesIntimacyLevel)
    }
}
