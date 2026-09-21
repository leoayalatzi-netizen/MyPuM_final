package com.mypum.pos.domain.repository
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.domain.model.*
interface UsuarioRepository { suspend fun login(nombre:String,pin:String):Usuario? }
