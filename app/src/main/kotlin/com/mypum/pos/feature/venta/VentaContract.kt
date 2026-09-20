package com.mypum.pos.feature.venta

import com.mypum.pos.core.UiEffect
import com.mypum.pos.core.UiEvent
import com.mypum.pos.core.UiState
import com.mypum.pos.domain.model.ItemCarrito
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.model.Turno
import java.math.BigDecimal

data class VentaContractState(
    val loading: Boolean = false,
    val message: String? = null,
    val turno: Turno? = null,
    val productos: List<Producto> = emptyList(),
    val query: String = "",
    val carrito: List<ItemCarrito> = emptyList(),
    val total: BigDecimal = BigDecimal.ZERO,
    val showCheckout: Boolean = false
) : UiState

sealed interface VentaContractEvent : UiEvent

sealed interface VentaContractEffect : UiEffect
