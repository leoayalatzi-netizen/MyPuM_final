package com.mypum.pos.domain.repository

import com.mypum.pos.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {

    fun observeAll(): Flow<List<Usuario>>

    suspend fun login(
        nombre: String,
        pin: String
    ): Usuario?

    suspend fun crear(
        nombre: String,
        pin: String,
        rol: com.mypum.pos.domain.model.enumss.RolUsuario
    ): Long

    suspend fun cambiarActivo(
        id: Long,
        activo: Boolean
    )
}
