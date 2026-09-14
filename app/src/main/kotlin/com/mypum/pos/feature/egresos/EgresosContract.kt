package com.mypum.pos.feature.egresos
import com.mypum.pos.core.*
data class EgresosContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface EgresosContractEvent:UiEvent
sealed interface EgresosContractEffect:UiEffect
