package com.mypum.pos.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.Instant
@Entity(tableName="historial_precios")
data class HistorialPrecioEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val productoId:Long,val precioAnterior:BigDecimal,val precioNuevo:BigDecimal,val usuarioId:Long,val createdAt:Instant)
