package com.spinbottle.truthdare.games.billing

internal object PremiumEntitlementResolver {
    const val MONTHLY_PRODUCT_ID = "remove_ads_monthly"
    const val LIFETIME_PRODUCT_ID = "remove_ads_lifetime"

    private val premiumProductIds = setOf(
        MONTHLY_PRODUCT_ID,
        LIFETIME_PRODUCT_ID
    )

    fun hasPremium(activeProductIds: Set<String>): Boolean {
        return activeProductIds.any(premiumProductIds::contains)
    }
}
