package com.mypum.pos.domain.usecase.turno

import com.mypum.pos.domain.model.CierreTurno
import com.mypum.pos.domain.model.enums.MetodoPago
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.repository.EgresoRepository
import com.mypum.pos.domain.repository.VentaRepository
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class CalcularCierreUseCase(
    private val ventaRepository: VentaRepository,
    private val egresoRepository: EgresoRepository
) {

    suspend operator fun invoke(
        turno: Turno,
        efectivoContado: BigDecimal
    ): CierreTurno {

        val ventas = ventaRepository.observeAll().first()

        val ventasEfectivo = ventas
            .filter {
                it.turnoId == turno.id &&
                !it.cancelada &&
                it.metodoPago == MetodoPago.EFECTIVO
            }
            .fold(BigDecimal.ZERO) { acumulado, venta ->
                acumulado.add(venta.total)
            }

        val egresos = egresoRepository
            .byTurno(turno.id)
            .first()
            .fold(BigDecimal.ZERO) { acumulado, egreso ->
                acumulado.add(egreso.monto)
            }

        val efectivoEsperado = turno.fondoInicial
            .add(ventasEfectivo)
            .subtract(egresos)

        val diferencia = efectivoContado.subtract(efectivoEsperado)

        return CierreTurno(
            turno = turno,
            efectivoEsperado = efectivoEsperado,
            efectivoContado = efectivoContado,
            diferencia = diferencia
        )
    }
}
