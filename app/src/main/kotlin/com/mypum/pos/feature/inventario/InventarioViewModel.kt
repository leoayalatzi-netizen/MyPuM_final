package com.mypum.pos.feature.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.model.enums.UnidadMedida
import com.mypum.pos.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.Instant
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
        viewModelScope.launch {
            productoRepository.observeAll()
                .catch { error ->
                    _state.value = _state.value.copy(
                        loading = false,
                        message = error.message ?: "No se pudo cargar el inventario"
                    )
                }
                .collect { products ->
                    _state.value = _state.value.copy(loading = false, productos = products, message = null)
                }
        }
    }

    fun clearMessage() { _state.value = _state.value.copy(message = null) }

    fun newProduct() {
        _state.value = _state.value.copy(showEditor = true, editing = null, message = null)
    }

    fun edit(producto: Producto) {
        _state.value = _state.value.copy(showEditor = true, editing = producto, message = null)
    }

    fun closeEditor() {
        _state.value = _state.value.copy(showEditor = false, editing = null)
    }

    fun save(
        id: Long,
        nombre: String,
        codigo: String,
        precio: String,
        costo: String,
        stock: String,
        stockMinimo: String,
        categoria: String,
        esGranel: Boolean,
        unidad: UnidadMedida
    ) {
        val precioValue = precio.toBigDecimalOrNull()
        val costoValue = costo.toBigDecimalOrNull()
        val stockValue = stock.toBigDecimalOrNull()
        val minimoValue = stockMinimo.toBigDecimalOrNull()
        if (nombre.isBlank() || precioValue == null || costoValue == null || stockValue == null || minimoValue == null) {
            _state.value = _state.value.copy(message = "Completa nombre, precios y existencias con valores válidos")
            return
        }
        viewModelScope.launch {
            runCatching {
                val old = _state.value.editing
                productoRepository.guardar(
                    Producto(
                        id = id,
                        nombre = nombre.trim(),
                        codigo = codigo.trim().ifBlank { null },
                        precio = precioValue,
                        costo = costoValue,
                        stock = stockValue,
                        stockMinimo = minimoValue,
                        categoria = categoria.trim().ifBlank { null },
                        esGranel = esGranel,
                        unidadMedida = unidad,
                        activo = old?.activo ?: true,
                        createdAt = old?.createdAt ?: Instant.now(),
                        updatedAt = Instant.now()
                    )
                )
            }.onSuccess {
                closeEditor()
            }.onFailure { error ->
                _state.value = _state.value.copy(message = error.message ?: "No se pudo guardar")
            }
        }
    }
}
