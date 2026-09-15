package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.TurnoDao
import com.mypum.pos.data.local.entity.TurnoEntity
import com.mypum.pos.data.mapper.toDomain
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.repository.TurnoRepository
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TurnoRepositoryImpl(
    private val dao: TurnoDao
) : TurnoRepository {

    override fun observeActivo(): Flow<Turno?> =
        dao.observeActivo().map { it?.toDomain() }

    override fun observeAll(): Flow<List<Turno>> =
        dao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun abrir(turno: Turno): Long =
        dao.insert(
            TurnoEntity(
                id = 0L,
                usuarioId = turno.usuarioId,
                fondoInicial = turno.fondoInicial,
                abierto = true,
                openedAt = turno.openedAt,
                closedAt = null,
                efectivoContado = null,
                diferencia = null
            )
        )

    override suspend fun cerrar(turno: Turno) {
        require(turno.id > 0L) {
            "El turno no tiene un ID válido"
        }

        dao.update(
            TurnoEntity(
                id = turno.id,
                usuarioId = turno.usuarioId,
                fondoInicial = turno.fondoInicial,
                abierto = false,
                openedAt = turno.openedAt,
                closedAt = turno.closedAt ?: Instant.now(),
                efectivoContado = turno.efectivoContado,
                diferencia = turno.diferencia
            )
        )
    }
}
