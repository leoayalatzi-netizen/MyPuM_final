package com.mypum.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mypum.pos.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos WHERE activo = 1 ORDER BY nombre")
    fun observeAll(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): ProductoEntity?

    @Query("SELECT * FROM productos WHERE codigo = :codigo LIMIT 1")
    suspend fun byCodigo(codigo: String): ProductoEntity?

    @Insert
    suspend fun insert(e: ProductoEntity): Long

    @Update
    suspend fun update(e: ProductoEntity)
}
