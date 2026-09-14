package com.mypum.pos.domain.usecase.auth
import com.mypum.pos.domain.repository.UsuarioRepository
class LoginUseCase(private val repo:UsuarioRepository){ suspend operator fun invoke(nombre:String,pin:String)=repo.login(nombre,pin) }
