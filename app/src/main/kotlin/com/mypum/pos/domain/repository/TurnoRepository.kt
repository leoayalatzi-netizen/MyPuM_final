package com.mypum.pos.domain.repository

import com.mypum.pos.domain.model.Turno
import kotlinx.coroutines.flow.Flow

interface TurnoRepository {

    fun observeActivo(): Flow<Turno?>

    fun observeAll(): Flow<List<Turno>>

    suspend fun abrir(turno: Turno): Long

    suspend fun cerrar(turno: Turno)
}
