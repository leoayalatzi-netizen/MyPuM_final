package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.TurnoDao
import com.mypum.pos.data.local.entity.TurnoEntity
import com.mypum.pos.data.mapper.toDomain
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.repository.TurnoRepository
import kotlinx.coroutines.flow.map

class TurnoRepositoryImpl(
    private val dao: TurnoDao
) : TurnoRepository {

    override fun observeActivo() =
        dao.observeActivo().map { it?.toDomain() }

    override suspend fun abrir(turno: Turno): Long =
        dao.insert(
            TurnoEntity(
                id = turno.id,
                usuarioId = turno.usuarioId,
                fondoInicial = turno.fondoInicial,
                abierto = true,
                openedAt = turno.openedAt,
                closedAt = null
            )
        )

    override suspend fun cerrar(turno: Turno) =
        dao.update(
            TurnoEntity(
                id = turno.id,
                usuarioId = turno.usuarioId,
                fondoInicial = turno.fondoInicial,
                abierto = false,
                openedAt = turno.openedAt,
                closedAt = turno.closedAt
            )
        )
}
