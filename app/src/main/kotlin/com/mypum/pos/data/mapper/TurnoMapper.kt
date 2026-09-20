package com.mypum.pos.data.mapper
import com.mypum.pos.data.local.entity.TurnoEntity
import com.mypum.pos.domain.model.Turno
fun TurnoEntity.toDomain()=Turno(id,usuarioId,fondoInicial,abierto,openedAt,closedAt)