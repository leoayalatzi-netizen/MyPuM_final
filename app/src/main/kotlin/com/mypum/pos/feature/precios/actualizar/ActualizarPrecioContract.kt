package com.mypum.pos.feature.precios.actualizar
import com.mypum.pos.core.*
data class ActualizarPrecioContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface ActualizarPrecioContractEvent:UiEvent
sealed interface ActualizarPrecioContractEffect:UiEffect
