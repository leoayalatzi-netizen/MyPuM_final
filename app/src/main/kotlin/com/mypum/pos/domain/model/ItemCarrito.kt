package com.mypum.pos.domain.model
import java.math.BigDecimal
data class ItemCarrito(val producto:Producto,val cantidad:BigDecimal=BigDecimal.ONE,val pesoGramos:Double?=null,val subtotal:BigDecimal=producto.precio.multiply(cantidad))
