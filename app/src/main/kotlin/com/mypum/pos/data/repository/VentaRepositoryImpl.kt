package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.VentaDao
import com.mypum.pos.domain.model.Venta
import com.mypum.pos.domain.repository.VentaRepository
import kotlinx.coroutines.flow.map

class VentaRepositoryImpl(
    private val dao: VentaDao
) : VentaRepository {

    override fun observeAll() =
        dao.observeAll().map { lista ->
            lista.map { e ->
                Venta(
                    id = e.id,
                    turnoId = e.turnoId,
                    total = e.total,
                    metodoPago = e.metodoPago,
                    cancelada = e.cancelada,
                    createdAt = e.createdAt
                )
            }
        }

    override suspend fun byId(id: Long): Venta? {
        return dao.byId(id)?.let { e ->
            Venta(
                id = e.id,
                turnoId = e.turnoId,
                total = e.total,
                metodoPago = e.metodoPago,
                cancelada = e.cancelada,
                createdAt = e.createdAt
            )
        }
    }

    override suspend fun registrar(venta: Venta): Long {
        return dao.insert(
            com.mypum.pos.data.local.entity.VentaEntity(
                venta.id,
                venta.turnoId,
                venta.total,
                venta.metodoPago,
                venta.cancelada,
                venta.createdAt
            )
        )
    }
}
