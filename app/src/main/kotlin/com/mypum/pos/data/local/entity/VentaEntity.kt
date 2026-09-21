package com.mypum.pos.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.Instant
import com.mypum.pos.domain.model.enums.MetodoPago
@Entity(tableName="ventas")
data class VentaEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val turnoId:Long,val total:BigDecimal,val metodoPago:MetodoPago,val cancelada:Boolean=false,val createdAt:Instant)
