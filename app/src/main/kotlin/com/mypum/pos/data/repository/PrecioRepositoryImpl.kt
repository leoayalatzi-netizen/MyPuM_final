package com.mypum.pos.data.repository
import com.mypum.pos.domain.repository.PrecioRepository
import com.mypum.pos.data.local.dao.*
import com.mypum.pos.domain.model.*
import com.mypum.pos.data.local.entity.*
import com.mypum.pos.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class PrecioRepositoryImpl(private val p:ProductoDao,private val h:HistorialPrecioDao):PrecioRepository{
 override suspend fun actualizar(producto:Producto,usuarioId:Long)=p.insert(producto.toEntity()).also{h.insert(HistorialPrecioEntity(productoId=producto.id,precioAnterior=producto.costo,precioNuevo=producto.precio,usuarioId=usuarioId,createdAt=producto.updatedAt))}
 override fun historial(productoId:Long)=h.byProducto(productoId).map{list->list.map{HistorialPrecio(it.id,it.productoId,it.precioAnterior,it.precioNuevo,it.usuarioId,it.createdAt)}}
}
