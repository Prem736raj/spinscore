package com.spinbottle.truthdare.games.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class BackupPolicyTest {
    private fun resource(path: String): File {
        val direct = File(path)
        if (direct.exists()) return direct
        return File("app/$path")
    }

    @Test
    fun legacyRulesDoNotIncludeFilesDomain() {
        val xml = resource("src/main/res/xml/backup_rules.xml").readText()
        assertTrue(xml.contains("domain=\"sharedpref\""))
        assertTrue(xml.contains("session_state_prefs.xml"))
        assertFalse(xml.contains("domain=\"file\""))
    }

    @Test
    fun android12RulesDoNotIncludeFilesDomain() {
        val xml = resource("src/main/res/xml/data_extraction_rules.xml").readText()
        assertTrue(xml.contains("domain=\"sharedpref\""))
        assertTrue(xml.contains("session_state_prefs.xml"))
        assertFalse(xml.contains("domain=\"file\""))
    }
}
