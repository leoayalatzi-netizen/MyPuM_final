package com.mypum.pos.feature.cierre_turno
import com.mypum.pos.core.*
data class CierreTurnoContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface CierreTurnoContractEvent:UiEvent
sealed interface CierreTurnoContractEffect:UiEffect
