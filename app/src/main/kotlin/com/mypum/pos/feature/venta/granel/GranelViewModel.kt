package com.mypum.pos.feature.venta.granel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class GranelViewModel:ViewModel() { private val _state=MutableStateFlow(GranelContractState()); val state:StateFlow<GranelContractState> =_state }
