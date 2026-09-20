package com.spinbottle.truthdare.games.couples

object CouplesContentPolicy {

    fun isAllowed(
        prompt: CouplesPrompt,
        prefs: CouplesPreferences
    ): Boolean {

        if (prompt.tier.level > prefs.maxTier.level) return false
        if (prompt.tags.any { it in prefs.blockedTags }) return false

        if (
            CouplesPromptTag.FLIRTING in prompt.tags &&
            !prefs.allowFlirtyConversation
        ) return false

        if (
            prompt.touchLevel == TouchLevel.LIGHT &&
            !prefs.allowLightTouch
        ) return false

        if (
            prompt.touchLevel == TouchLevel.AFFECTIONATE &&
            !prefs.allowAffection
        ) return false

        if (
            CouplesPromptTag.KISSING in prompt.tags &&
            !prefs.allowKissing
        ) return false

        if (
            CouplesPromptTag.MASSAGE in prompt.tags &&
            !prefs.allowMassage
        ) return false

        if (
            CouplesPromptTag.AFTER_DARK in prompt.tags &&
            !prefs.allowAfterDarkConversation
        ) return false

        return true
    }
}

