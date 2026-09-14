package com.mypum.pos.domain.repository
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.domain.model.*
interface EgresoRepository { suspend fun registrar(egreso:Egreso):Long }