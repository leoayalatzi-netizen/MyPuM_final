package com.mypum.pos.feature.reportes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

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
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 24.dp
        )
    ) {

        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.padding(6.dp))

                    Text(
                        text = "Reportes",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Text(
                    text = "Resumen de operación",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Ventas",
                    value = state.ventas.count { !it.cancelada }.toString(),
                    icon = Icons.Default.PointOfSale,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Ingresos",
                    value = money(state.totalVendido),
                    icon = Icons.Default.Assessment,
                    modifier = Modifier.weight(1f)
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
                        text = "Indicadores",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    IndicatorRow(
                        label = "Ticket promedio",
                        value = money(state.ticketPromedio)
                    )

                    IndicatorRow(
                        label = "Productos activos",
                        value = state.productos.size.toString()
                    )

                    IndicatorRow(
                        label = "Stock bajo",
                        value = state.productos
                            .count { it.stock <= it.stockMinimo }
                            .toString()
                    )
                }
            }
        }

        item {
            Text(
                text = "Productos más vendidos",
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
                        text = "Aún no hay ventas registradas.",
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
                                text = top.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "${top.unidadesVendidas} unidades",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = money(
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
                text = "Ventas recientes",
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
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Venta #${sale.id}")

                        Text(
                            text = sale.createdAt.toString(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = runCatching {
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

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.padding(4.dp))

                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun IndicatorRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Text(value)
    }
}

private fun money(value: BigDecimal): String =
    NumberFormat
        .getCurrencyInstance(Locale("es", "MX"))
        .format(value)
