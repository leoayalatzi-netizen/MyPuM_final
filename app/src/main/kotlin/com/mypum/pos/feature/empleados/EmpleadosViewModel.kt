package com.mypum.pos.feature.empleados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.model.Usuario
import com.mypum.pos.domain.model.enumss.RolUsuario
import com.mypum.pos.domain.model.subscription.Plan
import com.mypum.pos.domain.repository.SubscriptionRepository
import com.mypum.pos.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class EmpleadosState(
    val loading: Boolean = true,
    val plan: Plan = Plan.FREE,
    val empleados: List<Usuario> = emptyList(),
    val autorizado: Boolean = false,
    val mensaje: String? = null
)

@HiltViewModel
class EmpleadosViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmpleadosState())
    val state: StateFlow<EmpleadosState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                subscriptionRepository.observePlan(),
                usuarioRepository.observeAll()
            ) { plan, empleados ->
                EmpleadosState(
                    loading = false,
                    plan = plan,
                    empleados = empleados
                )
            }.collect { nuevo ->
                _state.value = nuevo.copy(
                    autorizado = _state.value.autorizado
                )
            }
        }
    }

    fun autorizar(
        nombre: String,
        pin: String
    ) {
        if (_state.value.plan != Plan.PRO) {
            _state.value = _state.value.copy(
                mensaje = "La administración de empleados requiere MyPuM PRO."
            )
            return
        }

        viewModelScope.launch {
            val usuario = usuarioRepository.login(
                nombre = nombre.trim(),
                pin = pin
            )

            if (usuario?.rol == RolUsuario.ADMIN) {
                _state.value = _state.value.copy(
                    autorizado = true,
                    mensaje = null
                )
            } else {
                _state.value = _state.value.copy(
                    mensaje = "Usuario o PIN de administrador incorrectos."
                )
            }
        }
    }

    fun crearEmpleado(
        nombre: String,
        pin: String,
        rol: RolUsuario
    ) {
        if (!_state.value.autorizado) {
            _state.value = _state.value.copy(
                mensaje = "Se requiere autorización de administrador."
            )
            return
        }

        val nombreLimpio = nombre.trim()
        val pinLimpio = pin.trim()

        if (nombreLimpio.isBlank()) {
            _state.value = _state.value.copy(
                mensaje = "Escribe el nombre del empleado."
            )
            return
        }

        if (pinLimpio.length < 4) {
            _state.value = _state.value.copy(
                mensaje = "El PIN debe tener al menos 4 caracteres."
            )
            return
        }

        if (_state.value.empleados.any {
                it.nombre.equals(nombreLimpio, ignoreCase = true)
            }
        ) {
            _state.value = _state.value.copy(
                mensaje = "Ya existe un usuario con ese nombre."
            )
            return
        }

        viewModelScope.launch {
            runCatching {
                usuarioRepository.crear(
                    nombre = nombreLimpio,
                    pin = pinLimpio,
                    rol = rol
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    mensaje = "Empleado creado correctamente."
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    mensaje = error.message
                        ?: "No se pudo crear el empleado."
                )
            }
        }
    }

    fun cambiarActivo(
        empleado: Usuario
    ) {
        if (!_state.value.autorizado) {
            _state.value = _state.value.copy(
                mensaje = "Se requiere autorización de administrador."
            )
            return
        }

        viewModelScope.launch {
            runCatching {
                usuarioRepository.cambiarActivo(
                    id = empleado.id,
                    activo = !empleado.activo
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    mensaje = if (empleado.activo) {
                        "Empleado desactivado."
                    } else {
                        "Empleado activado."
                    }
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    mensaje = error.message
                        ?: "No se pudo actualizar el empleado."
                )
            }
        }
    }

    fun limpiarMensaje() {
        _state.value = _state.value.copy(
            mensaje = null
        )
    }
}
