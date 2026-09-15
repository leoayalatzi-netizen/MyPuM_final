package com.mypum.pos.feature.venta

import com.mypum.pos.core.UiEffect
import com.mypum.pos.core.UiEvent
import com.mypum.pos.core.UiState
import com.mypum.pos.domain.model.ItemCarrito
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.model.enums.MetodoPago
import com.mypum.pos.domain.model.Turno
import java.math.BigDecimal

data class VentaContractState(
    val loading: Boolean = true,
    val productos: List<Producto> = emptyList(),
    val carrito: List<ItemCarrito> = emptyList(),
    val query: String = "",
    val turno: Turno? = null,
    val showCheckout: Boolean = false,
    val message: String? = null,
    val lastSaleId: Long? = null
) : UiState {
    val total: BigDecimal get() = carrito.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.subtotal) }
}

sealed interface VentaContractEvent : UiEvent
sealed interface VentaContractEffect : UiEffect
