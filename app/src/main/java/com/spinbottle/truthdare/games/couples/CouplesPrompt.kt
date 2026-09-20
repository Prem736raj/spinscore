package com.spinbottle.truthdare.games.couples

enum class CouplesPromptType {
    QUESTION,
    CHALLENGE,
    AFFECTION,
    CHOICE
}

enum class IntimacyTier(val level: Int) {
    WARM_UP(1),
    ROMANTIC(2),
    FLIRTY(3),
    AFFECTIONATE(4),
    AFTER_DARK(5)
}

enum class TouchLevel {
    NONE,
    LIGHT,
    AFFECTIONATE
}

enum class CouplesPromptTag {
    CONVERSATION,
    COMPLIMENT,
    MEMORIES,
    FUTURE,
    TRUST,
    FLIRTING,
    PHYSICAL_AFFECTION,
    KISSING,
    MASSAGE,
    DATE_NIGHT,
    AFTER_DARK,
    EMOTIONAL_INTIMACY,
    ROMANTIC,
    HUMOR,
    NO_TOUCH
}

data class CouplesPrompt(
    val id: String,
    val text: String,
    val type: CouplesPromptType,
    val tier: IntimacyTier,
    val touchLevel: TouchLevel = TouchLevel.NONE,
    val tags: Set<CouplesPromptTag> = emptySet(),
    val requiresExplicitConsent: Boolean = false,
    val cooldownWeight: Int = 1
)

