package com.mypum.pos.feature.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InventarioContractState())
    val state: StateFlow<InventarioContractState> = _state.asStateFlow()

    init {
        observarProductos()
    }

    private fun observarProductos() {
        viewModelScope.launch {
            productoRepository
                .observeAll()
                .catch { error ->
                    _state.value = _state.value.copy(
                        loading = false,
                        message = error.message ?: "Error al cargar inventario"
                    )
                }
                .collect { productos ->
                    _state.value = InventarioContractState(
                        loading = false,
                        productos = productos
                    )
                }
        }
    }

    fun onEvent(event: InventarioContractEvent) {
        when (event) {
            InventarioContractEvent.Recargar -> {
                _state.value = _state.value.copy(
                    loading = true,
                    message = null
                )
            }
        }
    }
}
