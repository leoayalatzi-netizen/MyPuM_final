package com.mypum.pos.data.remote.api
import retrofit2.http.Body
import retrofit2.http.POST
import com.mypum.pos.data.remote.api.dto.PagoServicioDto
interface PagoServiciosApi { @POST("pagos") suspend fun pagar(@Body request:PagoServicioDto):PagoServicioDto }
