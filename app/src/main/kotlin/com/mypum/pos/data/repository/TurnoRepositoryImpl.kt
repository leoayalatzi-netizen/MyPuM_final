package com.mypum.pos.data.repository
import com.mypum.pos.domain.repository.TurnoRepository
import com.mypum.pos.data.local.dao.TurnoDao
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.data.mapper.toDomain
import com.mypum.pos.data.local.entity.TurnoEntity
import kotlinx.coroutines.flow.map
class TurnoRepositoryImpl(private val dao:TurnoDao):TurnoRepository{
 override fun observeActivo()=dao.observeActivo().map{it?.toDomain()}
 override suspend fun abrir(t:Turno)=dao.insert(TurnoEntity(t.id,t.usuarioId,t.fondoInicial,t.abierto,t.openedAt,t.closedAt))
 override suspend fun cerrar(t:Turno)=dao.update(TurnoEntity(t.id,t.usuarioId,t.fondoInicial,false,t.openedAt,t.closedAt))
}
