package com.mypum.pos.feature.venta.granel
import com.mypum.pos.core.*
data class GranelContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface GranelContractEvent:UiEvent
sealed interface GranelContractEffect:UiEffect
