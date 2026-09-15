package com.mypum.pos.feature.apertura_turno
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class AperturaViewModel:ViewModel() { private val _state=MutableStateFlow(AperturaContractState()); val state: StateFlow<AperturaContractState> = _state }
