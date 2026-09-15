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
import java.time.ZoneId

data class ReporteTurno(
    val turno: Turno,
    val ventas: List<Venta>,
    val egresos: List<Egreso>
) {
    val ventasValidas: List<Venta>
        get() = ventas.filterNot { it.cancelada }

    val totalVentas: BigDecimal
        get() = ventasValidas.fold(BigDecimal.ZERO) { acc, v ->
            acc.add(v.total)
        }

    val totalEgresos: BigDecimal
        get() = egresos.fold(BigDecimal.ZERO) { acc, e ->
            acc.add(e.monto)
        }

    val efectivo: BigDecimal
        get() = ventasValidas
            .filter { it.metodoPago.name == "EFECTIVO" }
            .fold(BigDecimal.ZERO) { acc, v -> acc.add(v.total) }

    val tarjeta: BigDecimal
        get() = ventasValidas
            .filter { it.metodoPago.name == "TARJETA" }
            .fold(BigDecimal.ZERO) { acc, v -> acc.add(v.total) }

    val transferencia: BigDecimal
        get() = ventasValidas
            .filter { it.metodoPago.name == "TRANSFERENCIA" }
            .fold(BigDecimal.ZERO) { acc, v -> acc.add(v.total) }

    val efectivoEsperado: BigDecimal
        get() = turno.fondoInicial
            .add(efectivo)
            .subtract(totalEgresos)

    val fecha: LocalDate
        get() = turno.openedAt
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
}

data class ReporteDia(
    val fecha: LocalDate,
    val turnos: List<ReporteTurno>
) {
    val ventas: BigDecimal
        get() = turnos.fold(BigDecimal.ZERO) { acc, t ->
            acc.add(t.totalVentas)
        }

    val egresos: BigDecimal
        get() = turnos.fold(BigDecimal.ZERO) { acc, t ->
            acc.add(t.totalEgresos)
        }

    val efectivo: BigDecimal
        get() = turnos.fold(BigDecimal.ZERO) { acc, t ->
            acc.add(t.efectivo)
        }

    val tarjeta: BigDecimal
        get() = turnos.fold(BigDecimal.ZERO) { acc, t ->
            acc.add(t.tarjeta)
        }

    val transferencia: BigDecimal
        get() = turnos.fold(BigDecimal.ZERO) { acc, t ->
            acc.add(t.transferencia)
        }

    val efectivoEsperado: BigDecimal
        get() = turnos.fold(BigDecimal.ZERO) { acc, t ->
            acc.add(t.efectivoEsperado)
        }

    val numeroVentas: Int
        get() = turnos.sumOf { it.ventasValidas.size }
}

data class ReportesContractState(
    val loading: Boolean = true,
    val ventas: List<Venta> = emptyList(),
    val productos: List<Producto> = emptyList(),
    val topProductos: List<ProductoTop> = emptyList(),
    val turnos: List<Turno> = emptyList(),
    val egresos: List<Egreso> = emptyList(),
    val dias: List<ReporteDia> = emptyList(),
    val turnoSeleccionadoId: Long? = null,
    val message: String? = null
) : UiState {

    val totalVendido: BigDecimal
        get() = ventas.filterNot { it.cancelada }
            .fold(BigDecimal.ZERO) { acc, venta ->
                acc.add(venta.total)
            }

    val ticketPromedio: BigDecimal
        get() {
            val validas = ventas.count { !it.cancelada }
            return if (validas == 0) {
                BigDecimal.ZERO
            } else {
                totalVendido.divide(
                    BigDecimal(validas),
                    2,
                    java.math.RoundingMode.HALF_UP
                )
            }
        }

    val turnoSeleccionado: ReporteTurno?
        get() = dias
            .asSequence()
            .flatMap { it.turnos.asSequence() }
            .firstOrNull { it.turno.id == turnoSeleccionadoId }
}

sealed interface ReportesContractEvent : UiEvent
sealed interface ReportesContractEffect : UiEffect
