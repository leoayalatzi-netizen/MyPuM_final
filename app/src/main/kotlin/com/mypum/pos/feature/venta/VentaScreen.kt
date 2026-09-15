package com.mypum.pos.feature.venta

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mypum.pos.domain.model.ItemCarrito
import com.mypum.pos.domain.model.enums.MetodoPago
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun VentaScreen(viewModel: VentaViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showOpenTurno by remember { mutableStateOf(false) }
    val filtered = state.productos.filter { product ->
        state.query.isBlank() || product.nombre.contains(state.query, true) || product.codigo.orEmpty().contains(state.query, true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::requestCheckout, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.PointOfSale, "Cobrar")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PointOfSale, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.padding(6.dp))
                Text("Venta", style = MaterialTheme.typography.headlineMedium)
            }
            val turnoActual = state.turno
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(if (turnoActual == null) "Sin turno abierto" else "Turno #${turnoActual.id}", color = if (turnoActual == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    Text("${state.carrito.size} artículos · ${money(state.total)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (turnoActual == null) TextButton(onClick = { showOpenTurno = true }) { Text("Abrir turno") }
            }
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::search,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                label = { Text("Buscar producto") }
            )
            Spacer(Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(filtered, key = { it.id }) { product ->
                    val inCart = state.carrito.firstOrNull { it.producto.id == product.id }
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(product.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("${money(product.precio)} · Stock ${product.stock.stripTrailingZeros().toPlainString()}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (inCart == null) {
                                IconButton(onClick = { viewModel.add(product.id) }, enabled = product.stock > BigDecimal.ZERO) { Icon(Icons.Default.Add, "Agregar") }
                            } else {
                                IconButton(onClick = { viewModel.decrease(product.id) }) { Icon(Icons.Default.Remove, "Quitar") }
                                Text(inCart.cantidad.stripTrailingZeros().toPlainString())
                                IconButton(onClick = { viewModel.add(product.id) }) { Icon(Icons.Default.Add, "Agregar") }
                            }
                        }
                    }
                }
            }

            if (state.carrito.isNotEmpty()) {
                Divider()
                Spacer(Modifier.height(8.dp))
                state.carrito.forEach { CartRow(it, viewModel) }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL", style = MaterialTheme.typography.titleLarge)
                    Text(money(state.total), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = viewModel::requestCheckout, Modifier.fillMaxWidth()) { Text("Cobrar ${money(state.total)}") }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    state.message?.let { message ->
        AlertDialog(onDismissRequest = viewModel::clearMessage, title = { Text("MyPuM") }, text = { Text(message) }, confirmButton = { TextButton(onClick = viewModel::clearMessage) { Text("OK") } })
    }

    if (state.showCheckout) CheckoutDialog(state.total, viewModel::closeCheckout, viewModel::confirmPayment)
    if (showOpenTurno) OpenTurnoDialog(onDismiss = { showOpenTurno = false }) { fondo ->
        showOpenTurno = false
        viewModel.openTurno(fondo)
    }
}


@Composable
private fun OpenTurnoDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var fondo by remember { mutableStateOf("0") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Abrir turno") },
        text = {
            OutlinedTextField(
                value = fondo,
                onValueChange = { fondo = it },
                singleLine = true,
                label = { Text("Fondo inicial") }
            )
        },
        confirmButton = { Button(onClick = { onConfirm(fondo) }) { Text("Abrir") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun CartRow(item: ItemCarrito, viewModel: VentaViewModel) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(item.producto.nombre)
            Text("${item.cantidad.stripTrailingZeros().toPlainString()} × ${money(item.producto.precio)}", style = MaterialTheme.typography.bodySmall)
        }
        Text(money(item.subtotal))
        IconButton(onClick = { viewModel.remove(item.producto.id) }) { Icon(Icons.Default.Delete, "Eliminar") }
    }
}

@Composable
private fun CheckoutDialog(total: BigDecimal, onDismiss: () -> Unit, onConfirm: (MetodoPago, String) -> Unit) {
    var method by remember { mutableStateOf(MetodoPago.EFECTIVO) }
    var recibido by remember { mutableStateOf(total.toPlainString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cobrar ${money(total)}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Método de pago")
                MetodoPago.entries.forEach { option ->
                    androidx.compose.material3.FilterChip(
                        selected = method == option,
                        onClick = { method = option },
                        label = { Text(option.name.lowercase().replaceFirstChar(Char::uppercase)) }
                    )
                }
                if (method == MetodoPago.EFECTIVO) {
                    OutlinedTextField(recibido, { recibido = it }, label = { Text("Efectivo recibido") }, singleLine = true)
                    val change = (recibido.toBigDecimalOrNull() ?: BigDecimal.ZERO).subtract(total)
                    Text("Cambio: ${money(change.max(BigDecimal.ZERO))}")
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(method, recibido) }) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun money(value: BigDecimal): String = NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(value)
