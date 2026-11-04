package com.spinbottle.truthdare.games.data

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.spinbottle.truthdare.games.game.SessionEnvelope
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

class SessionRecoveryTest {
    @After fun cleanUp() { GameSessionHolder.clear() }

    private fun session(): String {
        GameSessionHolder.clear()
        GameSessionHolder.players = listOf(Player(id = "one", name = "Alice", avatar = "🐶", color = PlayerColors.colors[0]),
            Player(id = "two", name = "Bob", avatar = "🐱", color = PlayerColors.colors[1]))
        GameSessionHolder.startGame()
        GameSessionHolder.totalRounds = 7
        GameSessionHolder.currentPlayerIndex = 1
        GameSessionHolder.quickFireSecondsRemaining = 14
        GameSessionHolder.updatePlayerStats("one", true, false, false)
        return GameSessionHolder.snapshotJson()
    }

    @Test fun `actual snapshots restore every mode with scores and timer`() {
        GameMode.values().forEach { mode ->
            session()
            GameSessionHolder.gameMode = mode
            val json = GameSessionHolder.snapshotJson()
            GameSessionHolder.clear()
            assertTrue("Restore $mode", GameSessionHolder.restoreFromJson(json))
            assertEquals(mode, GameSessionHolder.gameMode)
            assertEquals(7, GameSessionHolder.totalRounds)
            assertEquals(1, GameSessionHolder.currentPlayerIndex)
            assertEquals(14, GameSessionHolder.quickFireSecondsRemaining)
            assertEquals(1, GameSessionHolder.players[0].truthsCompleted)
        }
    }

    @Test fun `null missing collection and invalid index recover without a crash or stale session`() {
        val payload = JsonParser.parseString(session()).asJsonObject["payload"].asString
        listOf("null", "{}", "{invalid", payload.replace("\"players\":[", "\"players\":null,\"unused\":["),
            payload.replace("\"eliminatedPlayers\":[]", "\"eliminatedPlayers\":null"),
            payload.replace("\"currentPlayerIndex\":1", "\"currentPlayerIndex\":99")).forEach {
            assertFalse(GameSessionHolder.restoreFromJson(it))
            assertFalse(GameSessionHolder.hasRestorableSession())
        }
    }

    @Test fun `future schemas and changed checksums are rejected`() {
        val json = session()
        assertFalse(GameSessionHolder.restoreFromJson(json.replace("\"schemaVersion\":3", "\"schemaVersion\":99")))
        assertFalse(GameSessionHolder.restoreFromJson(json.replace("Alice", "Eve")))
    }

    @Test fun `legacy snapshots without checksum remain readable`() {
        val envelope = JsonParser.parseString(session()).asJsonObject
        envelope.remove("checksum")
        assertTrue(GameSessionHolder.restoreFromJson(envelope.toString()))
        assertTrue(GameSessionHolder.restoreFromJson(envelope["payload"].asString))
    }

    @Test fun `setup roster survives recreation without being offered as an active game`() {
        session()
        GameSessionHolder.gameStartTime = 0
        GameSessionHolder.totalRounds = 0
        val draft = GameSessionHolder.snapshotJson()
        GameSessionHolder.clear()
        assertTrue(GameSessionHolder.restoreFromJson(draft))
        assertEquals(2, GameSessionHolder.players.size)
        assertFalse(GameSessionHolder.hasRestorableSession())
    }

    @Test fun `rematch preserves roster and setup and resets all progress`() {
        session()
        GameSessionHolder.gameMode = GameMode.QUICK_FIRE
        GameSessionHolder.difficulty = Difficulty.HARD
        GameSessionHolder.markCompletionRecorded()
        GameSessionHolder.prepareRematch()
        assertEquals(listOf("Alice", "Bob"), GameSessionHolder.players.map { it.name })
        assertEquals(GameMode.QUICK_FIRE, GameSessionHolder.gameMode)
        assertEquals(Difficulty.HARD, GameSessionHolder.difficulty)
        assertEquals(0, GameSessionHolder.totalRounds)
        assertEquals(0, GameSessionHolder.currentPlayerIndex)
        assertEquals(30, GameSessionHolder.quickFireSecondsRemaining)
        assertTrue(GameSessionHolder.players.all { it.score == 0 && it.truthsCompleted == 0 })
        assertFalse(GameSessionHolder.completionRecorded)
    }

    @Test fun `round limits finish on skips and completions but leave tournaments score based`() {
        session()
        GameSessionHolder.targetRounds = 8
        assertFalse(GameSessionHolder.hasReachedRoundLimit())
        GameSessionHolder.incrementRound()
        assertTrue(GameSessionHolder.hasReachedRoundLimit())
        GameSessionHolder.isTournament = true
        assertFalse(GameSessionHolder.hasReachedRoundLimit())
    }
}
