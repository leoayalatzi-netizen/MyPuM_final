package com.mypum.pos.feature.egresos
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class EgresosViewModel:ViewModel() { private val _state=MutableStateFlow(EgresosContractState()); val state:StateFlow<TEMP>_state }
