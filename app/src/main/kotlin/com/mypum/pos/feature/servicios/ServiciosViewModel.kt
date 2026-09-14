package com.mypum.pos.feature.servicios
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class ServiciosViewModel:ViewModel() { private val _state=MutableStateFlow(ServiciosContractState()); val state:StateFlow<TEMP>_state }
