package com.mypum.pos.data.mapper
import com.mypum.pos.data.local.entity.EgresoEntity
import com.mypum.pos.domain.model.Egreso
fun EgresoEntity.toDomain()=Egreso(id,turnoId,concepto,monto,createdAt)
