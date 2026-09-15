package com.mypum.pos.feature.venta.cobro

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CobroViewModel : ViewModel() {

    private val _state = MutableStateFlow(CobroContractState())

    val state: StateFlow<CobroContractState> = _state
}
