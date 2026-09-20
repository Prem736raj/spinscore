package com.spinbottle.truthdare.games.navigation

import com.spinbottle.truthdare.games.data.GameMode

object SessionResumeRouter {
    fun routeFor(mode: GameMode): String = when (mode) {
        GameMode.QUICK_FIRE -> Screen.QuickFireGame.route
        GameMode.COUPLES -> Screen.CouplesGame.route
        GameMode.KIDS_SAFE -> Screen.KidsSafeGame.route
        GameMode.CLASSIC,
        GameMode.PARTY,
        GameMode.CHALLENGE -> Screen.Game.route
    }
}
