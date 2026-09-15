package com.mypum.pos.feature.inventario

import com.mypum.pos.core.UiEffect
import com.mypum.pos.core.UiEvent
import com.mypum.pos.core.UiState
import com.mypum.pos.domain.model.Producto

data class InventarioContractState(
    val loading: Boolean = true,
    val productos: List<Producto> = emptyList(),
    val message: String? = null
) : UiState

sealed interface InventarioContractEvent : UiEvent {
    data object Recargar : InventarioContractEvent
}

sealed interface InventarioContractEffect : UiEffect
