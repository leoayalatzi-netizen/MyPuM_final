package com.mypum.pos.domain.repository

import com.mypum.pos.domain.model.Venta
import kotlinx.coroutines.flow.Flow

interface VentaRepository {

    fun observeAll(): Flow<List<Venta>>

    suspend fun byId(id: Long): Venta?

    suspend fun registrar(venta: Venta): Long
}
