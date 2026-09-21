package com.mypum.pos.domain.repository
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.domain.model.*
interface ServicioRepository { suspend fun registrar(servicio:Servicio):Long }
