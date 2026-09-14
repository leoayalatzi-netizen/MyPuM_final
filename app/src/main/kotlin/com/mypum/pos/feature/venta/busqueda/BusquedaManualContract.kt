package com.mypum.pos.feature.venta.busqueda
import com.mypum.pos.core.*
data class BusquedaManualContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface BusquedaManualContractEvent:UiEvent
sealed interface BusquedaManualContractEffect:UiEffect
