package com.mypum.pos.data.mapper

import com.mypum.pos.data.local.entity.TurnoEntity
import com.mypum.pos.domain.model.Turno

fun TurnoEntity.toDomain() =
    Turno(
        id = id,
        usuarioId = usuarioId,
        fondoInicial = fondoInicial,
        abierto = abierto,
        openedAt = openedAt,
        closedAt = closedAt,
        efectivoContado = efectivoContado,
        diferencia = diferencia
    )
