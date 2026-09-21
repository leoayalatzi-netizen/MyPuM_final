package com.mypum.pos.domain.model
import com.mypum.pos.domain.model.enumss.TipoServicio
import java.math.BigDecimal
import java.time.Instant
data class Servicio(val id:Long=0,val turnoId:Long,val tipo:TipoServicio,val referencia:String,val monto:BigDecimal,val createdAt:Instant=Instant.now())
