package com.mypum.pos.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
@Entity(tableName="detalle_venta")
data class DetalleVentaEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val ventaId:Long,val productoId:Long,val cantidad:BigDecimal,val pesoGramos:Double?,val precioUnitario:BigDecimal,val subtotal:BigDecimal)
