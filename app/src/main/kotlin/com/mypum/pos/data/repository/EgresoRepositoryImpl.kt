package com.mypum.pos.data.repository
import com.mypum.pos.domain.repository.EgresoRepository
import com.mypum.pos.data.local.dao.EgresoDao
import com.mypum.pos.domain.model.Egreso
import com.mypum.pos.data.local.entity.EgresoEntity
class EgresoRepositoryImpl(private val dao:EgresoDao):EgresoRepository { override suspend fun registrar(e:Egreso)=dao.insert(EgresoEntity(e.id,e.turnoId,e.concepto,e.monto,e.createdAt)) }
