package com.mypum.pos.domain.usecase.turno

import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.repository.TurnoRepository
import java.time.Instant

class CerrarTurnoUseCase(
    private val repository: TurnoRepository
) {
    suspend operator fun invoke(turno: Turno) {
        repository.cerrar(
            turno.copy(
                abierto = false,
                closedAt = turno.closedAt ?: Instant.now()
            )
        )
    }
}
