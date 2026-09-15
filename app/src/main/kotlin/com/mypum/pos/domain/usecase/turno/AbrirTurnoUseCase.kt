package com.mypum.pos.domain.usecase.turno

import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.repository.TurnoRepository

class AbrirTurnoUseCase(
    private val repository: TurnoRepository
) {
    suspend operator fun invoke(turno: Turno): Long =
        repository.abrir(turno)
}
