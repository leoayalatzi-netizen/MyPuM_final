package com.mypum.pos.feature.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.repository.EgresoRepository
import com.mypum.pos.domain.repository.ProductoRepository
import com.mypum.pos.domain.repository.ReporteRepository
import com.mypum.pos.domain.repository.TurnoRepository
import com.mypum.pos.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
                turnoRepository.observeAll(),
                egresoRepository.observeAll()
            ) { ventas, productos, turnos, egresos ->
                QuadData(ventas, productos, turnos, egresos)
            }
                .catch { error ->
                    _state.value = _state.value.copy(
                        loading = false,
                        message = error.message
                            ?: "No se pudieron cargar los reportes"
                    )
                }
                .collect { data ->

                    val grupos = data.turnos
                        .map { turno ->
                            ReporteTurno(
                                turno = turno,
                                ventas = data.ventas.filter {
                                    it.turnoId == turno.id
                                },
                                egresos = data.egresos.filter {
                                    it.turnoId == turno.id
                                }
                            )
                        }
                        .groupBy { it.fecha }
                        .map { (fecha, turnos) ->
                            ReporteDia(
                                fecha = fecha,
                                turnos = turnos.sortedByDescending {
                                    it.turno.openedAt
                                }
                            )
                        }
                        .sortedByDescending { it.fecha }

                    val top = runCatching {
                        reporteRepository.topProductos()
                    }.getOrElse {
                        emptyList()
                    }

                    val actual = _state.value

                    _state.value = ReportesContractState(
                        loading = false,
                        ventas = data.ventas,
                        productos = data.productos,
                        topProductos = top,
                        turnos = data.turnos,
                        egresos = data.egresos,
                        dias = grupos,
                        turnoSeleccionadoId =
                            actual.turnoSeleccionadoId
                                ?.takeIf { id ->
                                    data.turnos.any { it.id == id }
                                }
                                ?: grupos.firstOrNull()
                                    ?.turnos
                                    ?.firstOrNull()
                                    ?.turno
                                    ?.id,
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
        _state.value = _state.value.copy(message = null)
    }

    private data class QuadData(
        val ventas: List<com.mypum.pos.domain.model.Venta>,
        val productos: List<com.mypum.pos.domain.model.Producto>,
        val turnos: List<com.mypum.pos.domain.model.Turno>,
        val egresos: List<com.mypum.pos.domain.model.Egreso>
    )
}
