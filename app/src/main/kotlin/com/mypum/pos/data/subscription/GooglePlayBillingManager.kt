package com.mypum.pos.data.subscription

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.mypum.pos.domain.model.subscription.Plan
import com.mypum.pos.domain.model.subscription.SubscriptionPricing
import com.mypum.pos.domain.repository.subscription.SubscriptionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class GooglePlayBillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subscriptionRepository: SubscriptionRepository
) : PurchasesUpdatedListener {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    /*
     * BillingClient se crea de forma diferida.
     * Así, abrir MyPuM PRO no puede hacer caer la aplicación
     * solamente porque Google Play Billing no esté disponible.
     */
    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()
    }

    fun refreshPurchases() {
        runCatching {
            ensureConnected {
                queryActiveSubscription()
            }
        }.onFailure { error ->
            _message.value =
                error.message?.takeIf { it.isNotBlank() }
                    ?: "Google Play no está disponible en este momento."
        }
    }

    fun launchProPurchase(activity: Activity) {
        _message.value = null

        runCatching {
            ensureConnected {
                queryProductAndLaunch(activity)
            }
        }.onFailure { error ->
            _message.value =
                error.message?.takeIf { it.isNotBlank() }
                    ?: "No se pudo conectar con Google Play."
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        when {
            billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK &&
                purchases != null -> {
                purchases.forEach(::processPurchase)
            }

            billingResult.responseCode ==
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                _message.value = "Compra cancelada."
            }

            else -> {
                _message.value =
                    billingResult.debugMessage.ifBlank {
                        "No se pudo iniciar la compra en Google Play."
                    }
            }
        }
    }

    private fun ensureConnected(onReady: () -> Unit) {
        try {
            if (billingClient.isReady) {
                onReady()
                return
            }

            billingClient.startConnection(
                object : BillingClientStateListener {

                    override fun onBillingSetupFinished(
                        billingResult: BillingResult
                    ) {
                        if (
                            billingResult.responseCode ==
                                BillingClient.BillingResponseCode.OK
                        ) {
                            runCatching {
                                onReady()
                            }.onFailure { error ->
                                _message.value =
                                    error.message?.takeIf {
                                        it.isNotBlank()
                                    } ?: "Error al usar Google Play."
                            }
                        } else {
                            _message.value =
                                billingResult.debugMessage.ifBlank {
                                    "Google Play no está disponible."
                                }
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        _message.value =
                            "Se perdió la conexión con Google Play."
                    }
                }
            )
        } catch (error: Throwable) {
            _message.value =
                error.message?.takeIf { it.isNotBlank() }
                    ?: "No se pudo iniciar Google Play Billing."
        }
    }

    private fun queryProductAndLaunch(activity: Activity) {
        val product =
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(
                    SubscriptionPricing.PRO_ANNUAL_PRODUCT_ID
                )
                .setProductType(
                    BillingClient.ProductType.SUBS
                )
                .build()

        val params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(listOf(product))
                .build()

        billingClient.queryProductDetailsAsync(params) {
                billingResult,
                result ->

            if (
                billingResult.responseCode !=
                    BillingClient.BillingResponseCode.OK
            ) {
                _message.value =
                    billingResult.debugMessage.ifBlank {
                        "No se pudo consultar el plan PRO."
                    }
                return@queryProductDetailsAsync
            }

            val details = result.firstOrNull()

            if (details == null) {
                _message.value =
                    "El plan PRO no está disponible en Google Play."
                return@queryProductDetailsAsync
            }

            val offer =
                details.subscriptionOfferDetails
                    ?.firstOrNull { it.offerId == null }
                    ?: details.subscriptionOfferDetails
                        ?.firstOrNull()

            if (offer == null) {
                _message.value =
                    "El plan PRO no tiene una oferta disponible en Google Play."
                return@queryProductDetailsAsync
            }

            val flowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams
                                .newBuilder()
                                .setProductDetails(details)
                                .setOfferToken(offer.offerToken)
                                .build()
                        )
                    )
                    .build()

            try {
                val launchResult =
                    billingClient.launchBillingFlow(
                        activity,
                        flowParams
                    )

                if (
                    launchResult.responseCode !=
                        BillingClient.BillingResponseCode.OK
                ) {
                    _message.value =
                        launchResult.debugMessage.ifBlank {
                            "No se pudo abrir Google Play."
                        }
                }
            } catch (error: Throwable) {
                _message.value =
                    error.message?.takeIf { it.isNotBlank() }
                        ?: "No se pudo abrir la compra de Google Play."
            }
        }
    }

    private fun queryActiveSubscription() {
        val params =
            QueryPurchasesParams.newBuilder()
                .setProductType(
                    BillingClient.ProductType.SUBS
                )
                .build()

        try {
            billingClient.queryPurchasesAsync(params) {
                    billingResult,
                    purchases ->

                if (
                    billingResult.responseCode !=
                        BillingClient.BillingResponseCode.OK
                ) {
                    return@queryPurchasesAsync
                }

                val proPurchase =
                    purchases.firstOrNull { purchase ->
                        purchase.purchaseState ==
                            Purchase.PurchaseState.PURCHASED &&
                        purchase.products.contains(
                            SubscriptionPricing.PRO_ANNUAL_PRODUCT_ID
                        )
                    }

                if (proPurchase != null) {
                    processPurchase(proPurchase)
                } else {
                    scope.launch {
                        subscriptionRepository.setPlan(Plan.FREE)
                    }
                }
            }
        } catch (error: Throwable) {
            _message.value =
                error.message?.takeIf { it.isNotBlank() }
                    ?: "No se pudieron consultar las compras de Google Play."
        }
    }

    private fun processPurchase(purchase: Purchase) {
        if (
            purchase.purchaseState !=
                Purchase.PurchaseState.PURCHASED ||
            !purchase.products.contains(
                SubscriptionPricing.PRO_ANNUAL_PRODUCT_ID
            )
        ) {
            return
        }

        scope.launch {
            subscriptionRepository.setPlan(Plan.PRO)
            _message.value = null
        }

        if (!purchase.isAcknowledged) {
            val params =
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

            try {
                billingClient.acknowledgePurchase(params) { result ->
                    if (
                        result.responseCode !=
                            BillingClient.BillingResponseCode.OK
                    ) {
                        _message.value =
                            result.debugMessage.ifBlank {
                                "PRO se compró, pero Google Play aún no confirmó la compra."
                            }
                    }
                }
            } catch (error: Throwable) {
                _message.value =
                    error.message?.takeIf { it.isNotBlank() }
                        ?: "No se pudo confirmar la compra con Google Play."
            }
        }
    }
}
