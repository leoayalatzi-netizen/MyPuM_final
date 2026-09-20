package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.ProductoDao
import com.mypum.pos.data.mapper.toDomain
import com.mypum.pos.data.mapper.toEntity
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.map

class ProductoRepositoryImpl(
    private val dao: ProductoDao
) : ProductoRepository {

    override fun observeAll() =
        dao.observeAll().map { lista ->
            lista.map { it.toDomain() }
        }

    override suspend fun buscarPorCodigo(codigo: String) =
        dao.byCodigo(codigo)?.toDomain()

    override suspend fun guardar(producto: Producto): Long {
        val entity = producto.toEntity()

        return if (producto.id == 0L) {
            dao.insert(entity)
        } else {
            dao.update(entity)
            producto.id
        }
    }

    override suspend fun eliminar(producto: Producto) {
        dao.delete(producto.toEntity())
    }

    override suspend fun contarProductos(): Int =
        dao.count()
}
