package com.mypum.pos.domain.util
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
object DateFormatter { private val f=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault()); fun format(v:Instant)=f.format(v) }
