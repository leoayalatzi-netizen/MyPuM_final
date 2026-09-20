package com.mypum.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mypum.pos.data.local.entity.DetalleVentaEntity

@Dao
interface DetalleVentaDao {

    @Insert
    suspend fun insertAll(detalles: List<DetalleVentaEntity>)

    @Query("SELECT * FROM detalle_venta WHERE ventaId = :ventaId")
    suspend fun byVenta(ventaId: Long): List<DetalleVentaEntity>
}
