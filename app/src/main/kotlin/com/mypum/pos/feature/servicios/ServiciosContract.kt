package com.mypum.pos.feature.servicios
import com.mypum.pos.core.*
data class ServiciosContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface ServiciosContractEvent:UiEvent
sealed interface ServiciosContractEffect:UiEffect
