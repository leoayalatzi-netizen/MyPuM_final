package com.mypum.pos.domain.repository
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.domain.model.*
interface VentaRepository { fun observeAll():Flow<List<Venta>>; suspend fun registrar(venta:Venta):Long }