package com.mypum.pos.data.remote.api
import retrofit2.http.Body
import retrofit2.http.POST
import com.mypum.pos.data.remote.api.dto.*
interface RecargasApi { @POST("recargas") suspend fun recargar(@Body request:RecargaRequestDto):RecargaResponseDto }
