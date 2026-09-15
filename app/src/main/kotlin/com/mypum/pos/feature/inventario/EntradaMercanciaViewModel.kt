package com.mypum.pos.feature.inventario
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class EntradaMercanciaViewModel:ViewModel() { private val _state=MutableStateFlow(EntradaMercanciaContractState()); val state: StateFlow<EntradaMercanciaContractState> = _state }
