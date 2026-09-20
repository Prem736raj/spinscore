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

@Composable
fun SpinBottleNavHost(
    billingManager: BillingManager,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    val context = LocalContext.current
    val pinManager = remember { PinManager(context.applicationContext) }
    val isPinSet by pinManager.isPinSet.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + 
            slideInHorizontally(animationSpec = tween(300)) { it / 4 }
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + 
            slideOutHorizontally(animationSpec = tween(300)) { -it / 4 }
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + 
            slideInHorizontally(animationSpec = tween(300)) { -it / 4 }
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + 
            slideOutHorizontally(animationSpec = tween(300)) { it / 4 }
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Home.route) {
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
                    GameSessionHolder.isKidsModeFlow = false
                    navController.navigate(Screen.GameSetup.route)
                },
                onKidsMode = {
                    GameSessionHolder.clear()
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
                    // Special modes skip pack/difficulty selection
                    when (com.spinbottle.truthdare.games.data.GameSessionHolder.gameMode) {
                        com.spinbottle.truthdare.games.data.GameMode.COUPLES -> 
                            navController.navigate(Screen.CouplesGame.route)
                        com.spinbottle.truthdare.games.data.GameMode.KIDS_SAFE -> 
                            navController.navigate(Screen.KidsSafeGame.route)
                        else -> 
                            navController.navigate(Screen.PackSelection.route)
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
                    navController.navigate(Screen.GameCompletion.route) {
                        popUpTo(Screen.CouplesGame.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.KidsSafeGame.route) {
            KidsSafeGameScreen(
                onExitWithPin = {
                    GameSessionHolder.clear()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.KidsSafeGame.route) { inclusive = true }
                    }
                },
                onGameComplete = { 
                    navController.navigate(Screen.GameCompletion.route) {
                        popUpTo(Screen.KidsSafeGame.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.GameCompletion.route) {
            GameCompletionScreen(
                players = com.spinbottle.truthdare.games.data.GameSessionHolder.players,
                totalRounds = com.spinbottle.truthdare.games.data.GameSessionHolder.totalRounds,
                onPlayAgain = {
                    GameSessionHolder.clear()
                    navController.navigate(Screen.GameSetup.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onGoHome = {
                    GameSessionHolder.clear()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
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
