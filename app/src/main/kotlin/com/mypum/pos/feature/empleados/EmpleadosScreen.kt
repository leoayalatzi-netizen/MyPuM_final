package com.mypum.pos.feature.empleados

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mypum.pos.domain.model.Usuario
import com.mypum.pos.domain.model.enumss.RolUsuario
import com.mypum.pos.domain.model.subscription.Plan

@Composable
fun EmpleadosScreen(
    onBack: () -> Unit = {},
    viewModel: EmpleadosViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var adminNombre by remember { mutableStateOf("") }
    var adminPin by remember { mutableStateOf("") }

    var nuevoNombre by remember { mutableStateOf("") }
    var nuevoPin by remember { mutableStateOf("") }
    var nuevoRol by remember { mutableStateOf(RolUsuario.CAJERO) }

    var mostrarFormulario by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text("‹", style = MaterialTheme.typography.headlineMedium)
            }

            Text(
                text = "Empleados y permisos",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(Modifier.height(12.dp))

        if (state.plan != Plan.PRO) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Función PRO",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "La administración de empleados y permisos está disponible en MyPuM PRO."
                    )
                }
            }

            return@Column
        }

        if (!state.autorizado) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Autorización de administrador",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Para administrar empleados confirma un usuario ADMIN."
                    )

                    OutlinedTextField(
                        value = adminNombre,
                        onValueChange = { adminNombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Usuario administrador") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = adminPin,
                        onValueChange = { adminPin = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            viewModel.autorizar(
                                adminNombre,
                                adminPin
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continuar")
                    }
                }
            }

            state.mensaje?.let {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.empleados.size} usuarios"
            )

            Button(
                onClick = {
                    mostrarFormulario = !mostrarFormulario
                }
            ) {
                Text(
                    if (mostrarFormulario) {
                        "Cancelar"
                    } else {
                        "Nuevo empleado"
                    }
                )
            }
        }

        if (mostrarFormulario) {
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Nuevo empleado",
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = nuevoPin,
                        onValueChange = { nuevoPin = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    Text("Rol")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (nuevoRol == RolUsuario.CAJERO) {
                            Button(
                                onClick = {
                                    nuevoRol = RolUsuario.CAJERO
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("CAJERO")
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    nuevoRol = RolUsuario.CAJERO
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("CAJERO")
                            }
                        }

                        if (nuevoRol == RolUsuario.ADMIN) {
                            Button(
                                onClick = {
                                    nuevoRol = RolUsuario.ADMIN
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ADMIN")
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    nuevoRol = RolUsuario.ADMIN
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ADMIN")
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.crearEmpleado(
                                nuevoNombre,
                                nuevoPin,
                                nuevoRol
                            )
                            nuevoNombre = ""
                            nuevoPin = ""
                            nuevoRol = RolUsuario.CAJERO
                            mostrarFormulario = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar empleado")
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        state.mensaje?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = state.empleados,
                key = { it.id }
            ) { empleado ->
                EmpleadoCard(
                    empleado = empleado,
                    onToggle = {
                        viewModel.cambiarActivo(empleado)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmpleadoCard(
    empleado: Usuario,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = empleado.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = empleado.rol.name,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = if (empleado.activo) {
                            "Activo"
                        } else {
                            "Inactivo"
                        },
                        color = if (empleado.activo) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }

                TextButton(onClick = onToggle) {
                    Text(
                        if (empleado.activo) {
                            "Desactivar"
                        } else {
                            "Activar"
                        }
                    )
                }
            }
        }
    }
}
