package com.mypum.pos.feature.inventario

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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.mypum.pos.domain.model.enums.UnidadMedida
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InventarioScreen(viewModel: InventarioViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val products = state.productos.filter {
        query.isBlank() || it.nombre.contains(query, true) || it.codigo.orEmpty().contains(query, true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::newProduct) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo producto")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Inventory2, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.padding(6.dp))
                Text("Inventario", style = MaterialTheme.typography.headlineMedium)
            }
            Text("${state.productos.size} productos activos", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                label = { Text("Buscar por nombre o código") }
            )
            Spacer(Modifier.height(12.dp))
            if (state.loading) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(48.dp)); CircularProgressIndicator()
                }
            } else if (products.isEmpty()) {
                Surface(Modifier.fillMaxWidth(), tonalElevation = 2.dp, shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (state.productos.isEmpty()) "Aún no hay productos" else "Sin coincidencias")
                        Spacer(Modifier.height(8.dp))
                        if (state.productos.isEmpty()) Text("Usa + para registrar el primero.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 90.dp)) {
                    items(products, key = { it.id }) { product -> ProductCard(product, viewModel::edit) }
                }
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

    state.message?.let { message ->
        AlertDialog(onDismissRequest = viewModel::clearMessage, title = { Text("MyPuM") }, text = { Text(message) }, confirmButton = { TextButton(onClick = viewModel::clearMessage) { Text("Cerrar") } })
    }
}

@Composable
private fun ProductCard(producto: Producto, onEdit: (Producto) -> Unit) {
    val low = producto.stock <= producto.stockMinimo
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(producto.nombre, style = MaterialTheme.typography.titleLarge)
                    Text(producto.categoria ?: "Sin categoría", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { onEdit(producto) }) { Icon(Icons.Default.Edit, "Editar") }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Precio")
                Text(NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(producto.precio))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Existencia")
                Text("${producto.stock.stripTrailingZeros().toPlainString()} ${producto.unidadMedida.name.lowercase()}")
            }
            if (low) {
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = { }, label = { Text("Stock bajo") })
            }
            producto.codigo?.let { Text("Código: $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductEditorDialog(
    producto: Producto?,
    onDismiss: () -> Unit,
    onSave: (Long, String, String, String, String, String, String, String, Boolean, UnidadMedida) -> Unit
) {
    var nombre by remember(producto) { mutableStateOf(producto?.nombre.orEmpty()) }
    var codigo by remember(producto) { mutableStateOf(producto?.codigo.orEmpty()) }
    var precio by remember(producto) { mutableStateOf(producto?.precio?.toPlainString().orEmpty()) }
    var costo by remember(producto) { mutableStateOf(producto?.costo?.toPlainString().orEmpty()) }
    var stock by remember(producto) { mutableStateOf(producto?.stock?.toPlainString().orEmpty()) }
    var minimo by remember(producto) { mutableStateOf(producto?.stockMinimo?.toPlainString() ?: "5") }
    var categoria by remember(producto) { mutableStateOf(producto?.categoria.orEmpty()) }
    var granel by remember(producto) { mutableStateOf(producto?.esGranel ?: false) }
    var unidad by remember(producto) { mutableStateOf(producto?.unidadMedida ?: UnidadMedida.PIEZA) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (producto == null) "Nuevo producto" else "Editar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(codigo, { codigo = it }, label = { Text("Código de barras") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(precio, { precio = it }, label = { Text("Precio") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(costo, { costo = it }, label = { Text("Costo") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(stock, { stock = it }, label = { Text("Stock") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(minimo, { minimo = it }, label = { Text("Mínimo") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(categoria, { categoria = it }, label = { Text("Categoría") }, singleLine = true)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(value = unidad.name, onValueChange = {}, readOnly = true, label = { Text("Unidad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor())
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        UnidadMedida.entries.forEach { option -> DropdownMenuItem(text = { Text(option.name) }, onClick = { unidad = option; expanded = false }) }
                    }
                }
                SwitchRow("Producto a granel", granel) { granel = it }
            }
        },
        confirmButton = { Button(onClick = { onSave(producto?.id ?: 0L, nombre, codigo, precio, costo, stock, minimo, categoria, granel, unidad) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        androidx.compose.material3.Switch(checked, onCheckedChange)
    }
}
