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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.mypum.pos.feature.venta.scanner.BarcodeScannerDialog
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun VentaScreen(
    viewModel: VentaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showOpenTurno by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var showCloseTurno by remember { mutableStateOf(false) }

    val turnoActual = state.turno

    val filtered =
        state.productos.filter { product ->
            state.query.isBlank() ||
                product.nombre.contains(state.query, true) ||
                product.codigo.orEmpty().contains(state.query, true)
        }

    Scaffold(
        floatingActionButton = {
            if (state.carrito.isNotEmpty()) {
                FloatingActionButton(
                    onClick = viewModel::requestCheckout
                ) {
                    Icon(
                        Icons.Default.PointOfSale,
                        contentDescription = "Cobrar"
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.PointOfSale,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.padding(5.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        "Venta",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        if (turnoActual == null)
                            "Turno cerrado"
                        else
                            "Turno #${turnoActual.id} abierto",
                        color =
                            if (turnoActual == null)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                    )
                }

                if (turnoActual == null) {
                    Button(
                        onClick = { showOpenTurno = true }
                    ) {
                        Text("Abrir")
                    }
                } else {
                    TextButton(
                        onClick = { showCloseTurno = true }
                    ) {
                        Text("Cerrar turno")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::search,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    placeholder = {
                        Text("Producto o código")
                    }
                )

                IconButton(
                    onClick = { showScanner = true }
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Escanear código"
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (state.carrito.isNotEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(12.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${state.carrito.size} artículos",
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text(
                                money(state.total),
                                style =
                                    MaterialTheme.typography.titleLarge,
                                color =
                                    MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        state.carrito.forEach {
                            CartRow(it, viewModel)
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = viewModel::requestCheckout,
                            modifier = Modifier.fillMaxWidth(),
                            enabled =
                                turnoActual != null
                        ) {
                            Text(
                                "COBRAR  ${money(state.total)}"
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(6.dp),
                contentPadding =
                    PaddingValues(bottom = 80.dp)
            ) {

                items(
                    filtered,
                    key = { it.id }
                ) { product ->

                    val inCart =
                        state.carrito.firstOrNull {
                            it.producto.id == product.id
                        }

                    Card(
                        Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column(
                                Modifier.weight(1f)
                            ) {
                                Text(
                                    product.nombre,
                                    style =
                                        MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    "${money(product.precio)}  ·  Stock ${
                                        product.stock
                                            .stripTrailingZeros()
                                            .toPlainString()
                                    }",
                                    color =
                                        MaterialTheme.colorScheme
                                            .onSurfaceVariant
                                )
                            }

                            if (inCart == null) {

                                IconButton(
                                    onClick = {
                                        viewModel.add(product.id)
                                    },
                                    enabled =
                                        product.stock >
                                            BigDecimal.ZERO
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Agregar"
                                    )
                                }

                            } else {

                                IconButton(
                                    onClick = {
                                        viewModel.decrease(product.id)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = "Quitar"
                                    )
                                }

                                Text(
                                    inCart.cantidad
                                        .stripTrailingZeros()
                                        .toPlainString()
                                )

                                IconButton(
                                    onClick = {
                                        viewModel.add(product.id)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Agregar"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    state.message?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::clearMessage,
            title = { Text("MyPuM") },
            text = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = viewModel::clearMessage
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showOpenTurno) {
        OpenTurnoDialog(
            onDismiss = {
                showOpenTurno = false
            },
            onConfirm = { fondo ->
                showOpenTurno = false
                viewModel.openTurno(fondo)
            }
        )
    }

    if (showCloseTurno) {
        var efectivoContado by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = {
                showCloseTurno = false
            },
            title = {
                Text("Cerrar turno")
            },
            text = {
                Column {
                    Text(
                        "Cuenta el efectivo de la caja e ingresa " +
                            "el importe contado."
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = efectivoContado,
                        onValueChange = {
                            efectivoContado = it
                        },
                        singleLine = true,
                        label = {
                            Text("Efectivo contado")
                        },
                        prefix = {
                            Text("$")
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "El sistema calculará automáticamente " +
                            "el efectivo esperado y la diferencia."
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCloseTurno = false
                        viewModel.closeTurno(efectivoContado)
                    },
                    enabled = efectivoContado.toBigDecimalOrNull() != null
                ) {
                    Text("Cerrar turno")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCloseTurno = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showScanner) {
        BarcodeScannerDialog(
            onBarcodeDetected = { code ->
                viewModel.addByCode(code); showScanner = false
            },
            onDismiss = {
                showScanner = false
            }
        )
    }

    if (state.showCheckout) {
        CheckoutDialog(
            total = state.total,
            onDismiss = viewModel::closeCheckout,
            onConfirm = viewModel::confirmPayment
        )
    }
}

@Composable
private fun OpenTurnoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var fondo by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Abrir turno")
        },
        text = {
            OutlinedTextField(
                value = fondo,
                onValueChange = { fondo = it },
                singleLine = true,
                label = {
                    Text("Fondo inicial")
                }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(fondo)
                }
            ) {
                Text("Abrir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun CartRow(
    item: ItemCarrito,
    viewModel: VentaViewModel
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.weight(1f)
        ) {
            Text(item.producto.nombre)

            Text(
                "${item.cantidad.stripTrailingZeros()} × " +
                    money(item.producto.precio),
                style =
                    MaterialTheme.typography.bodySmall
            )
        }

        Text(money(item.subtotal))

        IconButton(
            onClick = {
                viewModel.remove(item.producto.id)
            }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar"
            )
        }
    }
}

@Composable
private fun CheckoutDialog(
    total: BigDecimal,
    onDismiss: () -> Unit,
    onConfirm: (MetodoPago, String) -> Unit
) {
    var method by remember {
        mutableStateOf(MetodoPago.EFECTIVO)
    }

    var recibido by remember {
        mutableStateOf(total.toPlainString())
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Column {
                Text(
                    "Cobrar",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Text(
                    money(total),
                    style =
                        MaterialTheme.typography.headlineLarge,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    MetodoButton(
                        "EFECTIVO",
                        method == MetodoPago.EFECTIVO,
                        Modifier.weight(1f)
                    ) {
                        method = MetodoPago.EFECTIVO
                    }

                    MetodoButton(
                        "TARJETA",
                        method == MetodoPago.TARJETA,
                        Modifier.weight(1f)
                    ) {
                        method = MetodoPago.TARJETA
                    }
                }

                Row(
                    Modifier.fillMaxWidth()
                ) {
                    MetodoButton(
                        "TRANSFERENCIA",
                        method == MetodoPago.TRANSFERENCIA,
                        Modifier.fillMaxWidth()
                    ) {
                        method = MetodoPago.TRANSFERENCIA
                    }
                }

                if (method == MetodoPago.EFECTIVO) {

                    OutlinedTextField(
                        value = recibido,
                        onValueChange = {
                            recibido = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = {
                            Text("Efectivo recibido")
                        }
                    )

                    val received =
                        recibido.toBigDecimalOrNull()
                            ?: BigDecimal.ZERO

                    val change =
                        received.subtract(total)
                            .max(BigDecimal.ZERO)

                    Text(
                        "Cambio: ${money(change)}",
                        style =
                            MaterialTheme.typography.titleMedium
                    )
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        method,
                        recibido
                    )
                }
            ) {
                Text("CONFIRMAR COBRO")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun MetodoButton(
    text: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text)
        }
    } else {
        TextButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text)
        }
    }
}

private fun money(value: BigDecimal): String =
    NumberFormat
        .getCurrencyInstance(Locale("es", "MX"))
        .format(value)
