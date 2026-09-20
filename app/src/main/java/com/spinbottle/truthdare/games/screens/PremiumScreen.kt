package com.spinbottle.truthdare.games.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.spinbottle.truthdare.games.billing.BillingConnectionState
import com.spinbottle.truthdare.games.billing.BillingManager
import com.spinbottle.truthdare.games.ui.components.ParticleBackground
import com.spinbottle.truthdare.games.ui.theme.AccentGreen
import com.spinbottle.truthdare.games.ui.theme.AccentOrange
import com.spinbottle.truthdare.games.ui.theme.AccentPink
import kotlinx.coroutines.delay

@Composable
fun PremiumScreen(
    billingManager: BillingManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current as Activity
    val products by billingManager.products.collectAsState()
    val isPremium by billingManager.isPremium.collectAsState()
    val billingUiState by billingManager.uiState.collectAsState()

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    val lifetimeProduct = products.find {
        it.productId == BillingManager.REMOVE_ADS_LIFETIME
    }
    val lifetimePrice = lifetimeProduct?.localizedPrice()

    Box(modifier = Modifier.fillMaxSize()) {
        ParticleBackground(
            modifier = Modifier.fillMaxSize(),
            particleCount = 30
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Support Spin Bottle",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(400)) +
                    slideInVertically(
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        initialOffsetY = { 40 }
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Support ongoing development",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Gameplay is not locked behind a purchase in this release. " +
                            "A lifetime purchase gives you supporter status and helps fund future updates.",
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isPremium) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = AccentGreen.copy(alpha = 0.2f)
                            ),
                            border = BorderStroke(1.dp, AccentGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Supporter status active",
                                    tint = AccentGreen,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Supporter Status Active",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "Google Play ownership is the source of truth.",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        when {
                            billingUiState.connectionState == BillingConnectionState.CONNECTING ||
                                billingUiState.isRefreshing -> {
                                CircularProgressIndicator(
                                    color = AccentOrange,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Checking Google Play…",
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }

                            lifetimeProduct != null && lifetimePrice != null -> {
                                PremiumPlanCard(
                                    title = "Lifetime Supporter",
                                    price = lifetimePrice,
                                    description = "One-time purchase",
                                    isPopular = true,
                                    enabled = true,
                                    onClick = {
                                        billingManager.initiatePurchaseFlow(
                                            context,
                                            lifetimeProduct
                                        )
                                    }
                                )
                            }

                            else -> {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.08f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Purchase details are unavailable. " +
                                            "No fallback price is shown because Google Play pricing is localized.",
                                        color = Color.White.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    billingUiState.message?.let { message ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = message,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { billingManager.refreshPurchases() },
                        enabled = !billingUiState.isRefreshing
                    ) {
                        Text(
                            text = if (billingUiState.isRefreshing) {
                                "Restoring…"
                            } else {
                                "Restore purchases"
                            },
                            color = AccentOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Existing monthly purchases remain recognized. " +
                            "New monthly subscriptions are not offered until the app has recurring premium value.",
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun ProductDetails.localizedPrice(): String? {
    return when (productType) {
        BillingClient.ProductType.SUBS ->
            subscriptionOfferDetails
                ?.firstOrNull()
                ?.pricingPhases
                ?.pricingPhaseList
                ?.lastOrNull()
                ?.formattedPrice

        BillingClient.ProductType.INAPP ->
            oneTimePurchaseOfferDetailsList
                ?.firstOrNull()
                ?.formattedPrice

        else -> null
    }
}

@Composable
private fun PremiumPlanCard(
    title: String,
    price: String,
    description: String,
    isPopular: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.55f)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = if (isPopular) {
                        listOf(
                            AccentPink.copy(alpha = 0.3f),
                            AccentOrange.copy(alpha = 0.3f)
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    }
                )
            )
            .border(
                width = if (isPopular) 2.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = if (isPopular) {
                        listOf(AccentPink, AccentOrange)
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    }
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(20.dp)
    ) {
        if (isPopular) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(AccentOrange, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "SUPPORTER",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = price,
                color = AccentOrange,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
