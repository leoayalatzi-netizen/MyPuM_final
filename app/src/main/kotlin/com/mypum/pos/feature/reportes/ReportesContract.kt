package com.mypum.pos.feature.reportes

import com.mypum.pos.core.UiEffect
import com.mypum.pos.core.UiEvent
import com.mypum.pos.core.UiState
import com.mypum.pos.domain.model.Egreso
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.model.ProductoTop
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.model.Venta
import java.math.BigDecimal
import java.time.LocalDate

data class ReportesContractState(
    val loading: Boolean = true,
    val message: String? = null,

    val ventas: List<Venta> = emptyList(),
    val productos: List<Producto> = emptyList(),
    val topProductos: List<ProductoTop> = emptyList(),
    val turnos: List<Turno> = emptyList(),
    val egresos: List<Egreso> = emptyList(),
    val dias: List<ReporteDia> = emptyList(),

    val totalVentas: BigDecimal = BigDecimal.ZERO,
    val efectivo: BigDecimal = BigDecimal.ZERO,
    val tarjeta: BigDecimal = BigDecimal.ZERO,
    val transferencia: BigDecimal = BigDecimal.ZERO,
    val totalEgresos: BigDecimal = BigDecimal.ZERO,
    val neto: BigDecimal = BigDecimal.ZERO,

    // Métricas PRO
    val operaciones: Int = 0,
    val ticketPromedio: BigDecimal = BigDecimal.ZERO,
    val costoMercancia: BigDecimal = BigDecimal.ZERO,
    val utilidadBruta: BigDecimal = BigDecimal.ZERO,
    val margenBruto: BigDecimal = BigDecimal.ZERO,
    val productosStockBajo: List<Producto> = emptyList()
,
    val turnoSeleccionadoId: Long? = null
) : UiState

data class ReporteTurno(
    val turno: Turno,
    val ventas: List<Venta>,
    val egresos: List<Egreso>
) {
    val fecha: LocalDate
        get() = turno.openedAt
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
}

data class ReporteDia(
    val fecha: LocalDate,
    val turnos: List<ReporteTurno>
)

sealed interface ReportesContractEvent : UiEvent

sealed interface ReportesContractEffect : UiEffect
