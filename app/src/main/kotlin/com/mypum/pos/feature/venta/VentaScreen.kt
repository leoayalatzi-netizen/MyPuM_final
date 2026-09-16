package com.mypum.pos.feature.venta

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (state.carrito.isNotEmpty()) {
                Button(
                    onClick = viewModel::requestCheckout,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 22.dp
                    )
                ) {
                    Icon(
                        Icons.Default.PointOfSale,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        "COBRAR",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Spacer(Modifier.height(4.dp))

            // ============================================================
            // CABECERA
            // ============================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(27.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Nueva venta",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (turnoActual == null)
                            "Caja cerrada"
                        else
                            "Turno #${turnoActual.id} · Caja abierta",
                        style = MaterialTheme.typography.bodyMedium,
                        color =
                            if (turnoActual == null)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                    )
                }

                if (turnoActual == null) {
                    Button(
                        onClick = { showOpenTurno = true },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Abrir")
                    }
                } else {
                    OutlinedButton(
                        onClick = { showCloseTurno = true },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Cerrar")
                    }
                }
            }

            // ============================================================
            // BUSCADOR
            // ============================================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::search,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        placeholder = {
                            Text("Buscar producto o código")
                        }
                    )

                    Spacer(Modifier.width(6.dp))

                    IconButton(
                        onClick = { showScanner = true },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer
                            )
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Escanear código",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // ============================================================
            // CARRITO
            // ============================================================

            if (state.carrito.isNotEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(Modifier.width(10.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Venta actual",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "${state.carrito.size} artículos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                money(state.total),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Divider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Spacer(Modifier.height(6.dp))

                        state.carrito.forEach { item ->
                            CartRow(
                                item = item,
                                viewModel = viewModel
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = viewModel::requestCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = turnoActual != null,
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.PointOfSale,
                                contentDescription = null
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                "COBRAR  ${money(state.total)}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ============================================================
            // CARGANDO
            // ============================================================

            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // ============================================================
            // PRODUCTOS
            // ============================================================

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    top = 2.dp,
                    bottom = 88.dp
                )
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
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 11.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    product.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    "${money(product.precio)}  ·  Stock ${
                                        product.stock
                                            .stripTrailingZeros()
                                            .toPlainString()
                                    }",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (!product.codigo.isNullOrBlank()) {
                                    Spacer(Modifier.height(2.dp))

                                    Text(
                                        "Código ${product.codigo}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (inCart == null) {

                                IconButton(
                                    onClick = {
                                        viewModel.add(product.id)
                                    },
                                    enabled =
                                        product.stock > BigDecimal.ZERO,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Agregar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                            } else {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    IconButton(
                                        onClick = {
                                            viewModel.decrease(product.id)
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Remove,
                                            contentDescription = "Quitar"
                                        )
                                    }

                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            inCart.cantidad
                                                .stripTrailingZeros()
                                                .toPlainString(),
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 6.dp
                                            ),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.add(product.id)
                                        },
                                        modifier = Modifier.size(40.dp)
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
    }

    // ================================================================
    // MENSAJE
    // ================================================================

    state.message?.let { message ->

        AlertDialog(
            onDismissRequest = viewModel::clearMessage,
            title = {
                Text(
                    "MyPuM",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::clearMessage
                ) {
                    Text("OK")
                }
            }
        )
    }

    // ================================================================
    // ABRIR TURNO
    // ================================================================

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

    // ================================================================
    // CERRAR TURNO
    // ================================================================

    if (showCloseTurno) {

        var efectivoContado by remember {
            mutableStateOf("")
        }

        AlertDialog(
            onDismissRequest = {
                showCloseTurno = false
            },
            title = {
                Text(
                    "Cerrar turno",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {

                Column {

                    Text(
                        "Cuenta el efectivo de la caja e ingresa " +
                            "el importe contado."
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = efectivoContado,
                        onValueChange = {
                            efectivoContado = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        label = {
                            Text("Efectivo contado")
                        },
                        prefix = {
                            Text("$ ")
                        }
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "El sistema calculará automáticamente " +
                            "el efectivo esperado y la diferencia.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {

                Button(
                    onClick = {
                        showCloseTurno = false
                        viewModel.closeTurno(efectivoContado)
                    },
                    enabled =
                        efectivoContado.toBigDecimalOrNull() != null
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

    // ================================================================
    // ESCÁNER
    // ================================================================

    if (showScanner) {

        BarcodeScannerDialog(
            onBarcodeDetected = { code ->
                viewModel.addByCode(code)
                showScanner = false
            },
            onDismiss = {
                showScanner = false
            }
        )
    }

    // ================================================================
    // CHECKOUT
    // ================================================================

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
    var fondo by remember {
        mutableStateOf("0")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Abrir turno",
                fontWeight = FontWeight.Bold
            )
        },
        text = {

            Column {

                Text(
                    "Indica el efectivo disponible al comenzar.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = fondo,
                    onValueChange = {
                        fondo = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    label = {
                        Text("Fondo inicial")
                    },
                    prefix = {
                        Text("$ ")
                    }
                )
            }
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

            TextButton(
                onClick = onDismiss
            ) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                item.producto.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(2.dp))

            Text(
                "${item.cantidad.stripTrailingZeros()} × " +
                    money(item.producto.precio),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            money(item.subtotal),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = {
                viewModel.remove(item.producto.id)
            }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = MaterialTheme.colorScheme.error
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PointOfSale,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        "Cobrar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    money(total),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    "Método de pago",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    MetodoButton(
                        text = "EFECTIVO",
                        selected = method == MetodoPago.EFECTIVO,
                        modifier = Modifier.weight(1f)
                    ) {
                        method = MetodoPago.EFECTIVO
                    }

                    MetodoButton(
                        text = "TARJETA",
                        selected = method == MetodoPago.TARJETA,
                        modifier = Modifier.weight(1f)
                    ) {
                        method = MetodoPago.TARJETA
                    }
                }

                MetodoButton(
                    text = "TRANSFERENCIA",
                    selected = method == MetodoPago.TRANSFERENCIA,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    method = MetodoPago.TRANSFERENCIA
                }

                if (method == MetodoPago.EFECTIVO) {

                    OutlinedTextField(
                        value = recibido,
                        onValueChange = {
                            recibido = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        label = {
                            Text("Efectivo recibido")
                        },
                        prefix = {
                            Text("$ ")
                        }
                    )

                    val received =
                        recibido.toBigDecimalOrNull()
                            ?: BigDecimal.ZERO

                    val change =
                        received.subtract(total)
                            .max(BigDecimal.ZERO)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                "Cambio",
                                style =
                                    MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                money(change),
                                style =
                                    MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color =
                                    MaterialTheme.colorScheme.primary
                            )
                        }
                    }
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
                Text(
                    "CONFIRMAR COBRO",
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
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
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(
                horizontal = 8.dp,
                vertical = 10.dp
            )
        ) {
            Text(
                text,
                fontWeight = FontWeight.Bold
            )
        }

    } else {

        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(
                horizontal = 8.dp,
                vertical = 10.dp
            )
        ) {
            Text(text)
        }
    }
}

private fun money(value: BigDecimal): String =
    NumberFormat
        .getCurrencyInstance(Locale("es", "MX"))
        .format(value)
