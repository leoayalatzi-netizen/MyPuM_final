package com.mypum.pos.domain.repository
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.domain.model.*
interface ProductoRepository { fun observeAll():Flow<List<Producto>>; suspend fun buscarPorCodigo(codigo:String):Producto?; suspend fun guardar(producto:Producto):Long }