package com.mypum.pos.data.repository
import com.mypum.pos.domain.repository.ServicioRepository
import com.mypum.pos.data.local.dao.ServicioDao
import com.mypum.pos.domain.model.Servicio
import com.mypum.pos.data.local.entity.ServicioEntity
class ServicioRepositoryImpl(private val dao:ServicioDao):ServicioRepository { override suspend fun registrar(s:Servicio)=dao.insert(ServicioEntity(s.id,s.turnoId,s.tipo,s.referencia,s.monto,s.createdAt)) }
