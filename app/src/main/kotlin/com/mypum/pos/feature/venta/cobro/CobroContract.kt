package com.mypum.pos.feature.venta.cobro
import com.mypum.pos.core.*
data class CobroContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface CobroContractEvent:UiEvent
sealed interface CobroContractEffect:UiEffect
