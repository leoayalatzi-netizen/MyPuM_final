package com.mypum.pos.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mypum.pos.domain.model.enum.RolUsuario
@Entity(tableName="usuarios")
data class UsuarioEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val nombre:String,val pinHash:String,val rol:RolUsuario,val activo:Boolean=true)
