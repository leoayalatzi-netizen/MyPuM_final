package com.mypum.pos.feature.venta.scanner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class ScannerViewModel:ViewModel() { private val _state=MutableStateFlow(ScannerContractState()); val state:StateFlow<TEMP>_state }
