package com.mypum.pos.data.mapper
import com.mypum.pos.data.local.entity.VentaEntity
import com.mypum.pos.domain.model.Venta
fun VentaEntity.toDomain()=Venta(id,turnoId,total,metodoPago,cancelada=cancelada,createdAt=createdAt)
