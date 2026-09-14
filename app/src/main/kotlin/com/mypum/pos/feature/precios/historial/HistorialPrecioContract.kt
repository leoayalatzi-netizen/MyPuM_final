package com.mypum.pos.feature.precios.historial
import com.mypum.pos.core.*
data class HistorialPrecioContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface HistorialPrecioContractEvent:UiEvent
sealed interface HistorialPrecioContractEffect:UiEffect
