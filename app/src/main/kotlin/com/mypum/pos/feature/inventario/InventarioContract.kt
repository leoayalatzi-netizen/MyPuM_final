package com.mypum.pos.feature.inventario
import com.mypum.pos.core.*
data class InventarioContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface InventarioContractEvent:UiEvent
sealed interface InventarioContractEffect:UiEffect
