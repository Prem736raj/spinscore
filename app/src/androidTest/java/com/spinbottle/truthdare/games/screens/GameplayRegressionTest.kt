package com.spinbottle.truthdare.games.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.spinbottle.truthdare.games.billing.BillingManager
import com.spinbottle.truthdare.games.couples.CouplesSessionRepository
import com.spinbottle.truthdare.games.data.*
import com.spinbottle.truthdare.games.navigation.*
import com.spinbottle.truthdare.games.ui.theme.SpinBottleTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GameplayRegressionTest {
    @get:Rule val composeRule = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private var billing: BillingManager? = null

    @Before fun setup() {
        GameSessionHolder.init(context)
        GameSessionHolder.clear()
        CouplesSessionRepository.init(context)
        CouplesSessionRepository.clearSession()
        ContentAccess.revoke()
        SettingsHolder.init(context)
        SettingsHolder.reduceMotion = true
        PromptPackManager.init(context)
        GameSessionHolder.players = listOf(
            Player(id = "a", name = "Alex", avatar = "🐶", color = PlayerColors.colors[0]),
            Player(id = "b", name = "Sam", avatar = "🐱", color = PlayerColors.colors[1]))
        GameSessionHolder.startGame()
        runBlocking { PinManager(context).setPin("1234") }
    }

    @After fun cleanup() {
        billing?.close()
        GameSessionHolder.clear()
        CouplesSessionRepository.clearSession()
        ContentAccess.revoke()
        SettingsHolder.reduceMotion = false
        runBlocking { PinManager(context).clearPin() }
    }

    private fun kidsCompletion() {
        GameSessionHolder.gameMode = GameMode.KIDS_SAFE
        GameSessionHolder.isKidsModeFlow = true
        GameSessionHolder.totalRounds = 10
        billing = BillingManager(context)
        composeRule.setContent { SpinBottleTheme { SpinBottleNavHost(billingManager = billing!!, startDestination = Screen.KidsSafeGame.route) } }
        composeRule.mainClock.advanceTimeBy(2_000)
        composeRule.onNodeWithText("Home").performScrollTo().assertIsDisplayed()
    }

    @Test fun kidsCompletionRequiresParentPinToReachHome() {
        kidsCompletion()
        composeRule.onNodeWithText("Home").performScrollTo().performClick()
        composeRule.onNodeWithText("Enter PIN").assertIsDisplayed()
        composeRule.onNodeWithText("Start Game").assertDoesNotExist()
    }

    @Test fun kidsReplayKeepsKidsModeAndRoster() {
        kidsCompletion()
        composeRule.onNodeWithText("Play Again").performScrollTo().performClick()
        composeRule.onNodeWithText("Round 1", substring = true).performScrollTo().assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(GameMode.KIDS_SAFE, GameSessionHolder.gameMode)
            assertEquals(2, GameSessionHolder.players.size)
            assertEquals(0, GameSessionHolder.totalRounds)
        }
    }

    @Test fun quickFireExitDialogPausesTimer() {
        GameSessionHolder.gameMode = GameMode.QUICK_FIRE
        GameSessionHolder.difficulty = Difficulty.EASY
        composeRule.setContent { SpinBottleTheme { QuickFireGameScreen(onBack = {}, onGameComplete = {}) } }
        composeRule.onNodeWithText("TRUTH", substring = true).performScrollTo().performClick()
        composeRule.mainClock.advanceTimeBy(2_000)
        composeRule.onNodeWithContentDescription("Exit game").performScrollTo().performClick()
        composeRule.waitForIdle()
        val pausedAt = GameSessionHolder.quickFireSecondsRemaining
        composeRule.mainClock.advanceTimeBy(5_000)
        composeRule.runOnIdle { assertEquals(pausedAt, GameSessionHolder.quickFireSecondsRemaining) }
        composeRule.onNodeWithText("Continue").performClick()
        composeRule.mainClock.advanceTimeBy(2_000)
        composeRule.runOnIdle { assertTrue(GameSessionHolder.quickFireSecondsRemaining < pausedAt) }
    }
}
