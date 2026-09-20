package com.mypum.pos.data.repository
import com.mypum.pos.domain.repository.ReporteRepository
import com.mypum.pos.domain.model.ProductoTop
class ReporteRepositoryImpl:ReporteRepository { override suspend fun topProductos()=emptyList<ProductoTop>() }
