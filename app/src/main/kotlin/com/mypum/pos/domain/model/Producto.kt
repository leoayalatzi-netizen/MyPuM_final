package com.mypum.pos.domain.model
import com.mypum.pos.domain.model.enum.UnidadMedida
import java.math.BigDecimal
import java.time.Instant
data class Producto(val id:Long=0,val nombre:String,val codigo:String?=null,val precio:BigDecimal=BigDecimal.ZERO,val costo:BigDecimal=BigDecimal.ZERO,val stock:BigDecimal=BigDecimal.ZERO,val stockMinimo:BigDecimal=BigDecimal("5"),val categoria:String?=null,val esGranel:Boolean=false,val unidadMedida:UnidadMedida=UnidadMedida.PIEZA,val activo:Boolean=true,val createdAt:Instant=Instant.now(),val updatedAt:Instant=Instant.now())
