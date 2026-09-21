package com.mypum.pos.core.ext
fun Double.money()=java.math.BigDecimal.valueOf(this)
