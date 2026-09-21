package com.mypum.pos.domain.usecase.egresos

import com.mypum.pos.domain.model.Egreso
import com.mypum.pos.domain.repository.EgresoRepository

class RegistrarEgresoUseCase(
    private val repository: EgresoRepository
) {
    suspend operator fun invoke(egreso: Egreso): Long {
        return repository.registrar(egreso)
    }
}
