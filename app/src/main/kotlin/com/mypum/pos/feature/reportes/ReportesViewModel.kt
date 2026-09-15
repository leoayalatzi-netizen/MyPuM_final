package com.mypum.pos.feature.reportes
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class ReportesViewModel:ViewModel() { private val _state=MutableStateFlow(ReportesContractState()); val state: StateFlow<ReportesContractState> = _state }
