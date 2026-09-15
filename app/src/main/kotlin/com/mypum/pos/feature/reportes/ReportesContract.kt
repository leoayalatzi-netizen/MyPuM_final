package com.mypum.pos.feature.reportes

import com.mypum.pos.core.UiEffect
import com.mypum.pos.core.UiEvent
import com.mypum.pos.core.UiState
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.model.ProductoTop
import com.mypum.pos.domain.model.Venta
import java.math.BigDecimal

data class ReportesContractState(
    val loading: Boolean = true,
    val ventas: List<Venta> = emptyList(),
    val productos: List<Producto> = emptyList(),
    val topProductos: List<ProductoTop> = emptyList(),
    val message: String? = null
) : UiState {
    val totalVendido: BigDecimal get() = ventas.filterNot { it.cancelada }.fold(BigDecimal.ZERO) { acc, venta -> acc.add(venta.total) }
    val ticketPromedio: BigDecimal get() = if (ventas.none { !it.cancelada }) BigDecimal.ZERO else totalVendido.divide(BigDecimal(ventas.count { !it.cancelada }), 2, java.math.RoundingMode.HALF_UP)
}

sealed interface ReportesContractEvent : UiEvent
sealed interface ReportesContractEffect : UiEffect
