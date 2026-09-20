package com.spinbottle.truthdare.games.screens.couples

import androidx.lifecycle.ViewModel
import com.spinbottle.truthdare.games.couples.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CouplesGameUiState(
    val session: CouplesSessionState? = null,
    val prompt: CouplesPrompt? = null,
    val isLoading: Boolean = false,
    val emptyStateMessage: String? = null
)

class CouplesGameViewModel(
    private val engine: CouplesPromptEngine = CouplesPromptEngine(),
    private val reducer: CouplesReducer = CouplesReducer()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CouplesGameUiState())
    val uiState: StateFlow<CouplesGameUiState> = _uiState.asStateFlow()

    fun initialize(session: CouplesSessionState) {
        _uiState.value = _uiState.value.copy(session = session)
        CouplesSessionRepository.saveSession(session)
        if (session.currentPromptId != null) {
            val prompt = CouplesPromptCatalog.findById(session.currentPromptId)
            _uiState.value = _uiState.value.copy(prompt = prompt)
        } else {
            drawNextPrompt()
        }
    }

    fun completePrompt() {
        val currentSession = _uiState.value.session ?: return
        val updated = reducer.reduce(currentSession, CouplesAction.CompletePrompt)
        _uiState.value = _uiState.value.copy(session = updated)
        CouplesSessionRepository.saveSession(updated)
        drawNextPrompt()
    }

    fun skipPrompt() {
        val currentSession = _uiState.value.session ?: return
        val updated = reducer.reduce(currentSession, CouplesAction.SkipPrompt)
        _uiState.value = _uiState.value.copy(session = updated)
        CouplesSessionRepository.saveSession(updated)
        drawNextPrompt()
    }

    fun blockTagAndSkip(tag: CouplesPromptTag) {
        val currentSession = _uiState.value.session ?: return
        val blocked = reducer.reduce(currentSession, CouplesAction.BlockTag(tag))
        val updated = reducer.reduce(blocked, CouplesAction.SkipPrompt)
        _uiState.value = _uiState.value.copy(session = updated)
        CouplesSessionRepository.saveSession(updated)
        drawNextPrompt()
    }

    fun drawNextPrompt() {
        val currentSession = _uiState.value.session ?: return
        val effectivePrefs = currentSession.preferences.copy(
            blockedTags = currentSession.preferences.blockedTags + currentSession.sessionBlockedTags
        )
        val preferredPackTags = currentSession.selectedPackIds.mapNotNull {
            when (it) {
                "date_night" -> CouplesPromptTag.DATE_NIGHT
                "deep_connection" -> CouplesPromptTag.EMOTIONAL_INTIMACY
                "flirty" -> CouplesPromptTag.FLIRTING
                "affection" -> CouplesPromptTag.PHYSICAL_AFFECTION
                "after_dark" -> CouplesPromptTag.AFTER_DARK
                else -> null
            }
        }.toSet()

        val nextPrompt = engine.nextPrompt(
            catalog = CouplesPromptCatalog.allPrompts,
            preferences = effectivePrefs,
            recentPromptIds = currentSession.recentPromptIds.toSet(),
            preferredPackTags = preferredPackTags
        )

        if (nextPrompt != null) {
            val updated = reducer.reduce(currentSession, CouplesAction.NextPrompt(nextPrompt.id))
            _uiState.value = _uiState.value.copy(session = updated, prompt = nextPrompt, emptyStateMessage = null)
            CouplesSessionRepository.saveSession(updated)
        } else {
            _uiState.value = _uiState.value.copy(
                prompt = null,
                emptyStateMessage = "No prompts match your current comfort settings."
            )
        }
    }
}

