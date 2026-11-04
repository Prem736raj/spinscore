package com.spinbottle.truthdare.games.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

/** Adult access is granted by a PIN for this process, never by restored JSON. */
object ContentAccess {
    var adultAuthorized: Boolean by mutableStateOf(false)
        private set
    fun grantAdultAccess() { adultAuthorized = true }
    fun revoke() { adultAuthorized = false }
}

object PromptSelectionPolicy {
    fun audience(category: PromptCategory, difficulty: Difficulty): AudienceClass = when {
        category == PromptCategory.COUPLES || difficulty == Difficulty.EXTREME -> AudienceClass.ADULT_COUPLES
        category == PromptCategory.KIDS || category == PromptCategory.FAMILY -> AudienceClass.FAMILY
        else -> AudienceClass.GENERAL
    }

    fun allows(audience: AudienceClass?, categories: Set<PromptCategory>, adultAuthorized: Boolean): Boolean {
        val familyOnly = categories.isNotEmpty() && categories.all {
            it == PromptCategory.KIDS || it == PromptCategory.FAMILY
        }
        if (familyOnly) return audience == AudienceClass.FAMILY
        return when (audience) {
            AudienceClass.FAMILY, AudienceClass.GENERAL -> true
            AudienceClass.ADULT_COUPLES, null -> adultAuthorized
        }
    }
}
