package com.mypum.pos.feature.venta.busqueda
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class BusquedaManualViewModel:ViewModel() { private val _state=MutableStateFlow(BusquedaManualContractState()); val state:StateFlow<BusquedaManualContractState>=_state }
