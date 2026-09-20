package com.mypum.pos.domain.model
import java.math.BigDecimal
import java.time.Instant
data class Turno(val id:Long=0,val usuarioId:Long,val fondoInicial:BigDecimal,val abierto:Boolean=true,val openedAt:Instant=Instant.now(),val closedAt:Instant?=null)
data class CierreTurno(val turno:Turno,val efectivoEsperado:BigDecimal,val efectivoContado:BigDecimal,val diferencia:BigDecimal)
