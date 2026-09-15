package com.mypum.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mypum.pos.data.local.entity.EgresoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EgresoDao {

    @Insert
    suspend fun insert(e: EgresoEntity): Long

    @Query("SELECT * FROM egresos ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<EgresoEntity>>

    @Query("SELECT * FROM egresos WHERE turnoId = :turnoId ORDER BY createdAt DESC")
    fun byTurno(turnoId: Long): Flow<List<EgresoEntity>>
}
