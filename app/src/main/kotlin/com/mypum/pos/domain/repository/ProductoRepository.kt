package com.mypum.pos.domain.repository

import com.mypum.pos.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {

    fun observeAll(): Flow<List<Producto>>

    suspend fun buscarPorCodigo(codigo: String): Producto?

    suspend fun guardar(producto: Producto): Long

    suspend fun eliminar(producto: Producto)
}
