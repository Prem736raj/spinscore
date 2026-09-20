package com.spinbottle.truthdare.games.couples

sealed interface CouplesAction {
    data object StartSession : CouplesAction
    data class NextPrompt(val promptId: String) : CouplesAction
    data object CompletePrompt : CouplesAction
    data object SkipPrompt : CouplesAction
    data class BlockTag(val tag: CouplesPromptTag) : CouplesAction
    data class UpdatePreferences(val preferences: CouplesPreferences) : CouplesAction
    data object EndSession : CouplesAction
}
