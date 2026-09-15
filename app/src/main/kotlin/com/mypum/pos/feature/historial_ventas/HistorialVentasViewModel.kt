package com.mypum.pos.feature.historial_ventas
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class HistorialVentasViewModel:ViewModel() { private val _state=MutableStateFlow(HistorialVentasContractState()); val state:StateFlow<HistorialVentasContractState> =_state }
