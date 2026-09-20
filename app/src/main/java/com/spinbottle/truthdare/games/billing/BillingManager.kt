package com.spinbottle.truthdare.games.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class BillingConnectionState {
    CONNECTING,
    READY,
    UNAVAILABLE
}

data class BillingUiState(
    val connectionState: BillingConnectionState = BillingConnectionState.CONNECTING,
    val isRefreshing: Boolean = false,
    val message: String? = null
)

class BillingManager(
    context: Context,
    private val coroutineScope: CoroutineScope
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
        const val REMOVE_ADS_MONTHLY = PremiumEntitlementResolver.MONTHLY_PRODUCT_ID
        const val REMOVE_ADS_LIFETIME = PremiumEntitlementResolver.LIFETIME_PRODUCT_ID
    }

    private val appContext = context.applicationContext

    private val billingClient = BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    @Volatile
    private var purchaseQueryGeneration = 0

    private var isConnecting = false

    init {
        connectToPlayBilling()
    }

    private fun connectToPlayBilling() {
        if (billingClient.isReady) {
            _uiState.value = _uiState.value.copy(
                connectionState = BillingConnectionState.READY
            )
            refreshPurchases()
            queryProductDetails()
            return
        }
        if (isConnecting) return

        isConnecting = true
        _uiState.value = _uiState.value.copy(
            connectionState = BillingConnectionState.CONNECTING,
            message = null
        )

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                isConnecting = false
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _uiState.value = BillingUiState(
                        connectionState = BillingConnectionState.READY,
                        isRefreshing = true
                    )
                    refreshPurchases()
                    queryProductDetails()
                } else {
                    Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                    _uiState.value = BillingUiState(
                        connectionState = BillingConnectionState.UNAVAILABLE,
                        message = "Google Play Billing is unavailable right now."
                    )
                }
            }

            override fun onBillingServiceDisconnected() {
                isConnecting = false
                Log.d(TAG, "Billing service disconnected.")
                _uiState.value = _uiState.value.copy(
                    connectionState = BillingConnectionState.UNAVAILABLE,
                    isRefreshing = false,
                    message = "Google Play Billing disconnected. Try restoring purchases."
                )
                // Billing 8+ automatic service reconnection is enabled on the client.
            }
        })
    }

    private fun queryProductDetails() {
        if (!billingClient.isReady) return

        // The monthly product remains recognized for existing owners but is not
        // offered to new users until the app has genuine recurring subscription value.
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(REMOVE_ADS_LIFETIME)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _products.value = queryResult.productDetailsList
                if (queryResult.unfetchedProductList.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        message = "Some purchase options are currently unavailable."
                    )
                }
            } else {
                Log.e(TAG, "Product query failed: ${billingResult.debugMessage}")
                _products.value = emptyList()
                _uiState.value = _uiState.value.copy(
                    message = "Purchase options could not be loaded."
                )
            }
        }
    }

    fun refreshPurchases() {
        if (!billingClient.isReady) {
            connectToPlayBilling()
            return
        }

        val generation = ++purchaseQueryGeneration
        _uiState.value = _uiState.value.copy(
            connectionState = BillingConnectionState.READY,
            isRefreshing = true,
            message = null
        )

        data class QueryResult(
            val success: Boolean,
            val purchases: List<Purchase>
        )

        val lock = Any()
        var subscriptionResult: QueryResult? = null
        var inAppResult: QueryResult? = null

        fun finishIfComplete() {
            val subscriptions: QueryResult
            val inApps: QueryResult

            synchronized(lock) {
                subscriptions = subscriptionResult ?: return
                inApps = inAppResult ?: return
            }

            if (generation != purchaseQueryGeneration) return

            if (!subscriptions.success || !inApps.success) {
                // Do not clear a previously verified entitlement based on a partial query.
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    message = "Purchases could not be fully verified. Try again."
                )
                return
            }

            val allPurchases = subscriptions.purchases + inApps.purchases
            processAcknowledgements(allPurchases)

            val activePremiumProducts = allPurchases
                .asSequence()
                .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                .flatMap { it.products.asSequence() }
                .filter {
                    it == REMOVE_ADS_MONTHLY || it == REMOVE_ADS_LIFETIME
                }
                .toSet()

            _isPremium.value =
                PremiumEntitlementResolver.hasPremium(activePremiumProducts)

            val hasPendingPremiumPurchase = allPurchases.any { purchase ->
                purchase.purchaseState == Purchase.PurchaseState.PENDING &&
                    purchase.products.any {
                        it == REMOVE_ADS_MONTHLY || it == REMOVE_ADS_LIFETIME
                    }
            }

            _uiState.value = BillingUiState(
                connectionState = BillingConnectionState.READY,
                isRefreshing = false,
                message = if (hasPendingPremiumPurchase) {
                    "A purchase is pending. Premium activates only after Google Play confirms payment."
                } else {
                    null
                }
            )
        }

        val subscriptionParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(subscriptionParams) { billingResult, purchases ->
            synchronized(lock) {
                subscriptionResult = QueryResult(
                    success = billingResult.responseCode == BillingClient.BillingResponseCode.OK,
                    purchases = purchases
                )
            }
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Subscription ownership query failed: ${billingResult.debugMessage}")
            }
            finishIfComplete()
        }

        val inAppParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(inAppParams) { billingResult, purchases ->
            synchronized(lock) {
                inAppResult = QueryResult(
                    success = billingResult.responseCode == BillingClient.BillingResponseCode.OK,
                    purchases = purchases
                )
            }
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "In-app ownership query failed: ${billingResult.debugMessage}")
            }
            finishIfComplete()
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val updatedPurchases = purchases.orEmpty()
                processAcknowledgements(updatedPurchases)

                val hasPending = updatedPurchases.any {
                    it.purchaseState == Purchase.PurchaseState.PENDING
                }
                _uiState.value = _uiState.value.copy(
                    message = if (hasPending) {
                        "Purchase pending. Google Play will notify the app after payment completes."
                    } else {
                        "Purchase received. Verifying ownership…"
                    }
                )
                refreshPurchases()
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    message = "Purchase canceled."
                )
            }

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                _uiState.value = _uiState.value.copy(
                    message = "Already owned. Restoring purchase…"
                )
                refreshPurchases()
            }

            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                _uiState.value = _uiState.value.copy(
                    connectionState = BillingConnectionState.UNAVAILABLE,
                    isRefreshing = false,
                    message = "Google Play Billing is unavailable on this device."
                )
            }

            else -> {
                Log.e(TAG, "Purchase flow error: ${billingResult.debugMessage}")
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    message = "Purchase could not be completed. Please try again."
                )
            }
        }
    }

    private fun processAcknowledgements(purchases: List<Purchase>) {
        purchases
            .filter { purchase ->
                purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                    !purchase.isAcknowledged &&
                    purchase.products.any {
                        it == REMOVE_ADS_MONTHLY || it == REMOVE_ADS_LIFETIME
                    }
            }
            .forEach { acknowledgePurchase(it.purchaseToken) }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully.")
            } else {
                Log.e(TAG, "Purchase acknowledgement failed: ${billingResult.debugMessage}")
                _uiState.value = _uiState.value.copy(
                    message = "Purchase is active, but acknowledgement failed. Reopen the app while online."
                )
            }
        }
    }

    fun initiatePurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails
    ) {
        if (!billingClient.isReady) {
            _uiState.value = _uiState.value.copy(
                message = "Google Play Billing is not ready yet."
            )
            connectToPlayBilling()
            return
        }

        val offerToken = when (productDetails.productType) {
            BillingClient.ProductType.SUBS ->
                productDetails.subscriptionOfferDetails
                    ?.firstOrNull()
                    ?.offerToken

            BillingClient.ProductType.INAPP ->
                productDetails.oneTimePurchaseOfferDetailsList
                    ?.firstOrNull()
                    ?.offerToken

            else -> null
        }

        if (offerToken == null) {
            _uiState.value = _uiState.value.copy(
                message = "This purchase option is unavailable."
            )
            return
        }

        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _uiState.value = _uiState.value.copy(
                message = "Google Play could not start the purchase flow."
            )
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun close() {
        billingClient.endConnection()
    }
}
