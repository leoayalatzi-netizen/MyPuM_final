package com.mypum.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mypum.pos.data.local.entity.TurnoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TurnoDao {

    @Query("SELECT * FROM turnos WHERE abierto = 1 LIMIT 1")
    fun observeActivo(): Flow<TurnoEntity?>

    @Query("SELECT * FROM turnos ORDER BY openedAt DESC")
    fun observeAll(): Flow<List<TurnoEntity>>

    @Insert
    suspend fun insert(e: TurnoEntity): Long

    @Update
    suspend fun update(e: TurnoEntity)
}
