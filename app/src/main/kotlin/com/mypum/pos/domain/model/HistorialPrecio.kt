package com.mypum.pos.domain.model
import java.math.BigDecimal
import java.time.Instant
data class HistorialPrecio(val id:Long=0,val productoId:Long,val precioAnterior:BigDecimal,val precioNuevo:BigDecimal,val usuarioId:Long,val createdAt:Instant=Instant.now())
