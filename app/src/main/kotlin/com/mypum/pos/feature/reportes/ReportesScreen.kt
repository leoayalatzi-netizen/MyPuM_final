package com.mypum.pos.feature.reportes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import java.math.BigDecimal

@Composable
fun ReportesScreen(
    viewModel: ReportesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 16.dp,
            bottom = 24.dp
        )
    ) {

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Assessment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.padding(6.dp))

                Text(
                    "Reportes",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Text(
                "Resumen de operación",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    "Ventas",
                    state.ventas.count { !it.cancelada }.toString(),
                    Icons.Default.PointOfSale,
                    Modifier.weight(1f)
                )

                MetricCard(
                    "Ingresos",
                    money(state.totalVendido),
                    Icons.Default.Assessment,
                    Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Indicadores",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ticket promedio")
                        Text(money(state.ticketPromedio))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Productos activos")
                        Text(state.productos.size.toString())
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Stock bajo")
                        Text(
                            state.productos.count {
                                it.stock <= it.stockMinimo
                            }.toString()
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Productos más vendidos",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (state.topProductos.isEmpty()) {

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        "Aún no hay ventas registradas.",
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

        } else {

            items(
                items = state.topProductos,
                key = { top -> "producto_${top.productoId}" }
            ) { top ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                top.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "${top.unidadesVendidas} unidades",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            money(
                                runCatching {
                                    BigDecimal.valueOf(top.totalVendido)
                                }.getOrDefault(BigDecimal.ZERO)
                            )
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Ventas recientes",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(
            items = state.ventas.take(10),
            key = { sale -> "venta_${sale.id}" }
        ) { sale ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {
                        Text("Venta #${sale.id}")

                        Text(
                            sale.createdAt.toString(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        runCatching {
                            money(sale.total)
                        }.getOrDefault("$0.00"),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    state.message?.let { message ->

        AlertDialog(
            onDismissRequest = viewModel::clearMessage,
            title = {
                Text("MyPuM")
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::clearMessage
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}
