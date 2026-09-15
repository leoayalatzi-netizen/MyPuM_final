package com.mypum.pos.feature.venta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.data.datastore.SessionDataStore
import com.mypum.pos.domain.model.ItemCarrito
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.model.Venta
import com.mypum.pos.domain.model.enums.MetodoPago
import com.mypum.pos.domain.repository.EgresoRepository
import com.mypum.pos.domain.repository.ProductoRepository
import com.mypum.pos.domain.repository.TurnoRepository
import com.mypum.pos.domain.repository.VentaRepository
import com.mypum.pos.domain.usecase.turno.CalcularCierreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@HiltViewModel
class VentaViewModel @Inject constructor(
    private val productoRepository: ProductoRepository,
    private val ventaRepository: VentaRepository,
    private val turnoRepository: TurnoRepository,
    private val egresoRepository: EgresoRepository,
    private val calcularCierreUseCase: CalcularCierreUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(VentaContractState())
    val state: StateFlow<VentaContractState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                productoRepository.observeAll(),
                turnoRepository.observeActivo()
            ) { products, turno -> products to turno }
                .catch { error ->
                    _state.value = _state.value.copy(
                        loading = false,
                        message = error.message ?: "No se pudo cargar la venta"
                    )
                }
                .collect { (products, turno) ->
                    _state.value = _state.value.copy(
                        loading = false,
                        productos = products,
                        turno = turno
                    )
                }
        }
    }

    fun search(query: String) {
        _state.value = _state.value.copy(query = query)
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    fun add(productId: Long) {
        val product = _state.value.productos.firstOrNull { it.id == productId }
            ?: return

        val existing = _state.value.carrito.firstOrNull {
            it.producto.id == productId
        }

        val quantity =
            existing?.cantidad?.add(BigDecimal.ONE) ?: BigDecimal.ONE

        if (quantity > product.stock) {
            _state.value = _state.value.copy(
                message = "Stock insuficiente para ${product.nombre}"
            )
            return
        }

        val newItem = ItemCarrito(product, quantity)

        _state.value = _state.value.copy(
            carrito = _state.value.carrito
                .filterNot { it.producto.id == productId } + newItem,
            message = null
        )
    }

    fun decrease(productId: Long) {
        val current = _state.value.carrito.firstOrNull {
            it.producto.id == productId
        } ?: return

        val newQuantity = current.cantidad.subtract(BigDecimal.ONE)

        _state.value = _state.value.copy(
            carrito =
                if (newQuantity <= BigDecimal.ZERO) {
                    _state.value.carrito
                        .filterNot { it.producto.id == productId }
                } else {
                    _state.value.carrito.map {
                        if (it.producto.id == productId) {
                            current.copy(
                                cantidad = newQuantity,
                                subtotal = current.producto.precio
                                    .multiply(newQuantity)
                            )
                        } else {
                            it
                        }
                    }
                }
        )
    }

    fun remove(productId: Long) {
        _state.value = _state.value.copy(
            carrito = _state.value.carrito
                .filterNot { it.producto.id == productId }
        )
    }

    fun requestCheckout() {
        when {
            _state.value.carrito.isEmpty() ->
                _state.value = _state.value.copy(
                    message = "Agrega al menos un producto"
                )

            _state.value.turno == null ->
                _state.value = _state.value.copy(
                    message = "Primero abre un turno"
                )

            else ->
                _state.value = _state.value.copy(
                    showCheckout = true,
                    message = null
                )
        }
    }

    fun closeCheckout() {
        _state.value = _state.value.copy(
            showCheckout = false
        )
    }

    fun openTurno(fondo: String) {
        val amount = fondo.toBigDecimalOrNull()

        if (amount == null || amount < BigDecimal.ZERO) {
            _state.value = _state.value.copy(
                message = "Fondo inicial inválido"
            )
            return
        }

        viewModelScope.launch {
            val userId =
                sessionDataStore.activeUserId.firstOrNull() ?: 1L

            runCatching {
                turnoRepository.abrir(
                    Turno(
                        usuarioId = userId,
                        fondoInicial = amount
                    )
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    message = error.message
                        ?: "No se pudo abrir el turno"
                )
            }
        }
    }

    fun confirmPayment(
        metodoPago: MetodoPago,
        recibido: String
    ) {
        val current = _state.value

        val turno = current.turno ?: run {
            _state.value = current.copy(
                showCheckout = false,
                message = "Primero abre un turno"
            )
            return
        }

        val received =
            recibido.toBigDecimalOrNull() ?: BigDecimal.ZERO

        if (
            metodoPago == MetodoPago.EFECTIVO &&
            received < current.total
        ) {
            _state.value = current.copy(
                message = "El efectivo recibido es menor al total"
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                loading = true,
                message = null
            )

            runCatching {
                ventaRepository.registrar(
                    Venta(
                        turnoId = turno.id,
                        total = current.total,
                        metodoPago = metodoPago,
                        items = current.carrito
                    )
                )
            }.onSuccess { id ->
                _state.value = _state.value.copy(
                    loading = false,
                    carrito = emptyList(),
                    showCheckout = false,
                    lastSaleId = id,
                    message = "Venta #$id registrada correctamente"
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    loading = false,
                    message = error.message
                        ?: "No se pudo registrar la venta"
                )
            }
        }
    }

    fun addByCode(code: String) {
        viewModelScope.launch {
            val product =
                productoRepository.buscarPorCodigo(code.trim())

            if (product == null) {
                _state.value = _state.value.copy(
                    message =
                        "No encontré un producto con código $code"
                )
                return@launch
            }

            val current =
                _state.value.carrito.firstOrNull {
                    it.producto.id == product.id
                }

            val quantity =
                current?.cantidad?.add(BigDecimal.ONE)
                    ?: BigDecimal.ONE

            if (quantity > product.stock) {
                _state.value = _state.value.copy(
                    message =
                        "Stock insuficiente: ${product.nombre}"
                )
                return@launch
            }

            val item = ItemCarrito(product, quantity)

            _state.value = _state.value.copy(
                carrito = _state.value.carrito
                    .filterNot { it.producto.id == product.id } + item,
                message = "${product.nombre} agregado"
            )
        }
    }

    fun closeTurno(efectivoContado: String) {
        val current = _state.value
        val turno = current.turno

        if (turno == null) {
            _state.value = current.copy(
                message = "No hay un turno abierto"
            )
            return
        }

        val contado =
            efectivoContado.toBigDecimalOrNull()

        if (contado == null || contado < BigDecimal.ZERO) {
            _state.value = current.copy(
                message = "El efectivo contado no es válido"
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                loading = true,
                message = null
            )

            runCatching {
                val ventas =
                    ventaRepository.observeAll().first()

                val egresos =
                    egresoRepository.byTurno(turno.id).first()

                val cierre =
                    calcularCierreUseCase(
                        turno = turno,
                        ventas = ventas,
                        egresos = egresos
                    )

                val diferencia =
                    contado.subtract(cierre.efectivoEsperado)

                turnoRepository.cerrar(
                    turno.copy(
                        abierto = false,
                        closedAt = Instant.now(),
                        efectivoContado = contado,
                        diferencia = diferencia
                    )
                )

                Triple(cierre, contado, diferencia)
            }.onSuccess { (cierre, contado, diferencia) ->

                val signo =
                    if (diferencia >= BigDecimal.ZERO) "+" else ""

                _state.value = _state.value.copy(
                    loading = false,
                    message =
                        "Turno cerrado.\n" +
                        "Efectivo esperado: ${money(cierre.efectivoEsperado)}\n" +
                        "Efectivo contado: ${money(contado)}\n" +
                        "Diferencia: $signo${money(diferencia)}"
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    loading = false,
                    message =
                        error.message
                            ?: "No se pudo cerrar el turno"
                )
            }
        }
    }

    private fun money(value: BigDecimal): String =
        "$" + value.setScale(2).toPlainString()
}
