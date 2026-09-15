package com.mypum.pos.domain.repository

import com.mypum.pos.domain.model.Egreso
import kotlinx.coroutines.flow.Flow

interface EgresoRepository {

    suspend fun registrar(egreso: Egreso): Long

    fun byTurno(turnoId: Long): Flow<List<Egreso>>
}
