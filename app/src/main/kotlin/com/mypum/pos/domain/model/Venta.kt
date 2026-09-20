package com.mypum.pos.domain.model
import com.mypum.pos.domain.model.enum.MetodoPago
import java.math.BigDecimal
import java.time.Instant
data class Venta(val id:Long=0,val turnoId:Long,val total:BigDecimal,val metodoPago:MetodoPago,val items:List<ItemCarrito> =emptyList(),val cancelada:Boolean=false,val createdAt:Instant=Instant.now())
