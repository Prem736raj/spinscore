package com.spinbottle.truthdare.games.data

/**
 * Domain-level audience classification to guarantee complete, non-bypassable
 * isolation between Family-Friendly content and Adult/Couples party content.
 */
enum class AudienceClass {
    FAMILY,
    GENERAL,
    ADULT_COUPLES
}

