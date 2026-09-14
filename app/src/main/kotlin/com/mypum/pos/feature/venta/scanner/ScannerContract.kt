package com.mypum.pos.feature.venta.scanner
import com.mypum.pos.core.*
data class ScannerContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface ScannerContractEvent:UiEvent
sealed interface ScannerContractEffect:UiEffect
