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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cash
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mypum.pos.domain.model.Egreso
import com.mypum.pos.domain.model.Venta
import com.mypum.pos.domain.model.enums.MetodoPago
import java.math.BigDecimal
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

    val seleccionado = state.turnoSeleccionado

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 28.dp
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
                    text = "Ventas, turnos, egresos y cierres",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            SectionTitle(
                icon = Icons.Default.CalendarMonth,
                title = "Cierres por día"
            )
        }

        if (state.dias.isEmpty()) {
            item {
                EmptyCard(
                    "Todavía no existen turnos registrados."
                )
            }
        } else {
            items(
                items = state.dias,
                key = { dia -> dia.fecha.toString() }
            ) { dia ->

                var expanded by remember(dia.fecha) {
                    mutableStateOf(true)
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = formatDate(dia.fecha),
                                    style = MaterialTheme.typography.titleLarge
                                )

                                Text(
                                    text =
                                        "${dia.turnos.size} turno(s) • " +
                                            "${dia.numeroVentas} venta(s)",
                                    color =
                                        MaterialTheme.colorScheme
                                            .onSurfaceVariant
                                )
                            }

                            TextButton(
                                onClick = {
                                    expanded = !expanded
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        if (expanded)
                                            Icons.Default.ExpandLess
                                        else
                                            Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        DailySummary(dia)

                        if (expanded) {
                            Spacer(Modifier.height(12.dp))

                            dia.turnos.forEach { reporte ->

                                TurnoCard(
                                    reporte = reporte,
                                    selected =
                                        reporte.turno.id ==
                                            state.turnoSeleccionadoId,
                                    onClick = {
                                        viewModel.seleccionarTurno(
                                            reporte.turno.id
                                        )
                                    }
                                )

                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        if (seleccionado != null) {

            item {
                SectionTitle(
                    icon = Icons.Default.PointOfSale,
                    title = "Detalle del turno"
                )
            }

            item {
                TurnoDetailCard(
                    reporte = seleccionado,
                    onClear = {
                        viewModel.limpiarTurnoSeleccionado()
                    }
                )
            }
        }

        item {
            SectionTitle(
                icon = Icons.Default.ReceiptLong,
                title = "Resumen general"
            )
        }

        item {
            GeneralSummary(state)
        }

        item {
            SectionTitle(
                icon = Icons.Default.ShoppingCart,
                title = "Productos más vendidos"
            )
        }

        if (state.topProductos.isEmpty()) {
            item {
                EmptyCard(
                    "Aún no hay ventas registradas."
                )
            }
        } else {
            items(
                items = state.topProductos,
                key = { top ->
                    "producto_${top.productoId}"
                }
            ) { top ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = top.nombre,
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text =
                                    "${top.unidadesVendidas} unidades",
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )
                        }

                        Text(
                            text = money(top.totalVendido),
                            style =
                                MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailySummary(
    dia: ReporteDia
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            IndicatorRow(
                "Ventas",
                money(dia.ventas)
            )

            IndicatorRow(
                "Egresos",
                money(dia.egresos)
            )

            IndicatorRow(
                "Efectivo",
                money(dia.efectivo)
            )

            IndicatorRow(
                "Tarjeta",
                money(dia.tarjeta)
            )

            IndicatorRow(
                "Transferencia",
                money(dia.transferencia)
            )

            IndicatorRow(
                "Efectivo esperado",
                money(dia.efectivoEsperado)
            )
        }
    }
}

@Composable
private fun TurnoCard(
    reporte: ReporteTurno,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Cash,
                    contentDescription = null,
                    tint =
                        if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.padding(6.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Turno #${reporte.turno.id}",
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text =
                            "${formatTime(reporte.turno.openedAt)}" +
                                " → " +
                                (
                                    reporte.turno.closedAt
                                        ?.let(::formatTime)
                                        ?: "Abierto"
                                ),
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                Text(
                    text = money(reporte.totalVentas),
                    style =
                        MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text =
                    "${reporte.ventasValidas.size} ventas • " +
                        "Egresos ${money(reporte.totalEgresos)}",
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TurnoDetailCard(
    reporte: ReporteTurno,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Turno #${reporte.turno.id}",
                        style =
                            MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text =
                            if (reporte.turno.abierto)
                                "Turno abierto"
                            else
                                "Turno cerrado",
                        color =
                            if (reporte.turno.abierto)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = onClear) {
                    Text("Cerrar detalle")
                }
            }

            Spacer(Modifier.height(12.dp))

            IndicatorRow(
                "Apertura",
                formatDateTime(reporte.turno.openedAt)
            )

            IndicatorRow(
                "Cierre",
                reporte.turno.closedAt
                    ?.let(::formatDateTime)
                    ?: "Abierto"
            )

            IndicatorRow(
                "Fondo inicial",
                money(reporte.turno.fondoInicial)
            )

            IndicatorRow(
                "Ventas",
                money(reporte.totalVentas)
            )

            IndicatorRow(
                "Efectivo",
                money(reporte.efectivo)
            )

            IndicatorRow(
                "Tarjeta",
                money(reporte.tarjeta)
            )

            IndicatorRow(
                "Transferencia",
                money(reporte.transferencia)
            )

            IndicatorRow(
                "Egresos",
                money(reporte.totalEgresos)
            )

            IndicatorRow(
                "Efectivo esperado",
                money(reporte.efectivoEsperado)
            )

            if (reporte.turno.efectivoContado != null) {
                IndicatorRow(
                    "Efectivo contado",
                    money(reporte.turno.efectivoContado)
                )
                IndicatorRow(
                    "Diferencia",
                    signedMoney(
                        reporte.turno.diferencia ?: BigDecimal.ZERO
                    )
                )
            }

            IndicatorRow(
                "Ventas realizadas",
                reporte.ventasValidas.size.toString()
            )

            IndicatorRow(
                "Ventas canceladas",
                (reporte.ventas.size -
                    reporte.ventasValidas.size).toString()
            )

            if (reporte.egresos.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Egresos del turno",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                reporte.egresos.forEach { egreso ->
                    ExpenseRow(egreso)
                }
            }

            if (reporte.ventasValidas.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Ventas del turno",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                reporte.ventasValidas.forEach { venta ->
                    SaleRow(venta)
                }
            }
        }
    }
}

@Composable
private fun ExpenseRow(
    egreso: Egreso
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(egreso.concepto)

            Text(
                formatTime(egreso.createdAt),
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = money(egreso.monto),
            style =
                MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun SaleRow(
    venta: Venta
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("Venta #${venta.id}")

            Text(
                paymentLabel(venta.metodoPago),
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = money(venta.total),
            style =
                MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun GeneralSummary(
    state: ReportesContractState
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            MetricCard(
                title = "Ventas",
                value =
                    state.ventas.count {
                        !it.cancelada
                    }.toString(),
                icon = Icons.Default.PointOfSale,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            IndicatorRow(
                "Ingresos",
                money(state.totalVendido)
            )

            IndicatorRow(
                "Ticket promedio",
                money(state.ticketPromedio)
            )

            IndicatorRow(
                "Turnos",
                state.turnos.size.toString()
            )

            IndicatorRow(
                "Egresos registrados",
                money(
                    state.egresos.fold(BigDecimal.ZERO) {
                        acc, egreso ->
                        acc.add(egreso.monto)
                    }
                )
            )

            IndicatorRow(
                "Productos activos",
                state.productos.size.toString()
            )

            IndicatorRow(
                "Stock bajo",
                state.productos
                    .count {
                        it.stock <= it.stockMinimo
                    }
                    .toString()
            )
        }
    }
}

@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.padding(6.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
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
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.bodyLarge
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
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.padding(6.dp))

            Column {
                Text(
                    text = title,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )

                Text(
                    text = value,
                    style =
                        MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun EmptyCard(
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(20.dp)
        )
    }
}

private fun paymentLabel(
    metodoPago: MetodoPago
): String =
    when (metodoPago) {
        MetodoPago.EFECTIVO -> "Efectivo"
        MetodoPago.TARJETA -> "Tarjeta"
        MetodoPago.TRANSFERENCIA -> "Transferencia"
    }

private fun money(
    value: BigDecimal
): String =
    "$" + value.setScale(
        2,
        java.math.RoundingMode.HALF_UP
    ).toPlainString()

private fun signedMoney(value: BigDecimal): String {
    val sign = if (value >= BigDecimal.ZERO) "+" else ""
    return sign + money(value)
}

private fun formatDate(
    date: java.time.LocalDate
): String =
    date.format(
        DateTimeFormatter.ofPattern(
            "EEEE d 'de' MMMM 'de' yyyy",
            Locale("es", "MX")
        )
    ).replaceFirstChar {
        it.uppercase()
    }

private fun formatTime(
    instant: java.time.Instant
): String =
    instant
        .atZone(ZoneId.systemDefault())
        .format(
            DateTimeFormatter.ofPattern(
                "HH:mm"
            )
        )

private fun formatDateTime(
    instant: java.time.Instant
): String =
    instant
        .atZone(ZoneId.systemDefault())
        .format(
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy HH:mm"
            )
        )
