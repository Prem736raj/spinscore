package com.spinbottle.truthdare.games.navigation

/**
 * Navigation routes for the Spin Bottle app
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object GameSetup : Screen("game_setup")
    object ModeSelection : Screen("mode_selection")
    object PackSelection : Screen("pack_selection")
    object DifficultySelection : Screen("difficulty_selection")
    object Game : Screen("game")
    object QuickFireGame : Screen("quick_fire_game")
    object CouplesGame : Screen("couples_game")
    object KidsSafeGame : Screen("kids_safe_game")
    object GameCompletion : Screen("game_completion")
    object Settings : Screen("settings")
    object MyPrompts : Screen("my_prompts")
    object AddPrompt : Screen("add_prompt")
    object EditPrompt : Screen("edit_prompt/{promptId}") {
        fun createRoute(promptId: String) = "edit_prompt/$promptId"
    }
    object HowToPlay : Screen("how_to_play")
    object PinSetup : Screen("pin_setup")
    object PinVerify : Screen("pin_verify")
    object DareGallery : Screen("dare_gallery")
    object Premium : Screen("premium")
}
