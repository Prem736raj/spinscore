package com.spinbottle.truthdare.games.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.spinbottle.truthdare.games.billing.BillingManager
import com.spinbottle.truthdare.games.ui.components.GlassButton
import com.spinbottle.truthdare.games.ui.components.ParticleBackground
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    billingManager: BillingManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current as Activity
    val products by billingManager.products.collectAsState()
    val isPremium by billingManager.isPremium.collectAsState()

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ParticleBackground(
            modifier = Modifier.fillMaxSize(),
            particleCount = 50
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
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
                    text = "Go Premium",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(
                            animationSpec = tween(600, easing = FastOutSlowInEasing),
                            initialOffsetY = { 50 }
                        )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Premium Star",
                        tint = AccentOrange,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Unlock the Ultimate Experience",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PremiumFeature(text = "Remove All Ads")
                    PremiumFeature(text = "Uninterrupted Gameplay")
                    PremiumFeature(text = "Support the Developers")

                    Spacer(modifier = Modifier.height(40.dp))

                    if (isPremium) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, AccentGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active",
                                    tint = AccentGreen,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Premium is Active!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    } else {
                        // Find products
                        val monthlyProduct = products.find { it.productId == BillingManager.REMOVE_ADS_MONTHLY }
                        val lifetimeProduct = products.find { it.productId == BillingManager.REMOVE_ADS_LIFETIME }

                        PremiumPlanCard(
                            title = "1 Month",
                            price = monthlyProduct?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice ?: "$1.99",
                            description = "Billed monthly",
                            isPopular = false,
                            onClick = {
                                monthlyProduct?.let { billingManager.initiatePurchaseFlow(context, it) }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PremiumPlanCard(
                            title = "Lifetime",
                            price = lifetimeProduct?.oneTimePurchaseOfferDetails?.formattedPrice ?: "$9.99",
                            description = "Pay once, enjoy forever",
                            isPopular = true,
                            onClick = {
                                lifetimeProduct?.let { billingManager.initiatePurchaseFlow(context, it) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumFeature(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AccentGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 18.sp
        )
    }
}

@Composable
fun PremiumPlanCard(
    title: String,
    price: String,
    description: String,
    isPopular: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = if (isPopular) {
                        listOf(AccentPink.copy(alpha = 0.3f), AccentOrange.copy(alpha = 0.3f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))
                    }
                )
            )
            .border(
                width = if (isPopular) 2.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = if (isPopular) {
                        listOf(AccentPink, AccentOrange)
                    } else {
                        listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f))
                    }
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        if (isPopular) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp)
                    .background(AccentOrange, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "BEST VALUE",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
            Text(
                text = price,
                color = if (isPopular) AccentOrange else Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
