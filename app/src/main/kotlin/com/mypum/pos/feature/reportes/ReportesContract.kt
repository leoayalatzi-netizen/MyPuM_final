package com.mypum.pos.feature.reportes
import com.mypum.pos.core.*
data class ReportesContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface ReportesContractEvent:UiEvent
sealed interface ReportesContractEffect:UiEffect
