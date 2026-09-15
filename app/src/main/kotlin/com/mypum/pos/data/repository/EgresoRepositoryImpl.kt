package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.EgresoDao
import com.mypum.pos.data.local.entity.EgresoEntity
import com.mypum.pos.domain.model.Egreso
import com.mypum.pos.domain.repository.EgresoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EgresoRepositoryImpl(
    private val dao: EgresoDao
) : EgresoRepository {

    override suspend fun registrar(e: Egreso): Long {
        return dao.insert(
            EgresoEntity(
                e.id,
                e.turnoId,
                e.concepto,
                e.monto,
                e.createdAt
            )
        )
    }

    override fun byTurno(turnoId: Long): Flow<List<Egreso>> {
        return dao.byTurno(turnoId).map { lista ->
            lista.map {
                Egreso(
                    id = it.id,
                    turnoId = it.turnoId,
                    concepto = it.concepto,
                    monto = it.monto,
                    createdAt = it.createdAt
                )
            }
        }
    }
}
