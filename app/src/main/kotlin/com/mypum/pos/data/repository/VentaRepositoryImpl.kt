package com.mypum.pos.data.repository
import com.mypum.pos.domain.repository.VentaRepository
import com.mypum.pos.data.local.dao.VentaDao
import com.mypum.pos.domain.model.*
import com.mypum.pos.data.local.entity.VentaEntity
import kotlinx.coroutines.flow.map
class VentaRepositoryImpl(private val dao:VentaDao):VentaRepository{
 override fun observeAll()=dao.observeAll().map{it.map{e->Venta(e.id,e.turnoId,e.total,e.metodoPago,cancelada=e.cancelada,createdAt=e.createdAt)}}
 override suspend fun registrar(venta:Venta)=dao.insert(VentaEntity(venta.id,venta.turnoId,venta.total,venta.metodoPago,venta.cancelada,venta.createdAt))
}
