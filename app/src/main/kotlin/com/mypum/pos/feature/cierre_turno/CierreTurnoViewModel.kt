package com.mypum.pos.feature.cierre_turno
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class CierreTurnoViewModel:ViewModel() { private val _state=MutableStateFlow(CierreTurnoContractState()); val state:StateFlow<CierreTurnoContractState> =_state }
