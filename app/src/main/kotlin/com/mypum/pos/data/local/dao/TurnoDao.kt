package com.mypum.pos.data.local.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.data.local.entity.*
@Dao interface TurnoDao { @Query("SELECT * FROM turnos WHERE abierto=1 LIMIT 1") fun observeActivo():Flow<TurnoEntity?>; @Insert suspend fun insert(e:TurnoEntity):Long; @Update suspend fun update(e:TurnoEntity) }