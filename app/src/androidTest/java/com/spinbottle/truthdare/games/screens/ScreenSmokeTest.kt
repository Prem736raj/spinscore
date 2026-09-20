package com.spinbottle.truthdare.games.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.spinbottle.truthdare.games.data.GameSessionHolder
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScreenSmokeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        GameSessionHolder.clear()
    }

    @Test
    fun couplesShowsInvalidPlayerWarningWhenNotTwoPlayers() {
        composeRule.setContent {
            CouplesGameScreen(
                onBack = {},
                onGameComplete = {}
            )
        }
        composeRule.onNodeWithText("Couples Mode requires exactly 2 players.").assertIsDisplayed()
        composeRule.onNodeWithText("Back to setup").assertIsDisplayed()
    }
}

