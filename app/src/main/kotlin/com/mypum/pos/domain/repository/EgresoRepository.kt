package com.mypum.pos.domain.repository

import com.mypum.pos.domain.model.Egreso
import kotlinx.coroutines.flow.Flow

interface EgresoRepository {

    fun observeAll(): Flow<List<Egreso>>

    fun byTurno(turnoId: Long): Flow<List<Egreso>>

    suspend fun registrar(egreso: Egreso): Long
}
