package com.mypum.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mypum.pos.data.local.entity.VentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {

    @Query("SELECT * FROM ventas ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<VentaEntity>>

    @Query("SELECT * FROM ventas WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): VentaEntity?

    @Insert
    suspend fun insert(e: VentaEntity): Long

    @Update
    suspend fun update(e: VentaEntity)
}
