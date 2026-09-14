package com.mypum.pos.feature.venta
import com.mypum.pos.core.*
data class VentaContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface VentaContractEvent:UiEvent
sealed interface VentaContractEffect:UiEffect
