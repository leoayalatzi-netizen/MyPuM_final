package com.mypum.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mypum.pos.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("""
        SELECT * FROM usuarios
        ORDER BY activo DESC, nombre ASC
    """)
    fun observeAll(): Flow<List<UsuarioEntity>>

    @Query("""
        SELECT * FROM usuarios
        WHERE nombre = :nombre
        AND activo = 1
        LIMIT 1
    """)
    suspend fun find(nombre: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): UsuarioEntity?

    @Insert
    suspend fun insert(e: UsuarioEntity): Long

    @Update
    suspend fun update(e: UsuarioEntity)
}
