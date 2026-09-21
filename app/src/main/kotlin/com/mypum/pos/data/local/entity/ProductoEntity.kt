package com.mypum.pos.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.Instant
import com.mypum.pos.domain.model.enums.UnidadMedida
@Entity(tableName="productos")
data class ProductoEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val nombre:String,val codigo:String?,val precio:BigDecimal,val costo:BigDecimal,val stock:BigDecimal,val stockMinimo:BigDecimal,val categoria:String?,val esGranel:Boolean,val unidadMedida:UnidadMedida,val activo:Boolean,val createdAt:Instant,val updatedAt:Instant)
