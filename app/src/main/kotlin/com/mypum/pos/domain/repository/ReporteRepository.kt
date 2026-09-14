package com.mypum.pos.domain.repository
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.domain.model.*
interface ReporteRepository { suspend fun topProductos():List<ProductoTop> }