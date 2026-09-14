package com.mypum.pos.data.repository
import com.mypum.pos.domain.repository.UsuarioRepository
import com.mypum.pos.data.local.dao.UsuarioDao
import com.mypum.pos.domain.model.Usuario
import com.mypum.pos.domain.util.PinHasher
class UsuarioRepositoryImpl(private val dao:UsuarioDao):UsuarioRepository{
 override suspend fun login(nombre:String,pin:String)=dao.find(nombre)?.takeIf{it.pinHash==PinHasher.hash(pin)}?.let{Usuario(it.id,it.nombre,it.pinHash,it.rol,it.activo)}
}
