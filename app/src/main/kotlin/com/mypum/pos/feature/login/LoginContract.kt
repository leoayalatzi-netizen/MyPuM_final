package com.mypum.pos.feature.login
import com.mypum.pos.core.*
data class LoginContractState(val loading:Boolean=false,val message:String?=null):UiState
sealed interface LoginContractEvent:UiEvent
sealed interface LoginContractEffect:UiEffect
