





package com.mypum.pos.feature.inventario

import androidx.compose.ui.platform.LocalContext

import androidx.compose.material.icons.filled.FileUpload

import androidx.compose.material.icons.filled.FileDownload

import androidx.activity.result.contract.ActivityResultContracts

import androidx.activity.compose.rememberLauncherForActivityResult


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.model.subscription.Plan
import com.mypum.pos.feature.subscription.ProScreen
import com.mypum.pos.domain.model.enumss.UnidadMedida
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InventarioScreen(
    onEmpleados: () -> Unit = {},
    viewModel: InventarioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    var showProScreen by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            runCatching {
                val csv = InventarioCsv.exportar(state.productos)

                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write("\uFEFF".toByteArray(Charsets.UTF_8))
                    output.write(csv.toByteArray(Charsets.UTF_8))
                } ?: error("No se pudo crear el archivo.")
            }.onSuccess {
                viewModel.mostrarMensaje(
                    "Inventario exportado correctamente."
                )
            }.onFailure { error ->
                viewModel.mostrarMensaje(
                    error.message ?: "No se pudo exportar el inventario."
                )
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver
                    .openInputStream(uri)
                    ?.bufferedReader(Charsets.UTF_8)
                    ?.use { it.readText() }
                    ?: error("No se pudo leer el archivo.")
            }.onSuccess { csv ->
                when (val resultado = InventarioCsv.importar(csv)) {
                    is InventarioCsv.Resultado.Exito -> {
                        if (state.plan == Plan.PRO) {
                            viewModel.importarProductos(resultado.productos)
                        } else {
                            viewModel.mostrarMensaje(
                                "La importación de inventario es una función PRO."
                            )
                        }
                    }

                    is InventarioCsv.Resultado.Error -> {
                        viewModel.mostrarMensaje(resultado.mensaje)
                    }
                }
            }.onFailure { error ->
                viewModel.mostrarMensaje(
                    error.message ?: "No se pudo importar el inventario."
                )
            }
        }
    }

    val products = state.productos.filter {
        query.isBlank() ||
            it.nombre.contains(query, true) ||
            it.codigo.orEmpty().contains(query, true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::newProduct
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nuevo producto"
                )
            }
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Inventory2,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.padding(6.dp))

                Text(
                    "Inventario",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Text(
                buildString {
                    append(state.plan.name)
                    append(" · ")
                    append(state.productos.size)

                    state.limiteProductos?.let { limite ->
                        append("/")
                        append(limite)
                    }

                    append(" productos")
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state.plan == Plan.FREE) {
                Spacer(Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showProScreen = true
                        },
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "MyPuM PRO",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "$99 MXN/año · Productos ilimitados y más funciones",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "Ver",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, null)
                },
                label = {
                    Text("Buscar por nombre o código")
                }
            )

            Spacer(Modifier.height(12.dp))

            if (state.plan == Plan.PRO) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            exportLauncher.launch("MyPuM_inventario.csv")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = null
                        )

                        Spacer(Modifier.padding(horizontal = 4.dp))

                        Text("Exportar")
                    }

                    OutlinedButton(
                        onClick = {
                            importLauncher.launch(
                                arrayOf(
                                    "text/csv",
                                    "text/comma-separated-values",
                                    "text/*"
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.FileUpload,
                            contentDescription = null
                        )

                        Spacer(Modifier.padding(horizontal = 4.dp))

                        Text("Importar")
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            if (state.loading) {

                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(48.dp))
                    CircularProgressIndicator()
                }

            } else if (products.isEmpty()) {

                Surface(
                    Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (state.productos.isEmpty())
                                "Aún no hay productos"
                            else
                                "Sin coincidencias"
                        )

                        Spacer(Modifier.height(8.dp))

                        if (state.productos.isEmpty()) {
                            Text(
                                "Usa + para registrar el primero.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(
                        products,
                        key = { it.id }
                    ) { product ->

                        ProductCard(
                            producto = product,
                            onEdit = viewModel::edit,
                            onDelete = {
                                productoAEliminar = product
                            }
                        )
                    }
                }
            }
        }
    }

    if (showProScreen) {
        Dialog(
            onDismissRequest = {
                showProScreen = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                ProScreen(
                    onEmpleados = {
                        showProScreen = false
                        onEmpleados()
                    },
                    onBack = {
                        showProScreen = false
                    }
                )
            }
        }
    }

    if (state.showEditor) {
        ProductEditorDialog(
            producto = state.editing,
            onDismiss = viewModel::closeEditor,
            onSave = viewModel::save
        )
    }

    productoAEliminar?.let { producto ->

        AlertDialog(
            onDismissRequest = {
                productoAEliminar = null
            },
            title = {
                Text("Eliminar producto")
            },
            text = {
                Text(
                    "¿Seguro que deseas eliminar \"${producto.nombre}\" del inventario?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminar(producto)
                        productoAEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        productoAEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
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
private fun ProductCard(
    producto: Producto,
    onEdit: (Producto) -> Unit,
    onDelete: () -> Unit
) {
    val low = producto.stock <= producto.stockMinimo

    Card(
        Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.weight(1f)
                ) {
                    Text(
                        producto.nombre,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        producto.categoria ?: "Sin categoría",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { onEdit(producto) }
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar"
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar"
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Precio")

                Text(
                    NumberFormat
                        .getCurrencyInstance(Locale("es", "MX"))
                        .format(producto.precio)
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Existencia")

                Text(
                    "${producto.stock.stripTrailingZeros().toPlainString()} " +
                        producto.unidadMedida.name.lowercase()
                )
            }

            if (low) {
                Spacer(Modifier.height(8.dp))

                AssistChip(
                    onClick = {},
                    label = {
                        Text("Stock bajo")
                    }
                )
            }

            producto.codigo?.let {
                Text(
                    "Código: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductEditorDialog(
    producto: Producto?,
    onDismiss: () -> Unit,
    onSave: (
        Long,
        String,
        String,
        String,
        String,
        String,
        String,
        String,
        Boolean,
        UnidadMedida
    ) -> Unit
) {
    var nombre by remember(producto) {
        mutableStateOf(producto?.nombre.orEmpty())
    }

    var codigo by remember(producto) {
        mutableStateOf(producto?.codigo.orEmpty())
    }

    var precio by remember(producto) {
        mutableStateOf(producto?.precio?.toPlainString().orEmpty())
    }

    var costo by remember(producto) {
        mutableStateOf(producto?.costo?.toPlainString().orEmpty())
    }

    var stock by remember(producto) {
        mutableStateOf(producto?.stock?.toPlainString().orEmpty())
    }

    var minimo by remember(producto) {
        mutableStateOf(
            producto?.stockMinimo?.toPlainString() ?: "5"
        )
    }

    var categoria by remember(producto) {
        mutableStateOf(producto?.categoria.orEmpty())
    }

    var granel by remember(producto) {
        mutableStateOf(producto?.esGranel ?: false)
    }

    var unidad by remember(producto) {
        mutableStateOf(
            producto?.unidadMedida ?: UnidadMedida.PIEZA
        )
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (producto == null)
                    "Nuevo producto"
                else
                    "Editar producto"
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    nombre,
                    { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )

                OutlinedTextField(
                    codigo,
                    { codigo = it },
                    label = { Text("Código de barras") },
                    singleLine = true
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        precio,
                        { precio = it },
                        label = { Text("Precio") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        costo,
                        { costo = it },
                        label = { Text("Costo") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        stock,
                        { stock = it },
                        label = { Text("Stock") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        minimo,
                        { minimo = it },
                        label = { Text("Mínimo") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    categoria,
                    { categoria = it },
                    label = { Text("Categoría") },
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    OutlinedTextField(
                        value = unidad.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults
                                .TrailingIcon(expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        UnidadMedida.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(option.name)
                                },
                                onClick = {
                                    unidad = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                SwitchRow(
                    "Producto a granel",
                    granel
                ) {
                    granel = it
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        producto?.id ?: 0L,
                        nombre,
                        codigo,
                        precio,
                        costo,
                        stock,
                        minimo,
                        categoria,
                        granel,
                        unidad
                    )
                }
            ) {
                Text("Guardar")
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
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)

        androidx.compose.material3.Switch(
            checked,
            onCheckedChange
        )
    }
}
