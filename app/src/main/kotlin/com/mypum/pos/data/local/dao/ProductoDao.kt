package com.mypum.pos.data.local.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.data.local.entity.*
@Dao interface ProductoDao { @Query("SELECT * FROM productos ORDER BY nombre") fun observeAll():Flow<List<ProductoEntity>>; @Query("SELECT * FROM productos WHERE codigo=:codigo LIMIT 1") suspend fun byCodigo(codigo:String):ProductoEntity?; @Insert suspend fun insert(e:ProductoEntity):Long; @Update suspend fun update(e:ProductoEntity) }