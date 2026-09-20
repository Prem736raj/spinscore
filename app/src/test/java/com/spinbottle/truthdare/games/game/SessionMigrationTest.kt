package com.spinbottle.truthdare.games.game

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class SessionMigrationTest {

    private val gson = Gson()

    @Test
    fun `legacy unversioned json passes through migration pipeline safely`() {
        val legacyJson = """{"players":[],"gameMode":"CLASSIC","totalRounds":5}"""
        val result = SessionMigrationPipeline.migrateToLatest(legacyJson)
        assertEquals(legacyJson, result)
    }

    @Test
    fun `version 3 envelope extracts payload correctly`() {
        val payload = """{"players":[],"gameMode":"COUPLES"}"""
        val envelope = SessionEnvelope(schemaVersion = 3, payload = payload)
        val envelopeJson = gson.toJson(envelope)

        val extracted = SessionMigrationPipeline.migrateToLatest(envelopeJson)
        assertEquals(payload, extracted)
    }

    @Test
    fun `corrupt json recovers gracefully without throwing`() {
        val corrupt = "{invalid json content"
        val result = SessionMigrationPipeline.migrateToLatest(corrupt)
        assertEquals(corrupt, result)
    }

    @Test
    fun `session validator detects valid and invalid configurations`() {
        assertTrue(SessionValidator.isValid(playersSize = 4, currentPlayerIndex = 2, round = 3))
        assertFalse("Empty players must be invalid", SessionValidator.isValid(playersSize = 0, currentPlayerIndex = 0, round = 1))
        assertFalse("Index out of bounds must be invalid", SessionValidator.isValid(playersSize = 2, currentPlayerIndex = 2, round = 1))
        assertFalse("Negative round must be invalid", SessionValidator.isValid(playersSize = 2, currentPlayerIndex = 0, round = -1))
    }
}
