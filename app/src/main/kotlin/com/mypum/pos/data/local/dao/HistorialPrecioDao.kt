package com.mypum.pos.data.local.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.data.local.entity.*
@Dao interface HistorialPrecioDao { @Insert suspend fun insert(e:HistorialPrecioEntity):Long; @Query("SELECT * FROM historial_precios WHERE productoId=:id ORDER BY createdAt DESC") fun byProducto(id:Long):Flow<List<HistorialPrecioEntity>> }
