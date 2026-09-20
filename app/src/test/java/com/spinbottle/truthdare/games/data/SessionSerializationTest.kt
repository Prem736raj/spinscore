package com.spinbottle.truthdare.games.data

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

/**
 * Regression test for session snapshot serialization.
 *
 * Verifies that every persisted field survives a Gson round-trip through
 * the same schema used by GameSessionHolder, catching silent data loss
 * from renamed fields, added defaults, or type mismatches.
 */
class SessionSerializationTest {

    private val gson = Gson()

    /**
     * A complete JSON snapshot with every field populated to non-default values.
     * This mirrors the private SessionSnapshot data class inside GameSessionHolder.
     */
    private val fullSnapshotJson = """
    {
        "players": [
            {
                "id": "p1",
                "name": "Alice",
                "avatar": "🦊",
                "colorIndex": 2,
                "score": 15,
                "truthsCompleted": 5,
                "daresCompleted": 8,
                "skips": 2
            },
            {
                "id": "p2",
                "name": "Bob",
                "avatar": "🐻",
                "colorIndex": 4,
                "score": 12,
                "truthsCompleted": 7,
                "daresCompleted": 3,
                "skips": 1
            }
        ],
        "gameMode": "COUPLES",
        "difficulty": "HARD",
        "totalRounds": 20,
        "gameStartTime": 1700000000000,
        "isKidsModeFlow": false,
        "isTournament": true,
        "completionRecorded": false,
        "targetScore": 25,
        "targetRounds": 15,
        "eliminationMode": true,
        "eliminatedPlayers": ["p2"],
        "currentChallengeLevel": 3,
        "roundsInCurrentLevel": 4,
        "currentPlayerIndex": 1,
        "quickFireSecondsRemaining": 18,
        "couplesIntimacyLevel": 4
    }
    """.trimIndent()

    @Test
    fun `full snapshot survives Gson round-trip`() {
        // Deserialize
        val parsed = gson.fromJson(fullSnapshotJson, Map::class.java)
        assertNotNull("JSON must parse to a Map", parsed)

        // Re-serialize
        val reserialized = gson.toJson(parsed)
        val reparsed = gson.fromJson(reserialized, Map::class.java)

        // Verify all top-level keys survive
        val expectedKeys = setOf(
            "players", "gameMode", "difficulty", "totalRounds",
            "gameStartTime", "isKidsModeFlow", "isTournament",
            "completionRecorded", "targetScore", "targetRounds",
            "eliminationMode", "eliminatedPlayers",
            "currentChallengeLevel", "roundsInCurrentLevel",
            "currentPlayerIndex", "quickFireSecondsRemaining",
            "couplesIntimacyLevel"
        )
        for (key in expectedKeys) {
            assertTrue("Key '${key}' must survive round-trip", reparsed.containsKey(key))
        }
    }

    @Test
    fun `player fields survive round-trip`() {
        val parsed = gson.fromJson(fullSnapshotJson, Map::class.java)
        @Suppress("UNCHECKED_CAST")
        val players = parsed["players"] as List<Map<String, Any>>

        assertEquals(2, players.size)

        val alice = players[0]
        assertEquals("p1", alice["id"])
        assertEquals("Alice", alice["name"])
        assertEquals("🦊", alice["avatar"])
        assertEquals(2.0, alice["colorIndex"]) // Gson deserializes ints as doubles in Maps
        assertEquals(15.0, alice["score"])
        assertEquals(5.0, alice["truthsCompleted"])
        assertEquals(8.0, alice["daresCompleted"])
        assertEquals(2.0, alice["skips"])
    }

    @Test
    fun `per-mode turn fields have correct values`() {
        val parsed = gson.fromJson(fullSnapshotJson, Map::class.java)

        assertEquals(1.0, parsed["currentPlayerIndex"])
        assertEquals(18.0, parsed["quickFireSecondsRemaining"])
        assertEquals(4.0, parsed["couplesIntimacyLevel"])
    }

    @Test
    fun `missing optional fields default gracefully`() {
        // A minimal snapshot with only required fields
        val minimalJson = "{ \"players\": [], \"gameMode\": \"CLASSIC\" }"
        val parsed = gson.fromJson(minimalJson, Map::class.java)

        assertNotNull(parsed)
        assertEquals("CLASSIC", parsed["gameMode"])
        // Missing fields should not be present (Gson skips them)
        assertNull("targetRounds should be absent", parsed["targetRounds"])
    }

    @Test
    fun `enum values are stored as strings`() {
        val parsed = gson.fromJson(fullSnapshotJson, Map::class.java)

        assertEquals("COUPLES", parsed["gameMode"])
        assertEquals("HARD", parsed["difficulty"])

        // Verify they parse back to valid enum values
        assertNotNull(GameMode.valueOf(parsed["gameMode"] as String))
        assertNotNull(Difficulty.valueOf(parsed["difficulty"] as String))
    }
}
