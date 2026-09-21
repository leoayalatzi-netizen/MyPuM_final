package com.mypum.pos.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.Instant
import com.mypum.pos.domain.model.enumss.TipoServicio
@Entity(tableName="servicios")
data class ServicioEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val turnoId:Long,val tipo:TipoServicio,val referencia:String,val monto:BigDecimal,val createdAt:Instant)
