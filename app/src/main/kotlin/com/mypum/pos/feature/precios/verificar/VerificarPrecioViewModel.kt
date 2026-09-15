package com.mypum.pos.feature.precios.verificar
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class VerificarPrecioViewModel:ViewModel() { private val _state=MutableStateFlow(VerificarPrecioContractState()); val state:StateFlow<VerificarPrecioContractState> =_state }
