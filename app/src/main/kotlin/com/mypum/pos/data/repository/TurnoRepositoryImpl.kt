package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.TurnoDao
import com.mypum.pos.data.local.entity.TurnoEntity
import com.mypum.pos.data.mapper.toDomain
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.repository.TurnoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TurnoRepositoryImpl(
    private val dao: TurnoDao
) : TurnoRepository {

    override fun observeActivo(): Flow<Turno?> =
        dao.observeActivo().map { it?.toDomain() }

    override fun observeAll(): Flow<List<Turno>> =
        dao.observeAll().map { lista ->
            lista.map { it.toDomain() }
        }

    override suspend fun abrir(turno: Turno): Long =
        dao.insert(
            TurnoEntity(
                turno.id,
                turno.usuarioId,
                turno.fondoInicial,
                turno.abierto,
                turno.openedAt,
                turno.closedAt
            )
        )

    override suspend fun cerrar(turno: Turno) =
        dao.update(
            TurnoEntity(
                turno.id,
                turno.usuarioId,
                turno.fondoInicial,
                false,
                turno.openedAt,
                turno.closedAt
            )
        )
}
