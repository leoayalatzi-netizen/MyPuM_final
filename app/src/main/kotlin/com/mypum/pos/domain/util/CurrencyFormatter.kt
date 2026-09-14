package com.mypum.pos.domain.util
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
object CurrencyFormatter { fun format(v:BigDecimal)=NumberFormat.getCurrencyInstance(Locale("es","MX")).format(v) }
