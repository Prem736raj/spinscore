package com.spinbottle.truthdare.games.navigation

import com.spinbottle.truthdare.games.data.GameMode
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionResumeRouterTest {
    @Test
    fun quickFireResumesToQuickFire() {
        assertEquals(
            Screen.QuickFireGame.route,
            SessionResumeRouter.routeFor(GameMode.QUICK_FIRE)
        )
    }

    @Test
    fun couplesResumesToCouples() {
        assertEquals(
            Screen.CouplesGame.route,
            SessionResumeRouter.routeFor(GameMode.COUPLES)
        )
    }

    @Test
    fun kidsSafeResumesToKidsSafe() {
        assertEquals(
            Screen.KidsSafeGame.route,
            SessionResumeRouter.routeFor(GameMode.KIDS_SAFE)
        )
    }

    @Test
    fun classicLikeModesResumeToBottleGame() {
        listOf(GameMode.CLASSIC, GameMode.PARTY, GameMode.CHALLENGE)
            .forEach { mode ->
                assertEquals(Screen.Game.route, SessionResumeRouter.routeFor(mode))
            }
    }
}
