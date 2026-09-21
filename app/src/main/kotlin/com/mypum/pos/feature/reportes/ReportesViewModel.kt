package com.mypum.pos.feature.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.model.enum.MetodoPago
import com.mypum.pos.domain.repository.EgresoRepository
import com.mypum.pos.domain.repository.ProductoRepository
import com.mypum.pos.domain.repository.ReporteRepository
import com.mypum.pos.domain.repository.TurnoRepository
import com.mypum.pos.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ReportesViewModel @Inject constructor(
    private val ventaRepository: VentaRepository,
    private val productoRepository: ProductoRepository,
    private val reporteRepository: ReporteRepository,
    private val turnoRepository: TurnoRepository,
    private val egresoRepository: EgresoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReportesContractState())
    val state: StateFlow<ReportesContractState> = _state.asStateFlow()

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            combine(
                ventaRepository.observeAll(),
                productoRepository.observeAll(),
                turnoRepository.observeAll()
            ) { ventas, productos, turnos ->
                Triple(ventas, productos, turnos)
            }
                .catch { error ->
                    _state.value = _state.value.copy(
                        loading = false,
                        message = error.message
                            ?: "No se pudieron cargar los reportes"
                    )
                }
                .collect { data ->

                    val ventas = data.first
                    val productos = data.second
                    val turnos = data.third

                    val egresosPorTurno = turnos.associate { turno ->
                        turno.id to runCatching {
                            egresoRepository
                                .byTurno(turno.id)
                                .first()
                        }.getOrDefault(emptyList())
                    }

                    val grupos = turnos.map { turno ->
                        ReporteTurno(
                            turno = turno,
                            ventas = ventas.filter {
                                it.turnoId == turno.id
                            },
                            egresos = egresosPorTurno[turno.id]
                                ?: emptyList()
                        )
                    }

                    val dias = grupos
                        .groupBy { it.fecha }
                        .map { (fecha, gruposDelDia) ->
                            ReporteDia(
                                fecha = fecha,
                                turnos = gruposDelDia.sortedByDescending {
                                    it.turno.openedAt
                                }
                            )
                        }
                        .sortedByDescending { it.fecha }

                    val ventasValidas = ventas.filter {
                        !it.cancelada
                    }

                    val totalVentas =
                        ventasValidas.fold(BigDecimal.ZERO) { total, venta ->
                            total.add(venta.total)
                        }

                    val efectivo =
                        ventasValidas
                            .filter {
                                it.metodoPago == MetodoPago.EFECTIVO
                            }
                            .fold(BigDecimal.ZERO) { total, venta ->
                                total.add(venta.total)
                            }

                    val tarjeta =
                        ventasValidas
                            .filter {
                                it.metodoPago == MetodoPago.TARJETA
                            }
                            .fold(BigDecimal.ZERO) { total, venta ->
                                total.add(venta.total)
                            }

                    val transferencia =
                        ventasValidas
                            .filter {
                                it.metodoPago == MetodoPago.TRANSFERENCIA
                            }
                            .fold(BigDecimal.ZERO) { total, venta ->
                                total.add(venta.total)
                            }

                    val totalEgresos =
                        egresosPorTurno.values
                            .flatten()
                            .fold(BigDecimal.ZERO) { total, egreso ->
                                total.add(egreso.monto)
                            }

                    val neto = totalVentas.subtract(totalEgresos)

                    val top = runCatching {
                        reporteRepository.topProductos()
                    }.getOrElse {
                        emptyList()
                    }

                    val actual = _state.value

                    _state.value = ReportesContractState(
                        loading = false,
                        ventas = ventas,
                        productos = productos,
                        topProductos = top,
                        turnos = turnos,
                        egresos = egresosPorTurno.values.flatten(),
                        dias = dias,

                        totalVentas = totalVentas,
                        efectivo = efectivo,
                        tarjeta = tarjeta,
                        transferencia = transferencia,
                        totalEgresos = totalEgresos,
                        neto = neto,

                        turnoSeleccionadoId =
                            actual.turnoSeleccionadoId
                                ?.takeIf { id ->
                                    turnos.any { it.id == id }
                                }
                                ?: turnos.firstOrNull()?.id,

                        message = null
                    )
                }
        }
    }

    fun seleccionarTurno(turnoId: Long) {
        _state.value = _state.value.copy(
            turnoSeleccionadoId = turnoId
        )
    }

    fun limpiarTurnoSeleccionado() {
        _state.value = _state.value.copy(
            turnoSeleccionadoId = null
        )
    }

    fun clearMessage() {
        _state.value = _state.value.copy(
            message = null
        )
    }
}
