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
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportesScreen(viewModel: ReportesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.loading) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(80.dp)); CircularProgressIndicator()
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Assessment, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.padding(6.dp))
                Text("Reportes", style = MaterialTheme.typography.headlineMedium)
            }
            Text("Resumen de operación", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Ventas", state.ventas.count { !it.cancelada }.toString(), Icons.Default.PointOfSale, Modifier.weight(1f))
                MetricCard("Ingresos", money(state.totalVendido), Icons.Default.Assessment, Modifier.weight(1f))
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Indicadores", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Ticket promedio"); Text(money(state.ticketPromedio)) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Productos activos"); Text(state.productos.size.toString()) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Stock bajo"); Text(state.productos.count { it.stock <= it.stockMinimo }.toString()) }
                }
            }
        }
        item { Text("Productos más vendidos", style = MaterialTheme.typography.titleLarge) }
        if (state.topProductos.isEmpty()) {
            item { Surface(Modifier.fillMaxWidth(), tonalElevation = 2.dp, shape = MaterialTheme.shapes.large) { Text("Aún no hay ventas registradas.", Modifier.padding(20.dp)) } }
        } else {
            items(state.topProductos, key = { it.productoId }) { top ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) { Text(top.nombre, style = MaterialTheme.typography.titleMedium); Text("${top.unidadesVendidas} unidades", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Text(money(BigDecimal.valueOf(top.totalVendido)))
                    }
                }
            }
        }
        item { Text("Ventas recientes", style = MaterialTheme.typography.titleLarge) }
        items(state.ventas.take(10), key = { it.id }) { sale ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("Venta #${sale.id}"); Text(SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date.from(sale.createdAt)), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Text(money(sale.total), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
    state.message?.let { message ->
        AlertDialog(onDismissRequest = viewModel::clearMessage, title = { Text("MyPuM") }, text = { Text(message) }, confirmButton = { TextButton(onClick = viewModel::clearMessage) { Text("Cerrar") } })
    }
}

@Composable
private fun MetricCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(modifier) {
        Column(Modifier.padding(14.dp)) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(8.dp)); Text(value, style = MaterialTheme.typography.titleLarge); Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

private fun money(value: BigDecimal): String = NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(value)
