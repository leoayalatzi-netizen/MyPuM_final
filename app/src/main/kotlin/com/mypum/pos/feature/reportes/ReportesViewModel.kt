package com.mypum.pos.feature.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.repository.ProductoRepository
import com.mypum.pos.domain.repository.ReporteRepository
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
    private val reporteRepository: ReporteRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow(ReportesContractState())

    val state: StateFlow<ReportesContractState> =
        _state.asStateFlow()

    init {
        cargar()
    }

    private fun cargar() {

        viewModelScope.launch {

            combine(
                ventaRepository.observeAll(),
                productoRepository.observeAll()
            ) { ventas, productos ->
                ventas to productos
            }
                .catch { error ->

                    _state.value =
                        _state.value.copy(
                            loading = false,
                            message =
                                error.message
                                    ?: "No se pudieron cargar los reportes"
                        )
                }
                .collect { (ventas, productos) ->

                    val top =
                        try {
                            reporteRepository.topProductos()
                        } catch (_: Exception) {
                            emptyList()
                        }

                    _state.value =
                        ReportesContractState(
                            loading = false,
                            ventas = ventas,
                            productos = productos,
                            topProductos = top,
                            message = null
                        )
                }
        }
    }

    fun clearMessage() {
        _state.value =
            _state.value.copy(message = null)
    }
}
