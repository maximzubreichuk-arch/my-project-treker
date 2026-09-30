package com.example.myprojecttreker.data.subscription

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Google Play Billing integration shell.
 * Product ID must be created in Play Console before a real purchase is available.
 */
class BillingManager(
    context: Context,
    private val subscriptionManager: SubscriptionManager
) {
    private val _product = MutableStateFlow<ProductDetails?>(null)
    val product: StateFlow<ProductDetails?> = _product.asStateFlow()

    private val listener = PurchasesUpdatedListener { result, purchases ->
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            processPurchases(purchases)
        }
    }

    private val billingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener(listener)
        .enablePendingPurchases(
            com.android.billingclient.api.PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    private var connectionStarted = false
    private var connectionReady = false

    fun start() {
        if (connectionStarted || connectionReady) return
        connectionStarted = true
        billingClient.startConnection(object : com.android.billingclient.api.BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                connectionStarted = false
                connectionReady = billingResult.responseCode == BillingClient.BillingResponseCode.OK
                if (connectionReady) {
                    queryProducts()
                    queryPurchases()
                }
            }
            override fun onBillingServiceDisconnected() {
                connectionStarted = false
                connectionReady = false
            }
        })
    }

    fun launchPurchase(activity: Activity): BillingResult? {
        if (!connectionReady) {
            start()
            return null
        }
        val details = _product.value ?: return null
        val offer = details.subscriptionOfferDetails?.firstOrNull() ?: return null
        val params = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offer.offerToken)
            .build()
        return billingClient.launchBillingFlow(
            activity,
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(params))
                .build()
        )
    }

    fun restorePurchases() {
        if (connectionReady) queryPurchases() else start()
    }

    private fun queryProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()
        billingClient.queryProductDetailsAsync(
            params,
            ProductDetailsResponseListener { result, response ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _product.value = response.productDetailsList.firstOrNull()
                }
            }
        )
    }

    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchases)
            }
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        val active = purchases.any { purchase ->
            val activePurchase = purchase.products.contains(PRODUCT_ID) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
            if (activePurchase && !purchase.isAcknowledged) {
                billingClient.acknowledgePurchase(
                    com.android.billingclient.api.AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                ) { }
            }
            activePurchase
        }
        subscriptionManager.setProForTesting(active)
    }

    companion object {
        // Create this subscription ID in Google Play Console before production.
        const val PRODUCT_ID = "myprojecttreker_pro_monthly"
    }
}
