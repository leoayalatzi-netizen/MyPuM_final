package com.mypum.pos.feature.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.model.enums.UnidadMedida
import com.mypum.pos.domain.repository.ProductoRepository
import com.mypum.pos.domain.repository.subscription.SubscriptionRepository
import com.mypum.pos.domain.model.subscription.PlanEntitlements
import com.mypum.pos.domain.usecase.subscription.VerificarLimiteProductosUseCase
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
    private val productoRepository: ProductoRepository,
    private val verificarLimiteProductos: VerificarLimiteProductosUseCase,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InventarioContractState())
    val state: StateFlow<InventarioContractState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            subscriptionRepository
                .observePlan()
                .collect { plan ->
                    _state.value = _state.value.copy(
                        plan = plan,
                        limiteProductos = PlanEntitlements
                            .forPlan(plan)
                            .maxProducts
                    )
                }
        }

        viewModelScope.launch {
            productoRepository.observeAll()
                .catch { error ->
                    _state.value = _state.value.copy(
                        loading = false,
                        message = error.message
                            ?: "No se pudo cargar el inventario"
                    )
                }
                .collect { products ->
                    _state.value = _state.value.copy(
                        loading = false,
                        productos = products,
                        message = null
                    )
                }
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    fun newProduct() {
        _state.value = _state.value.copy(
            showEditor = true,
            editing = null,
            message = null
        )
    }

    fun edit(producto: Producto) {
        _state.value = _state.value.copy(
            showEditor = true,
            editing = producto,
            message = null
        )
    }

    fun closeEditor() {
        _state.value = _state.value.copy(
            showEditor = false,
            editing = null
        )
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
        val precioValue = precio.replace(",", ".").toBigDecimalOrNull()
        val costoValue = costo.replace(",", ".").toBigDecimalOrNull()
        val stockValue = stock.replace(",", ".").toBigDecimalOrNull()
        val minimoValue = stockMinimo.replace(",", ".").toBigDecimalOrNull()

        if (
            nombre.isBlank() ||
            precioValue == null ||
            costoValue == null ||
            stockValue == null ||
            minimoValue == null
        ) {
            _state.value = _state.value.copy(
                message = "Completa nombre, precios y existencias con valores válidos"
            )
            return
        }

        viewModelScope.launch {
            runCatching {
                val old = _state.value.editing

                /*
                 * El límite de productos solamente aplica al alta.
                 *
                 * Si estamos editando un producto existente, no se
                 * comprueba el límite para no bloquear modificaciones
                 * cuando la cuenta ya tenga el máximo permitido.
                 */
                if (old == null) {
                    when (val resultado = verificarLimiteProductos()) {
                        is VerificarLimiteProductosUseCase.Resultado.Permitido -> {
                            // Continúa normalmente.
                        }

                        is VerificarLimiteProductosUseCase.Resultado.LimiteAlcanzado -> {
                            throw LimiteProductosException(
                                resultado.productosActuales,
                                resultado.limite
                            )
                        }
                    }
                }

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
                _state.value = _state.value.copy(
                    message = when (error) {
                        is LimiteProductosException ->
                            "Has alcanzado el límite de ${error.limite} productos del plan FREE. Actualiza a PRO para agregar más."

                        else ->
                            error.message ?: "No se pudo guardar"
                    }
                )
            }
        }
    }

    fun eliminar(producto: Producto) {
        viewModelScope.launch {
            runCatching {
                productoRepository.eliminar(producto)
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    message = error.message
                        ?: "No se pudo eliminar el producto"
                )
            }
        }
    }
}

private class LimiteProductosException(
    val productosActuales: Int,
    val limite: Int
) : IllegalStateException(
    "Límite de productos alcanzado: $productosActuales/$limite"
)
