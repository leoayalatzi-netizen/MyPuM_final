package com.mypum.pos.domain.usecase.turno

import com.mypum.pos.domain.model.Egreso
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.model.Venta
import com.mypum.pos.domain.model.enums.MetodoPago
import java.math.BigDecimal

data class CierreTurnoResult(
    val turnoId: Long,
    val fondoInicial: BigDecimal,
    val ventas: BigDecimal,
    val efectivo: BigDecimal,
    val tarjeta: BigDecimal,
    val transferencia: BigDecimal,
    val egresos: BigDecimal,
    val numeroVentas: Int,
    val ventasCanceladas: Int,
    val efectivoEsperado: BigDecimal
)

class CalcularCierreUseCase {

    operator fun invoke(
        turno: Turno,
        ventas: List<Venta>,
        egresos: List<Egreso>
    ): CierreTurnoResult {

        val ventasValidas = ventas.filter {
            it.turnoId == turno.id && !it.cancelada
        }

        val egresosTurno = egresos.filter {
            it.turnoId == turno.id
        }

        val ventasTotal = ventasValidas.fold(BigDecimal.ZERO) { acc, venta ->
            acc.add(venta.total)
        }

        val efectivo = ventasValidas
            .filter { it.metodoPago == MetodoPago.EFECTIVO }
            .fold(BigDecimal.ZERO) { acc, venta -> acc.add(venta.total) }

        val tarjeta = ventasValidas
            .filter { it.metodoPago == MetodoPago.TARJETA }
            .fold(BigDecimal.ZERO) { acc, venta -> acc.add(venta.total) }

        val transferencia = ventasValidas
            .filter { it.metodoPago == MetodoPago.TRANSFERENCIA }
            .fold(BigDecimal.ZERO) { acc, venta -> acc.add(venta.total) }

        val totalEgresos = egresosTurno.fold(BigDecimal.ZERO) { acc, egreso ->
            acc.add(egreso.monto)
        }

        val efectivoEsperado =
            turno.fondoInicial
                .add(efectivo)
                .subtract(totalEgresos)

        return CierreTurnoResult(
            turnoId = turno.id,
            fondoInicial = turno.fondoInicial,
            ventas = ventasTotal,
            efectivo = efectivo,
            tarjeta = tarjeta,
            transferencia = transferencia,
            egresos = totalEgresos,
            numeroVentas = ventasValidas.size,
            ventasCanceladas = ventas.count {
                it.turnoId == turno.id && it.cancelada
            },
            efectivoEsperado = efectivoEsperado
        )
    }
}
