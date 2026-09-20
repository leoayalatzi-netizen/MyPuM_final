package com.mypum.pos.domain.usecase.auth
import com.mypum.pos.domain.model.Usuario
import com.mypum.pos.domain.model.enum.RolUsuario
class VerificarRolUseCase { operator fun invoke(u:Usuario,rol:RolUsuario)=u.rol==rol }