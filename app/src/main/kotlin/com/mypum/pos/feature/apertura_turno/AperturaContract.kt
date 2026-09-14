package com.mypum.pos.feature.apertura_turno
import com.mypum.pos.core.*
data class AperturaContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface AperturaContractEvent:UiEvent
sealed interface AperturaContractEffect:UiEffect
