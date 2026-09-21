package com.mypum.pos.domain.repository
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.domain.model.*
interface PrecioRepository { suspend fun actualizar(producto:Producto,usuarioId:Long):Long; fun historial(productoId:Long):Flow<List<HistorialPrecio>> }
