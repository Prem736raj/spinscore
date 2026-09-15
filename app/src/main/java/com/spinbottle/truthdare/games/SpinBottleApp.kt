package com.spinbottle.truthdare.games

import androidx.compose.runtime.Composable
import com.spinbottle.truthdare.games.navigation.SpinBottleNavHost

import com.spinbottle.truthdare.games.billing.BillingManager

@Composable
fun SpinBottleApp(billingManager: BillingManager) {
    SpinBottleNavHost(billingManager = billingManager)
}
