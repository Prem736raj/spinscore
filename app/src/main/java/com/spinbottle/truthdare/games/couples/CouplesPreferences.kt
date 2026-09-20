package com.spinbottle.truthdare.games.couples

data class CouplesPreferences(
    val maxTier: IntimacyTier = IntimacyTier.ROMANTIC,
    val allowFlirtyConversation: Boolean = true,
    val allowLightTouch: Boolean = false,
    val allowAffection: Boolean = false,
    val allowKissing: Boolean = false,
    val allowMassage: Boolean = false,
    val allowAfterDarkConversation: Boolean = false,
    val blockedTags: Set<CouplesPromptTag> = emptySet()
)

private fun lowerTier(
    a: IntimacyTier,
    b: IntimacyTier
): IntimacyTier =
    if (a.level <= b.level) a else b

fun CouplesPreferences.intersect(
    other: CouplesPreferences
): CouplesPreferences =
    CouplesPreferences(
        maxTier = lowerTier(maxTier, other.maxTier),
        allowFlirtyConversation =
            allowFlirtyConversation && other.allowFlirtyConversation,
        allowLightTouch =
            allowLightTouch && other.allowLightTouch,
        allowAffection =
            allowAffection && other.allowAffection,
        allowKissing =
            allowKissing && other.allowKissing,
        allowMassage =
            allowMassage && other.allowMassage,
        allowAfterDarkConversation =
            allowAfterDarkConversation && other.allowAfterDarkConversation,
        blockedTags = blockedTags + other.blockedTags
    )

