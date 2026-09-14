package com.mypum.pos.data.local.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.data.local.entity.*
@Dao interface EgresoDao { @Insert suspend fun insert(e:EgresoEntity):Long; @Query("SELECT * FROM egresos WHERE turnoId=:turnoId ORDER BY createdAt DESC") fun byTurno(turnoId:Long):Flow<List<EgresoEntity>> }