package com.mypum.pos.data.local.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.data.local.entity.*
@Dao interface VentaDao { @Query("SELECT * FROM ventas ORDER BY createdAt DESC") fun observeAll():Flow<List<VentaEntity>>; @Insert suspend fun insert(e:VentaEntity):Long; @Update suspend fun update(e:VentaEntity) }