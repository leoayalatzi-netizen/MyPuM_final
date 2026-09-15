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
    private val _state = MutableStateFlow(ReportesContractState())
    val state: StateFlow<ReportesContractState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(ventaRepository.observeAll(), productoRepository.observeAll()) { ventas, productos -> ventas to productos }
                .catch { error -> _state.value = _state.value.copy(loading = false, message = error.message) }
                .collect { (ventas, productos) ->
                    val top = runCatching { reporteRepository.topProductos() }.getOrDefault(emptyList())
                    _state.value = ReportesContractState(false, ventas, productos, top)
                }
        }
    }

    fun clearMessage() { _state.value = _state.value.copy(message = null) }
}
