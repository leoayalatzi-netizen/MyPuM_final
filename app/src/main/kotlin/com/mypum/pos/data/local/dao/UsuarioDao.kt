package com.mypum.pos.data.local.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.data.local.entity.*
@Dao interface UsuarioDao { @Query("SELECT * FROM usuarios WHERE nombre=:nombre AND activo=1 LIMIT 1") suspend fun find(nombre:String):UsuarioEntity?; @Insert suspend fun insert(e:UsuarioEntity):Long }