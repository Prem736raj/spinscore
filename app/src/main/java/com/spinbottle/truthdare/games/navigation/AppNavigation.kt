package com.spinbottle.truthdare.games.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spinbottle.truthdare.games.screens.*

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.spinbottle.truthdare.games.billing.BillingManager
import com.spinbottle.truthdare.games.data.GameMode
import com.spinbottle.truthdare.games.data.GameSessionHolder
import com.spinbottle.truthdare.games.data.PinManager
import com.spinbottle.truthdare.games.data.ContentAccess
import com.spinbottle.truthdare.games.couples.*
import androidx.activity.compose.BackHandler

@Composable
fun SpinBottleNavHost(
    billingManager: BillingManager,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    val context = LocalContext.current
    val pinManager = remember { PinManager(context.applicationContext) }
    val isPinSet by pinManager.isPinSet.collectAsState(initial = false)
    fun goHome() {
        GameSessionHolder.clear()
        CouplesSessionRepository.clearSession()
        ContentAccess.revoke()
        navController.navigate(Screen.Home.route) { popUpTo(navController.graph.id) { inclusive = true } }
    }
    fun replay() {
        val couples = CouplesSessionRepository.loadSession()
        GameSessionHolder.prepareRematch()
        if (GameSessionHolder.gameMode == GameMode.COUPLES && couples != null) {
            CouplesSessionRepository.saveSession(CouplesSessionState(
                playerAId = couples.playerAId, playerBId = couples.playerBId,
                preferences = couples.preferences, selectedPackIds = couples.selectedPackIds))
        }
        navController.navigate(SessionResumeRouter.routeFor(GameSessionHolder.gameMode)) {
            popUpTo(if (GameSessionHolder.gameMode == GameMode.COUPLES) Screen.CouplesSummary.route
                else Screen.GameCompletion.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            if (com.spinbottle.truthdare.games.data.SettingsHolder.reduceMotion) EnterTransition.None else fadeIn(animationSpec = tween(300)) +
            slideInHorizontally(animationSpec = tween(300)) { it / 4 }
        },
        exitTransition = {
            if (com.spinbottle.truthdare.games.data.SettingsHolder.reduceMotion) ExitTransition.None else fadeOut(animationSpec = tween(300)) +
            slideOutHorizontally(animationSpec = tween(300)) { -it / 4 }
        },
        popEnterTransition = {
            if (com.spinbottle.truthdare.games.data.SettingsHolder.reduceMotion) EnterTransition.None else fadeIn(animationSpec = tween(300)) +
            slideInHorizontally(animationSpec = tween(300)) { -it / 4 }
        },
        popExitTransition = {
            if (com.spinbottle.truthdare.games.data.SettingsHolder.reduceMotion) ExitTransition.None else fadeOut(animationSpec = tween(300)) +
            slideOutHorizontally(animationSpec = tween(300)) { it / 4 }
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    val destination = if (GameSessionHolder.hasRestorableSession() &&
                        GameSessionHolder.gameMode == GameMode.KIDS_SAFE) {
                        if (GameSessionHolder.completionRecorded) Screen.GameCompletion.route else Screen.KidsSafeGame.route
                    } else Screen.Home.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            val isPremium by billingManager.isPremium.collectAsState()
            HomeScreen(
                onStartGame = {
                    GameSessionHolder.clear()
                    CouplesSessionRepository.clearSession()
                    ContentAccess.revoke()
                    GameSessionHolder.isKidsModeFlow = false
                    navController.navigate(Screen.GameSetup.route)
                },
                onResumeGame = {
                    if (GameSessionHolder.hasRestorableSession()) {
                        navController.navigate(
                            if ((GameSessionHolder.gameMode.requiresPin || GameSessionHolder.difficulty.requiresPin ||
                                (GameSessionHolder.gameMode != GameMode.KIDS_SAFE && com.spinbottle.truthdare.games.data.PromptPackManager.hasAdultPackEnabled())) &&
                                !ContentAccess.adultAuthorized) Screen.ResumeUnlock.route
                            else SessionResumeRouter.routeFor(GameSessionHolder.gameMode)
                        ) {
                            launchSingleTop = true
                        }
                    } else {
                        GameSessionHolder.clear()
                        navController.navigate(Screen.GameSetup.route)
                    }
                },
                canResumeGame = GameSessionHolder.hasRestorableSession(),
                onKidsMode = {
                    GameSessionHolder.clear()
                    CouplesSessionRepository.clearSession()
                    ContentAccess.revoke()
                    if (isPinSet) {
                        GameSessionHolder.isKidsModeFlow = true
                        GameSessionHolder.gameMode = GameMode.KIDS_SAFE
                        navController.navigate(Screen.GameSetup.route)
                    } else {
                        navController.navigate(Screen.PinSetup.route)
                    }
                },
                onHowToPlay = { navController.navigate(Screen.HowToPlay.route) },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onMyPrompts = { navController.navigate(Screen.MyPrompts.route) },
                isPremium = isPremium,
                onPremiumClick = { navController.navigate(Screen.Premium.route) }
            )
        }
        
        composable(Screen.GameSetup.route) {
            GameSetupScreen(
                onBack = { navController.popBackStack() },
                onNext = {
                    if (GameSessionHolder.isKidsModeFlow) {
                        GameSessionHolder.gameMode = GameMode.KIDS_SAFE
                        navController.navigate(Screen.KidsSafeGame.route)
                    } else {
                        navController.navigate(Screen.ModeSelection.route)
                    }
                }
            )
        }
        
        composable(Screen.ModeSelection.route) {
            ModeSelectionScreen(
                onBack = { navController.popBackStack() },
                onModeSelected = { 
                    // Special modes skip generic pack/difficulty selection
                    when (com.spinbottle.truthdare.games.data.GameSessionHolder.gameMode) {
                        com.spinbottle.truthdare.games.data.GameMode.COUPLES -> 
                            navController.navigate(Screen.CouplesComfortSetup.route)
                        com.spinbottle.truthdare.games.data.GameMode.KIDS_SAFE -> 
                            navController.navigate(Screen.KidsSafeGame.route)
                        else -> 
                            navController.navigate(Screen.PackSelection.route)
                    }
                }
            )
        }
        
        composable(Screen.CouplesComfortSetup.route) {
            com.spinbottle.truthdare.games.screens.couples.CouplesComfortSetupScreen(
                onBack = { navController.popBackStack() },
                onSetupComplete = { prefs ->
                    CouplesSessionRepository.saveSession(CouplesSessionState(
                        playerAId = GameSessionHolder.players[0].id,
                        playerBId = GameSessionHolder.players[1].id,
                        preferences = prefs))
                    navController.navigate(Screen.CouplesPackSelection.route)
                }
            )
        }

        composable(Screen.CouplesPackSelection.route) {
            com.spinbottle.truthdare.games.screens.couples.CouplesPackSelectionScreen(
                preferences = CouplesSessionRepository.loadSession()?.preferences ?: CouplesPreferences(),
                onBack = { navController.popBackStack() },
                onStartGame = { packIds ->
                    CouplesSessionRepository.loadSession()?.let {
                        CouplesSessionRepository.saveSession(it.copy(selectedPackIds = packIds))
                    }
                    navController.navigate(Screen.CouplesGame.route) {
                        popUpTo(Screen.ModeSelection.route) { inclusive = false }
                    }
                }
            )
        }
        
        composable(Screen.PackSelection.route) {
            PackSelectionScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.DifficultySelection.route) }
            )
        }
        
        composable(Screen.DifficultySelection.route) {
            DifficultySelectionScreen(
                onBack = { navController.popBackStack() },
                onStartGame = { 
                    // Choose game screen based on mode
                    val route = when (com.spinbottle.truthdare.games.data.GameSessionHolder.gameMode) {
                        com.spinbottle.truthdare.games.data.GameMode.QUICK_FIRE -> Screen.QuickFireGame.route
                        com.spinbottle.truthdare.games.data.GameMode.COUPLES -> Screen.CouplesGame.route
                        else -> Screen.Game.route
                    }
                    navController.navigate(route)
                }
            )
        }
        
        composable(Screen.Game.route) {
            GameScreen(
                onBack = { navController.popBackStack() },
                onGameEnd = { 
                    navController.navigate(Screen.GameCompletion.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onViewGallery = { navController.navigate(Screen.DareGallery.route) }
            )
        }
        
        composable(Screen.QuickFireGame.route) {
            QuickFireGameScreen(
                onBack = { navController.popBackStack() },
                onGameComplete = { 
                    navController.navigate(Screen.GameCompletion.route) {
                        popUpTo(Screen.QuickFireGame.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.CouplesGame.route) {
            CouplesGameScreen(
                onBack = { navController.popBackStack() },
                onGameComplete = { 
                    navController.navigate(Screen.CouplesSummary.route) {
                        popUpTo(Screen.CouplesGame.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.KidsSafeGame.route) {
            KidsSafeGameScreen(
                onExitWithPin = {
                    goHome()
                },
                onGameComplete = { 
                    navController.navigate(Screen.GameCompletion.route) {
                        popUpTo(Screen.KidsSafeGame.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.GameCompletion.route) {
            val homeAction = {
                if (GameSessionHolder.gameMode == GameMode.KIDS_SAFE) navController.navigate(Screen.ParentExit.route)
                else goHome()
            }
            BackHandler(onBack = homeAction)
            GameCompletionScreen(
                players = com.spinbottle.truthdare.games.data.GameSessionHolder.players,
                totalRounds = com.spinbottle.truthdare.games.data.GameSessionHolder.totalRounds,
                onPlayAgain = { replay() },
                onGoHome = homeAction
            )
        }

        composable(Screen.ParentExit.route) {
            PinScreen(mode = PinScreenMode.VERIFY,
                onBack = { navController.popBackStack() }, onSuccess = { goHome() },
                allowSetupWhenMissing = false, markAgeVerifiedOnSetup = false)
        }
        composable(Screen.ResumeUnlock.route) {
            PinScreen(mode = PinScreenMode.VERIFY, onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(SessionResumeRouter.routeFor(GameSessionHolder.gameMode)) {
                        popUpTo(Screen.ResumeUnlock.route) { inclusive = true }
                    }
                })
        }
        composable(Screen.CouplesSummary.route) {
            BackHandler { goHome() }
            val session = CouplesSessionRepository.loadSession()
            if (session != null) {
                com.spinbottle.truthdare.games.screens.couples.CouplesSessionSummaryScreen(
                    session = session, onHome = { goHome() }, onReplay = {
                        replay()
                    })
            } else {
                androidx.compose.runtime.LaunchedEffect(Unit) { goHome() }
            }
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onViewGallery = { navController.navigate(Screen.DareGallery.route) }
            )
        }
        
        composable(Screen.MyPrompts.route) {
            MyPromptsScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddPrompt = { navController.navigate(Screen.AddPrompt.route) },
                onEditPrompt = { promptId ->
                    navController.navigate(Screen.EditPrompt.createRoute(promptId))
                }
            )
        }
        
        composable(Screen.AddPrompt.route) {
            AddEditPromptScreen(
                promptId = null,
                onNavigateBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
        
        composable(Screen.EditPrompt.route) { backStackEntry ->
            val promptId = backStackEntry.arguments?.getString("promptId")
            AddEditPromptScreen(
                promptId = promptId,
                onNavigateBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
        
        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.PinSetup.route) {
            PinScreen(
                mode = PinScreenMode.SETUP,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    GameSessionHolder.isKidsModeFlow = true
                    GameSessionHolder.gameMode = GameMode.KIDS_SAFE
                    navController.navigate(Screen.GameSetup.route) {
                        popUpTo(Screen.PinSetup.route) { inclusive = true }
                    }
                },
                markAgeVerifiedOnSetup = false
            )
        }

        composable(Screen.DareGallery.route) {
            DareGalleryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Premium.route) {
            PremiumScreen(
                billingManager = billingManager,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
