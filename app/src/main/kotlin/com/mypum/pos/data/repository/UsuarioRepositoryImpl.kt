package com.mypum.pos.data.repository

import com.mypum.pos.data.local.dao.UsuarioDao
import com.mypum.pos.data.local.entity.UsuarioEntity
import com.mypum.pos.domain.model.Usuario
import com.mypum.pos.domain.model.enumss.RolUsuario
import com.mypum.pos.domain.repository.UsuarioRepository
import com.mypum.pos.domain.util.PinHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UsuarioRepositoryImpl(
    private val dao: UsuarioDao
) : UsuarioRepository {

    override fun observeAll(): Flow<List<Usuario>> =
        dao.observeAll().map { usuarios ->
            usuarios.map {
                Usuario(
                    id = it.id,
                    nombre = it.nombre,
                    pinHash = it.pinHash,
                    rol = it.rol,
                    activo = it.activo
                )
            }
        }

    override suspend fun login(
        nombre: String,
        pin: String
    ): Usuario? =
        dao.find(nombre)
            ?.takeIf {
                it.pinHash == PinHasher.hash(pin)
            }
            ?.let {
                Usuario(
                    it.id,
                    it.nombre,
                    it.pinHash,
                    it.rol,
                    it.activo
                )
            }

    override suspend fun crear(
        nombre: String,
        pin: String,
        rol: RolUsuario
    ): Long {
        return dao.insert(
            UsuarioEntity(
                nombre = nombre,
                pinHash = PinHasher.hash(pin),
                rol = rol,
                activo = true
            )
        )
    }

    override suspend fun cambiarActivo(
        id: Long,
        activo: Boolean
    ) {
        val usuario = dao.findById(id) ?: return

        dao.update(
            usuario.copy(
                activo = activo
            )
        )
    }
}
