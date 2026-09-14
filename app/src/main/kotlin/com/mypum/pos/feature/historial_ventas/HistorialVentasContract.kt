package com.mypum.pos.feature.historial_ventas
import com.mypum.pos.core.*
data class HistorialVentasContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface HistorialVentasContractEvent:UiEvent
sealed interface HistorialVentasContractEffect:UiEffect
