package com.spinbottle.truthdare.games.couples

class CouplesReducer {
    fun reduce(
        state: CouplesSessionState,
        action: CouplesAction
    ): CouplesSessionState =
        when (action) {
            CouplesAction.StartSession ->
                state.copy(
                    startedAtEpochMs = if (state.startedAtEpochMs == 0L) System.currentTimeMillis() else state.startedAtEpochMs,
                    round = if (state.round == 0) 1 else state.round
                )

            is CouplesAction.NextPrompt ->
                state.copy(
                    currentPromptId = action.promptId,
                    recentPromptIds = (listOf(action.promptId) + state.recentPromptIds).distinct().take(50)
                )

            CouplesAction.CompletePrompt ->
                state.copy(
                    completedPrompts = state.completedPrompts + 1,
                    round = state.round + 1,
                    currentPlayerIndex = (state.currentPlayerIndex + 1) % 2
                )

            CouplesAction.SkipPrompt ->
                state.copy(
                    skippedPrompts = state.skippedPrompts + 1,
                    round = state.round + 1,
                    currentPlayerIndex = (state.currentPlayerIndex + 1) % 2
                )

            is CouplesAction.BlockTag ->
                state.copy(
                    sessionBlockedTags = state.sessionBlockedTags + action.tag
                )

            is CouplesAction.UpdatePreferences ->
                state.copy(preferences = action.preferences)

            CouplesAction.EndSession ->
                state.copy(currentPromptId = null)
        }
}

