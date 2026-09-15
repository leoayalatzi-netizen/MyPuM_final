package com.mypum.pos.feature.precios.actualizar
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class ActualizarPrecioViewModel:ViewModel() { private val _state=MutableStateFlow(ActualizarPrecioContractState()); val state: StateFlow<ActualizarPrecioContractState> = _state }
