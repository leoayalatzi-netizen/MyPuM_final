package com.mypum.pos.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.data.datastore.SessionDataStore
import com.mypum.pos.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {
    private val _state = MutableStateFlow(LoginContractState())
    val state: StateFlow<LoginContractState> = _state.asStateFlow()

    fun login(nombre: String, pin: String) {
        if (nombre.isBlank() || pin.length < 4) {
            _state.value = _state.value.copy(message = "Escribe usuario y PIN de 4 dígitos")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, message = null)
            runCatching { usuarioRepository.login(nombre.trim(), pin) }
                .onSuccess { usuario ->
                    if (usuario == null) {
                        _state.value = LoginContractState(message = "Usuario o PIN incorrectos")
                    } else {
                        sessionDataStore.setUser(usuario.id)
                        _state.value = LoginContractState(
                            userId = usuario.id,
                            userName = usuario.nombre
                        )
                    }
                }
                .onFailure { error ->
                    _state.value = LoginContractState(
                        message = error.message ?: "No fue posible iniciar sesión"
                    )
                }
        }
    }
}
