package com.mypum.pos.feature.reportes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mypum.pos.domain.model.ProductoTop
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun ReportesScreen(
    viewModel: ReportesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            state.loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Reportes",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    item {
                        ResumenFinanciero(state)
                    }

                    item {
                        Text(
                            text = "Métodos de pago",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    item {
                        MetodosPagoCard(state)
                    }

                    item {
                        Text(
                            text = "Productos más vendidos",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    if (state.topProductos.isEmpty()) {
                        item {
                            Text(
                                text = "Todavía no hay productos vendidos."
                            )
                        }
                    } else {
                        items(
                            state.topProductos.take(10),
                            key = { it.productoId }
                        ) { producto ->
                            ProductoTopCard(producto)
                        }
                    }

                    item {
                        Text(
                            text = "Historial de turnos",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    if (state.turnos.isEmpty()) {
                        item {
                            Text("Todavía no hay turnos registrados.")
                        }
                    } else {
                        items(
                            state.turnos,
                            key = { it.id }
                        ) { turno ->
                            val ventasTurno = state.ventas.filter {
                                it.turnoId == turno.id && !it.cancelada
                            }

                            val egresosTurno = state.egresos.filter {
                                it.turnoId == turno.id
                            }

                            val totalTurno =
                                ventasTurno.fold(BigDecimal.ZERO) { total, venta ->
                                    total.add(venta.total)
                                }

                            val egresos =
                                egresosTurno.fold(BigDecimal.ZERO) { total, egreso ->
                                    total.add(egreso.monto)
                                }

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Turno #${turno.id}",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = if (turno.abierto) {
                                            "Estado: abierto"
                                        } else {
                                            "Estado: cerrado"
                                        }
                                    )

                                    Text(
                                        text = "Fondo inicial: ${money(turno.fondoInicial)}"
                                    )

                                    Text(
                                        text = "Ventas: ${money(totalTurno)}"
                                    )

                                    Text(
                                        text = "Egresos: ${money(egresos)}"
                                    )

                                    Text(
                                        text = "Neto: ${money(totalTurno.subtract(egresos))}"
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Resumen por día",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(
                        state.dias,
                        key = { it.fecha.toString() }
                    ) { dia ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = dia.fecha.toString(),
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(Modifier.height(6.dp))

                                val ventas = dia.turnos
                                    .flatMap { it.ventas }
                                    .filter { !it.cancelada }

                                val egresos = dia.turnos
                                    .flatMap { it.egresos }

                                val totalVentas =
                                    ventas.fold(BigDecimal.ZERO) { total, venta ->
                                        total.add(venta.total)
                                    }

                                val totalEgresos =
                                    egresos.fold(BigDecimal.ZERO) { total, egreso ->
                                        total.add(egreso.monto)
                                    }

                                Text("Turnos: ${dia.turnos.size}")
                                Text("Ventas: ${ventas.size}")
                                Text("Ingresos: ${money(totalVentas)}")
                                Text("Egresos: ${money(totalEgresos)}")
                                Text(
                                    "Neto: ${
                                        money(totalVentas.subtract(totalEgresos))
                                    }"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenFinanciero(
    state: ReportesContractState
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen financiero",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Ventas: ${money(state.totalVentas)}",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Egresos: ${money(state.totalEgresos)}")

            Text(
                text = "Neto: ${money(state.neto)}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun MetodosPagoCard(
    state: ReportesContractState
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            MetodoRow("Efectivo", state.efectivo)
            MetodoRow("Tarjeta", state.tarjeta)
            MetodoRow("Transferencia", state.transferencia)
        }
    }
}

@Composable
private fun MetodoRow(
    nombre: String,
    importe: BigDecimal
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(nombre)
        Text(money(importe))
    }
}

@Composable
private fun ProductoTopCard(
    producto: ProductoTop
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Unidades: ${producto.unidadesVendidas}"
            )

            Text(
                text = "Vendido: ${
                    money(
                        producto.totalVendido.toBigDecimal()
                    )
                }"
            )
        }
    }
}

private fun money(
    value: BigDecimal
): String {
    return "$ ${
        value.setScale(
            2,
            RoundingMode.HALF_UP
        )
    }"
}
