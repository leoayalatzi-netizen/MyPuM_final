package com.mypum.pos.feature.inventario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mypum.pos.domain.model.Producto

@Composable
fun InventarioScreen(
    onFinished: () -> Unit = {}
) {
    val viewModel: InventarioViewModel = hiltViewModel()
    val productos by viewModel.productos.collectAsStateWithLifecycle()

    var productoAEliminar by remember {
        mutableStateOf<Producto?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Inventario",
            style = MaterialTheme.typography.headlineSmall
        )

        if (productos.isEmpty()) {
            Text(
                text = "No hay productos en el inventario.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = productos,
                    key = { producto -> producto.id }
                ) { producto ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 14.dp,
                                    top = 10.dp,
                                    bottom = 10.dp,
                                    end = 4.dp
                                )
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = producto.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(
                                    modifier = Modifier.padding(2.dp)
                                )

                                Text(
                                    text = "Código: ${producto.codigo}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            IconButton(
                                onClick = {
                                    productoAEliminar = producto
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar producto"
                                )
                            }
                        }
                    }
                }
            }
        }

        TextButton(
            onClick = onFinished,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
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
                    "¿Quieres eliminar \"${producto.nombre}\" del inventario?"
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
}
