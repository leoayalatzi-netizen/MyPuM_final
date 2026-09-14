package com.mypum.pos.navigation
import kotlinx.serialization.Serializable
sealed interface Route { @Serializable data object Login:Route; @Serializable data object Venta:Route; @Serializable data object Inventario:Route; @Serializable data object Turno:Route; @Serializable data object Reportes:Route }
