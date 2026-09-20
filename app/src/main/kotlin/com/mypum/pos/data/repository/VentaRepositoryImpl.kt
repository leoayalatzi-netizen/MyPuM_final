package com.mypum.pos.data.repository

import androidx.room.withTransaction
import com.mypum.pos.data.local.PosDatabase
import com.mypum.pos.data.local.dao.DetalleVentaDao
import com.mypum.pos.data.local.dao.ProductoDao
import com.mypum.pos.data.local.dao.VentaDao
import com.mypum.pos.data.local.entity.DetalleVentaEntity
import com.mypum.pos.data.local.entity.VentaEntity
import com.mypum.pos.data.mapper.toEntity
import com.mypum.pos.domain.model.Venta
import com.mypum.pos.domain.repository.VentaRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VentaRepositoryImpl @Inject constructor(
    private val dao: VentaDao,
    private val productoDao: ProductoDao,
    private val detalleVentaDao: DetalleVentaDao,
    private val database: PosDatabase
) : VentaRepository {

    override fun observeAll() =
        dao.observeAll().map { lista ->
            lista.map { entity ->
                Venta(
                    id = entity.id,
                    turnoId = entity.turnoId,
                    total = entity.total,
                    metodoPago = entity.metodoPago,
                    cancelada = entity.cancelada,
                    createdAt = entity.createdAt
                )
            }
        }

    override suspend fun byId(id: Long): Venta? {
        return dao.byId(id)?.let { entity ->
            Venta(
                id = entity.id,
                turnoId = entity.turnoId,
                total = entity.total,
                metodoPago = entity.metodoPago,
                cancelada = entity.cancelada,
                createdAt = entity.createdAt
            )
        }
    }

    override suspend fun registrar(venta: Venta): Long {

        require(venta.items.isNotEmpty()) {
            "No se puede registrar una venta sin productos"
        }

        return database.withTransaction {

            /*
             * 1. Registrar encabezado de venta.
             */
            val ventaId = dao.insert(
                VentaEntity(
                    id = 0L,
                    turnoId = venta.turnoId,
                    total = venta.total,
                    metodoPago = venta.metodoPago,
                    cancelada = venta.cancelada,
                    createdAt = venta.createdAt
                )
            )

            /*
             * 2. Registrar todos los detalles.
             */
            val detalles = venta.items.map { item ->
                DetalleVentaEntity(
                    id = 0L,
                    ventaId = ventaId,
                    productoId = item.producto.id,
                    cantidad = item.cantidad,
                    pesoGramos = item.pesoGramos,
                    precioUnitario = item.producto.precio,
                    subtotal = item.subtotal
                )
            }

            detalleVentaDao.insertAll(detalles)

            /*
             * 3. Descontar inventario.
             */
            venta.items.forEach { item ->

                val nuevoStock =
                    item.producto.stock.subtract(item.cantidad)

                require(nuevoStock >= java.math.BigDecimal.ZERO) {
                    "Stock insuficiente para ${item.producto.nombre}"
                }

                productoDao.update(
                    item.producto
                        .copy(stock = nuevoStock)
                        .toEntity()
                )
            }

            ventaId
        }
    }
}
