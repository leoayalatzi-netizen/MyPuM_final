package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.ReporteDao
import com.mypum.pos.domain.model.ProductoTop
import com.mypum.pos.domain.repository.ReporteRepository

class ReporteRepositoryImpl(
    private val dao: ReporteDao
) : ReporteRepository {
    override suspend fun topProductos(): List<ProductoTop> = dao.topProductos()
}
