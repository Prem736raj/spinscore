package com.spinbottle.truthdare.games.couples

object CouplesPromptValidator {
    fun validate(prompt: CouplesPrompt): List<String> {
        val errors = mutableListOf<String>()

        if (prompt.id.isBlank()) errors += "Prompt id is blank"
        if (prompt.text.trim().length < 10) errors += "Prompt too short: '${prompt.text}'"

        if (
            prompt.touchLevel != TouchLevel.NONE &&
            !prompt.requiresExplicitConsent
        ) errors += "Physical prompt '${prompt.id}' must require consent"

        if (
            CouplesPromptTag.KISSING in prompt.tags &&
            !prompt.requiresExplicitConsent
        ) errors += "Kissing prompt '${prompt.id}' must require consent"

        if (
            CouplesPromptTag.MASSAGE in prompt.tags &&
            !prompt.requiresExplicitConsent
        ) errors += "Massage prompt '${prompt.id}' must require consent"

        return errors
    }
}
