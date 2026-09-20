package com.mypum.pos.feature.inventario

import com.mypum.pos.core.UiEffect
import com.mypum.pos.core.UiEvent
import com.mypum.pos.core.UiState
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.model.subscription.Plan
import com.mypum.pos.domain.model.subscription.PlanEntitlements

data class InventarioContractState(
    val loading: Boolean = true,
    val message: String? = null,
    val productos: List<Producto> = emptyList(),
    val showEditor: Boolean = false,
    val editing: Producto? = null,
    val plan: Plan = Plan.FREE,
    val limiteProductos: Int? = PlanEntitlements.FREE.maxProducts
) : UiState

sealed interface InventarioContractEvent : UiEvent

sealed interface InventarioContractEffect : UiEffect
