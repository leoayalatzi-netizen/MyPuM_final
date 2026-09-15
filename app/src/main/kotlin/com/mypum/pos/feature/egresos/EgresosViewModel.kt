package com.mypum.pos.feature.egresos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.model.Egreso
import com.mypum.pos.domain.model.Turno
import com.mypum.pos.domain.repository.EgresoRepository
import com.mypum.pos.domain.repository.TurnoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal

@HiltViewModel
class EgresosViewModel @Inject constructor(
    private val egresoRepository: EgresoRepository,
    private val turnoRepository: TurnoRepository
) : ViewModel() {

    val turnoActivo: StateFlow<Turno?> =
        turnoRepository.observeActivo()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    val egresos: StateFlow<List<Egreso>> =
        turnoActivo
            .flatMapLatest { turno ->
                if (turno == null) {
                    flowOf(emptyList())
                } else {
                    egresoRepository.byTurno(turno.id)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun registrar(concepto: String, montoTexto: String) {
        val turno = turnoActivo.value ?: return

        val monto = montoTexto
            .replace(",", ".")
            .toBigDecimalOrNull()
            ?: return

        if (concepto.isBlank() || monto <= BigDecimal.ZERO) return

        viewModelScope.launch {
            egresoRepository.registrar(
                Egreso(
                    turnoId = turno.id,
                    concepto = concepto.trim(),
                    monto = monto
                )
            )
        }
    }
}
