package com.mypum.pos.domain.model
import java.math.BigDecimal
import java.time.Instant
data class Egreso(val id:Long=0,val turnoId:Long,val concepto:String,val monto:BigDecimal,val createdAt:Instant=Instant.now())
