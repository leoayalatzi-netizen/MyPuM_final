package com.mypum.pos.domain.model.subscription

import java.math.BigDecimal

object SubscriptionPricing {

    val PRO_ANNUAL_PRICE: BigDecimal =
        BigDecimal("99.00")

    const val PRO_ANNUAL_CURRENCY: String = "MXN"

    const val PRO_ANNUAL_PRODUCT_ID: String =
        "mypum_pro_annual"
}
