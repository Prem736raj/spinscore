package com.spinbottle.truthdare.games.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun resumeVisibleWhenSessionExists() {
        composeRule.setContent {
            HomeScreen(
                onStartGame = {},
                onResumeGame = {},
                canResumeGame = true,
                onKidsMode = {},
                onHowToPlay = {},
                onSettings = {},
                onMyPrompts = {},
                isPremium = true
            )
        }
        composeRule.onNodeWithText("Resume Game").assertIsDisplayed()
    }

    @Test
    fun resumeHiddenWithoutSession() {
        composeRule.setContent {
            HomeScreen(
                onStartGame = {},
                onResumeGame = {},
                canResumeGame = false,
                onKidsMode = {},
                onHowToPlay = {},
                onSettings = {},
                onMyPrompts = {},
                isPremium = true
            )
        }
        composeRule.onNodeWithText("Resume Game").assertDoesNotExist()
    }
}

