package com.mypum.pos.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.Instant
@Entity(tableName="egresos")
data class EgresoEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val turnoId:Long,val concepto:String,val monto:BigDecimal,val createdAt:Instant)
