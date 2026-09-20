package com.mypum.pos.domain.usecase.turno

import com.mypum.pos.domain.model.CierreTurno
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.repository.TurnoRepository
import java.time.Instant

class CerrarTurnoUseCase(
    private val turnoRepository: TurnoRepository,
    private val calcularCierre: CalcularCierreUseCase
) {

    suspend operator fun invoke(
        turno: Turno,
        efectivoContado: java.math.BigDecimal
    ): CierreTurno {

        val cierre = calcularCierre(
            turno = turno,
            efectivoContado = efectivoContado
        )

        turnoRepository.cerrar(
            turno.copy(
                abierto = false,
                closedAt = Instant.now()
            )
        )

        return cierre
    }
}
