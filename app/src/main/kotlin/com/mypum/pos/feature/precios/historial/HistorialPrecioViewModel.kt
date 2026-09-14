package com.mypum.pos.feature.precios.historial
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class HistorialPrecioViewModel:ViewModel() { private val _state=MutableStateFlow(HistorialPrecioContractState()); val state:StateFlow<TEMP>_state }
