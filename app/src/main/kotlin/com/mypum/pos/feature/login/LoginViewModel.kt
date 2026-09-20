package com.mypum.pos.feature.login
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class LoginViewModel:ViewModel() { private val _state=MutableStateFlow(LoginContractState()); val state:StateFlow<LoginContractState> =_state }
