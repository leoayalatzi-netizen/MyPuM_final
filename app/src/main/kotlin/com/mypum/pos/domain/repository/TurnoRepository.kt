package com.mypum.pos.domain.repository
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.domain.model.*
interface TurnoRepository { fun observeActivo():Flow<Turno?>; suspend fun abrir(turno:Turno):Long; suspend fun cerrar(turno:Turno) }