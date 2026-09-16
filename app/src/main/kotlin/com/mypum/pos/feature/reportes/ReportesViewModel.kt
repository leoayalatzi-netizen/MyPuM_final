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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
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
                turnoRepository.observeActivo()
            ) { ventas, productos, turnoActivo ->
                Triple(ventas, productos, turnoActivo)
            }
                .flatMapLatest { data ->

                    val turno = data.third

                    if (turno == null) {
                        flowOf(
                            ReportesData(
                                ventas = data.first,
                                productos = data.second,
                                turno = null,
                                egresos = emptyList()
                            )
                        )
                    } else {
                        egresoRepository
                            .byTurno(turno.id)
                            .combine(
                                flowOf(data.first)
                            ) { egresos, ventas ->
                                ReportesData(
                                    ventas = ventas,
                                    productos = data.second,
                                    turno = turno,
                                    egresos = egresos
                                )
                            }
                    }
                }
                .catch { error ->
                    _state.value = _state.value.copy(
                        loading = false,
                        message = error.message
                            ?: "No se pudieron cargar los reportes"
                    )
                }
                .collect { data ->

                    val grupos = data.turno?.let { turno ->

                        listOf(
                            ReporteTurno(
                                turno = turno,
                                ventas = data.ventas.filter {
                                    it.turnoId == turno.id
                                },
                                egresos = data.egresos
                            )
                        )
                    } ?: emptyList()

                    val dias = grupos
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
                        turnos = grupos.map { it.turno },
                        egresos = data.egresos,
                        dias = dias,
                        turnoSeleccionadoId =
                            actual.turnoSeleccionadoId
                                ?.takeIf { id ->
                                    grupos.any { it.turno.id == id }
                                }
                                ?: grupos.firstOrNull()
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
        _state.value = _state.value.copy(
            message = null
        )
    }

    private data class ReportesData(
        val ventas: List<com.mypum.pos.domain.model.Venta>,
        val productos: List<com.mypum.pos.domain.model.Producto>,
        val turno: com.mypum.pos.domain.model.Turno?,
        val egresos: List<com.mypum.pos.domain.model.Egreso>
    )
}
