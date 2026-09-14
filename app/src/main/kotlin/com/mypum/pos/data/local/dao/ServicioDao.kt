package com.mypum.pos.data.local.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.data.local.entity.*
@Dao interface ServicioDao { @Insert suspend fun insert(e:ServicioEntity):Long; @Query("SELECT * FROM servicios ORDER BY createdAt DESC") fun observeAll():Flow<List<ServicioEntity>> }