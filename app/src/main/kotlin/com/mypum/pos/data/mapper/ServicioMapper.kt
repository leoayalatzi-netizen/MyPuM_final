package com.mypum.pos.data.mapper
import com.mypum.pos.data.local.entity.ServicioEntity
import com.mypum.pos.domain.model.Servicio
fun ServicioEntity.toDomain()=Servicio(id,turnoId,tipo,referencia,monto,createdAt)
