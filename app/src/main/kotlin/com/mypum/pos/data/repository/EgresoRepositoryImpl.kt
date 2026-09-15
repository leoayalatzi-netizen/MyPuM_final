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

    override fun observeAll(): Flow<List<Egreso>> =
        dao.observeAll().map { entities ->
            entities.map {
                Egreso(
                    id = it.id,
                    turnoId = it.turnoId,
                    concepto = it.concepto,
                    monto = it.monto,
                    createdAt = it.createdAt
                )
            }
        }

    override fun byTurno(turnoId: Long): Flow<List<Egreso>> =
        dao.byTurno(turnoId).map { entities ->
            entities.map {
                Egreso(
                    id = it.id,
                    turnoId = it.turnoId,
                    concepto = it.concepto,
                    monto = it.monto,
                    createdAt = it.createdAt
                )
            }
        }

    override suspend fun registrar(egreso: Egreso): Long {
        require(egreso.turnoId > 0L) {
            "No hay un turno válido para registrar el egreso"
        }

        require(egreso.concepto.isNotBlank()) {
            "El concepto del egreso es obligatorio"
        }

        require(egreso.monto > java.math.BigDecimal.ZERO) {
            "El monto del egreso debe ser mayor que cero"
        }

        return dao.insert(
            EgresoEntity(
                id = 0L,
                turnoId = egreso.turnoId,
                concepto = egreso.concepto.trim(),
                monto = egreso.monto,
                createdAt = egreso.createdAt
            )
        )
    }
}
