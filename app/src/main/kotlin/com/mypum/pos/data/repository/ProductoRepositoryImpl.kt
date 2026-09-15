package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.ProductoDao
import com.mypum.pos.data.mapper.toDomain
import com.mypum.pos.data.mapper.toEntity
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductoRepositoryImpl(
    private val dao: ProductoDao
) : ProductoRepository {

    override fun observeAll(): Flow<List<Producto>> =
        dao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun buscarPorCodigo(codigo: String): Producto? =
        dao.byCodigo(codigo)?.toDomain()

    override suspend fun guardar(producto: Producto): Long =
        dao.insert(producto.toEntity())
}
