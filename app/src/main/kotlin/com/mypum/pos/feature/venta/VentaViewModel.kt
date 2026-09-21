package com.mypum.pos.feature.venta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.model.ItemCarrito
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.model.Venta
import com.mypum.pos.domain.model.enumss.MetodoPago
import com.mypum.pos.domain.repository.ProductoRepository
import com.mypum.pos.domain.repository.TurnoRepository
import com.mypum.pos.domain.repository.VentaRepository
import com.mypum.pos.domain.usecase.turno.AbrirTurnoUseCase
import com.mypum.pos.domain.usecase.turno.CerrarTurnoUseCase
import com.mypum.pos.domain.usecase.venta.RegistrarVentaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VentaViewModel @Inject constructor(
    private val productoRepository: ProductoRepository,
    private val ventaRepository: VentaRepository,
    private val turnoRepository: TurnoRepository,
    private val registrarVentaUseCase: RegistrarVentaUseCase,
    private val abrirTurnoUseCase: AbrirTurnoUseCase,
    private val cerrarTurnoUseCase: CerrarTurnoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(VentaContractState())
    val state: StateFlow<VentaContractState> = _state.asStateFlow()

    private var productosJob: Job? = null
    private var turnoJob: Job? = null

    init {
        observarProductos()
        observarTurno()
    }

    private fun observarProductos() {
        productosJob?.cancel()

        productosJob = viewModelScope.launch {
            productoRepository.observeAll().collect { productos ->
                _state.value = _state.value.copy(
                    productos = productos
                )
            }
        }
    }

    private fun observarTurno() {
        turnoJob?.cancel()

        turnoJob = viewModelScope.launch {
            turnoRepository.observeActivo().collect { turno ->
                _state.value = _state.value.copy(
                    turno = turno
                )
            }
        }
    }

    fun search(value: String) {
        _state.value = _state.value.copy(
            query = value
        )
    }

    fun add(productId: Long) {
        val current = _state.value

        if (current.turno == null) {
            showMessage("Primero debes abrir un turno.")
            return
        }

        val product = current.productos.firstOrNull {
            it.id == productId
        } ?: return

        if (product.stock <= BigDecimal.ZERO) {
            showMessage("No hay stock disponible de ${product.nombre}.")
            return
        }

        val existing = current.carrito.firstOrNull {
            it.producto.id == productId
        }

        val newCart =
            if (existing == null) {
                current.carrito + ItemCarrito(
                    producto = product,
                    cantidad = BigDecimal.ONE
                )
            } else {

                val nuevaCantidad =
                    existing.cantidad.add(BigDecimal.ONE)

                if (nuevaCantidad > product.stock) {
                    showMessage("Stock insuficiente de ${product.nombre}.")
                    return
                }

                current.carrito.map {
                    if (it.producto.id == productId) {
                        it.copy(
                            cantidad = nuevaCantidad,
                            subtotal = product.precio.multiply(nuevaCantidad)
                        )
                    } else {
                        it
                    }
                }
            }

        actualizarCarrito(newCart)
    }

    fun decrease(productId: Long) {

        val current = _state.value

        val item = current.carrito.firstOrNull {
            it.producto.id == productId
        } ?: return

        val nuevaCantidad =
            item.cantidad.subtract(BigDecimal.ONE)

        val newCart =
            if (nuevaCantidad <= BigDecimal.ZERO) {
                current.carrito.filter {
                    it.producto.id != productId
                }
            } else {
                current.carrito.map {
                    if (it.producto.id == productId) {
                        it.copy(
                            cantidad = nuevaCantidad,
                            subtotal = item.producto.precio.multiply(nuevaCantidad)
                        )
                    } else {
                        it
                    }
                }
            }

        actualizarCarrito(newCart)
    }

    fun remove(productId: Long) {

        val newCart = _state.value.carrito.filter {
            it.producto.id != productId
        }

        actualizarCarrito(newCart)
    }

    private fun actualizarCarrito(
        carrito: List<ItemCarrito>
    ) {

        val total =
            carrito.fold(BigDecimal.ZERO) { acumulado, item ->
                acumulado.add(item.subtotal)
            }

        _state.value = _state.value.copy(
            carrito = carrito,
            total = total
        )
    }

    fun addByCode(code: String) {

        val normalized = code.trim()

        if (normalized.isEmpty()) return

        viewModelScope.launch {

            val producto =
                productoRepository.buscarPorCodigo(normalized)

            if (producto == null) {
                showMessage("No se encontró el código $normalized.")
                return@launch
            }

            add(producto.id)
        }
    }

    fun requestCheckout() {

        val current = _state.value

        if (current.turno == null) {
            showMessage("Primero debes abrir un turno.")
            return
        }

        if (current.carrito.isEmpty()) {
            showMessage("Agrega al menos un producto.")
            return
        }

        if (current.total <= BigDecimal.ZERO) {
            showMessage("El total de la venta no es válido.")
            return
        }

        _state.value = current.copy(
            showCheckout = true
        )
    }

    fun closeCheckout() {
        _state.value = _state.value.copy(
            showCheckout = false
        )
    }

    fun confirmPayment(
        metodoPago: MetodoPago,
        recibidoTexto: String
    ) {

        val current = _state.value

        val turno = current.turno
        if (turno == null) {
            showMessage("No hay un turno abierto.")
            return
        }

        if (current.carrito.isEmpty()) {
            showMessage("El carrito está vacío.")
            return
        }

        val recibido =
            recibidoTexto
                .trim()
                .replace(",", ".")
                .toBigDecimalOrNull()

        if (metodoPago == MetodoPago.EFECTIVO) {

            if (recibido == null) {
                showMessage("Ingresa el efectivo recibido.")
                return
            }

            if (recibido < current.total) {
                showMessage(
                    "El efectivo recibido es menor al total."
                )
                return
            }
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                loading = true,
                message = null
            )

            try {

                val venta = Venta(
                    id = 0L,
                    turnoId = turno.id,
                    total = current.total,
                    metodoPago = metodoPago,
                    items = current.carrito,
                    cancelada = false,
                    createdAt = Instant.now()
                )

                registrarVentaUseCase(venta)

                val cambio =
                    if (metodoPago == MetodoPago.EFECTIVO) {
                        recibido!!.subtract(current.total)
                    } else {
                        BigDecimal.ZERO
                    }

                _state.value = _state.value.copy(
                    loading = false,
                    carrito = emptyList(),
                    total = BigDecimal.ZERO,
                    showCheckout = false,
                    message =
                        if (metodoPago == MetodoPago.EFECTIVO) {
                            "Venta registrada.\nCambio: $ ${
                                cambio.setScale(
                                    2,
                                    java.math.RoundingMode.HALF_UP
                                )
                            }"
                        } else {
                            "Venta registrada correctamente."
                        }
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    loading = false,
                    message =
                        e.message
                            ?: "No se pudo registrar la venta."
                )
            }
        }
    }

    fun openTurno(fondoTexto: String) {

        val fondo =
            fondoTexto
                .trim()
                .replace(",", ".")
                .toBigDecimalOrNull()

        if (fondo == null || fondo < BigDecimal.ZERO) {
            showMessage("El fondo inicial no es válido.")
            return
        }

        if (_state.value.turno != null) {
            showMessage("Ya existe un turno abierto.")
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                loading = true
            )

            try {

                /*
                 * MyPuM funciona sin pantalla de usuario/PIN.
                 * El usuario local inicial creado por DatabaseSeeder
                 * utiliza normalmente el ID 1.
                 */
                abrirTurnoUseCase(
                    Turno(
                        id = 0L,
                        usuarioId = 1L,
                        fondoInicial = fondo,
                        abierto = true,
                        openedAt = Instant.now(),
                        closedAt = null
                    )
                )

                _state.value = _state.value.copy(
                    loading = false,
                    message = "Turno abierto correctamente."
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    loading = false,
                    message =
                        e.message
                            ?: "No se pudo abrir el turno."
                )
            }
        }
    }

    fun closeTurno(efectivoContadoTexto: String) {

        val turno = _state.value.turno

        if (turno == null) {
            showMessage("No hay un turno abierto.")
            return
        }

        val efectivo =
            efectivoContadoTexto
                .trim()
                .replace(",", ".")
                .toBigDecimalOrNull()

        if (efectivo == null || efectivo < BigDecimal.ZERO) {
            showMessage("El efectivo contado no es válido.")
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                loading = true
            )

            try {

                val cierre =
                    cerrarTurnoUseCase(
                        turno,
                        efectivo
                    )

                val diferencia =
                    cierre.diferencia.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                    )

                _state.value = _state.value.copy(
                    loading = false,
                    message =
                        "Turno cerrado.\n" +
                        "Efectivo esperado: $ ${
                            cierre.efectivoEsperado.setScale(
                                2,
                                java.math.RoundingMode.HALF_UP
                            )
                        }\n" +
                        "Efectivo con