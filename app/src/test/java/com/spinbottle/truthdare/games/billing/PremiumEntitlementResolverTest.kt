package com.spinbottle.truthdare.games.billing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumEntitlementResolverTest {

    @Test
    fun noPurchases_isNotPremium() {
        assertFalse(PremiumEntitlementResolver.hasPremium(emptySet()))
    }

    @Test
    fun monthlyOnly_isPremium() {
        assertTrue(
            PremiumEntitlementResolver.hasPremium(
                setOf(PremiumEntitlementResolver.MONTHLY_PRODUCT_ID)
            )
        )
    }

    @Test
    fun lifetimeOnly_isPremium() {
        assertTrue(
            PremiumEntitlementResolver.hasPremium(
                setOf(PremiumEntitlementResolver.LIFETIME_PRODUCT_ID)
            )
        )
    }

    @Test
    fun bothProducts_isPremium() {
        assertTrue(
            PremiumEntitlementResolver.hasPremium(
                setOf(
                    PremiumEntitlementResolver.MONTHLY_PRODUCT_ID,
                    PremiumEntitlementResolver.LIFETIME_PRODUCT_ID
                )
            )
        )
    }

    @Test
    fun revokedOrExpiredOwnership_isNotPremiumWhenProductIsAbsent() {
        assertFalse(
            PremiumEntitlementResolver.hasPremium(
                setOf("unrelated_product")
            )
        )
    }
}
