package com.mypum.pos.domain.repository

import com.mypum.pos.domain.model.ProductoTop

interface ReporteRepository {
    suspend fun topProductos(): List<ProductoTop>
}
