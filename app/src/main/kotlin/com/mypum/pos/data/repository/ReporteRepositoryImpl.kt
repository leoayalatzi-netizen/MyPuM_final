package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.ReporteDao
import com.mypum.pos.domain.model.ProductoTop
import com.mypum.pos.domain.repository.ReporteRepository
import javax.inject.Inject

class ReporteRepositoryImpl @Inject constructor(
    private val dao: ReporteDao
) : ReporteRepository {

    override suspend fun topProductos(): List<ProductoTop> {
        return dao.topProductos()
    }
}
