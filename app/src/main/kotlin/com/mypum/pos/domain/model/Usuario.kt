package com.mypum.pos.domain.model
import com.mypum.pos.domain.model.enumss.RolUsuario
data class Usuario(val id:Long=0,val nombre:String,val pinHash:String,val rol:RolUsuario,val activo:Boolean=true)
