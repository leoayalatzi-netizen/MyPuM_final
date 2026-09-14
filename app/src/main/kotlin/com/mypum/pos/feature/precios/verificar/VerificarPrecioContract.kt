package com.mypum.pos.feature.precios.verificar
import com.mypum.pos.core.*
data class VerificarPrecioContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface VerificarPrecioContractEvent:UiEvent
sealed interface VerificarPrecioContractEffect:UiEffect
