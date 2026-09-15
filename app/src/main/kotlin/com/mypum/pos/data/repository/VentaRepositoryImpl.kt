package com.mypum.pos.data.repository

import androidx.room.withTransaction
import com.mypum.pos.data.local.PosDatabase
import com.mypum.pos.data.local.dao.ProductoDao
import com.mypum.pos.data.local.dao.VentaDao
import com.mypum.pos.data.local.entity.DetalleVentaEntity
import com.mypum.pos.data.local.entity.VentaEntity
import com.mypum.pos.domain.model.Venta
import com.mypum.pos.domain.repository.VentaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.Instant

class VentaRepositoryImpl(
    private val dao: VentaDao,
    private val productoDao: ProductoDao,
    private val db: PosDatabase
) : VentaRepository {
    override fun observeAll(): Flow<List<Venta>> =
        dao.observeAll().map { entities ->
            entities.map {
                Venta(
                    id = it.id,
                    turnoId = it.turnoId,
                    total = it.total,
                    metodoPago = it.metodoPago,
                    cancelada = it.cancelada,
                    createdAt = it.createdAt
                )
            }
        }

    override suspend fun registrar(venta: Venta): Long = db.withTransaction {
        require(venta.items.isNotEmpty()) { "La venta no contiene productos" }

        venta.items.forEach { item ->
            val actual = productoDao.byId(item.producto.id)
                ?: error("Producto no encontrado: ${item.producto.nombre}")
            val nuevoStock = actual.stock.subtract(item.cantidad)
            require(nuevoStock >= BigDecimal.ZERO) {
                "Stock insuficiente para ${actual.nombre}"
            }
        }

        val ventaId = dao.insert(
            VentaEntity(
                turnoId = venta.turnoId,
                total = venta.total,
                metodoPago = venta.metodoPago,
                cancelada = venta.cancelada,
                createdAt = venta.createdAt
            )
        )

        dao.insertDetalles(
            venta.items.map { item ->
                DetalleVentaEntity(
                    ventaId = ventaId,
                    productoId = item.producto.id,
                    cantidad = item.cantidad,
                    pesoGramos = item.pesoGramos,
                    precioUnitario = item.producto.precio,
                    subtotal = item.subtotal
                )
            }
        )

        venta.items.forEach { item ->
            val actual = productoDao.byId(item.producto.id)
                ?: error("Producto no encontrado")
            productoDao.update(
                actual.copy(
                    stock = actual.stock.subtract(item.cantidad),
                    updatedAt = Instant.now()
                )
            )
        }

        ventaId
    }
}
