package com.mypum.pos.feature.inventario
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class InventarioViewModel:ViewModel() { private val _state=MutableStateFlow(InventarioContractState()); val state:StateFlow<TEMP>_state }
