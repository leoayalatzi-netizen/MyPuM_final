package com.mypum.pos.feature.venta
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class VentaViewModel:ViewModel() { private val _state=MutableStateFlow(VentaContractState()); val state:StateFlow<TEMP>_state }
