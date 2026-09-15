package com.mypum.pos.feature.inventario

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductoFormViewModel : ViewModel() {

    private val _state = MutableStateFlow(InventarioContractState())

    val state: StateFlow<InventarioContractState> = _state
}
