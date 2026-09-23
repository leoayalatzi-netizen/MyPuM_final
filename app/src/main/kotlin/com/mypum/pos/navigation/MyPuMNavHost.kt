package com.mypum.pos.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mypum.pos.feature.egresos.EgresosScreen
import com.mypum.pos.feature.egresos.EgresosViewModel
import com.mypum.pos.feature.empleados.EmpleadosScreen
import com.mypum.pos.feature.inventario.InventarioScreen
import com.mypum.pos.feature.reportes.ReportesScreen
import com.mypum.pos.feature.venta.VentaScreen

private data class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val destinations = listOf(
    Destination("venta", "Venta", Icons.Default.PointOfSale),
    Destination("inventario", "Inventario", Icons.Default.Inventory2),
    Destination("egresos", "Egresos", Icons.Default.AccountBalanceWallet),
    Destination("reportes", "Reportes", Icons.Default.Assessment)
)

@Composable
fun MyPuMNavHost(nav: NavHostController) {

    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            nav.navigate(destination.route) {
                                popUpTo("venta") {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = {
                            Text(destination.label)
                        }
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = nav,
            startDestination = "venta",
            modifier = Modifier.padding(padding)
        ) {
            composable("venta") {
                VentaScreen()
            }

            composable("inventario") {
                InventarioScreen()
            }

            composable("egresos") {
                val viewModel: EgresosViewModel = hiltViewModel()
                val turno by viewModel.turnoActivo.collectAsStateWithLifecycle()
                val egresos by viewModel.egresos.collectAsStateWithLifecycle()

                EgresosScreen(
                    turnoId = turno?.id ?: 0L,
                    egresos = egresos,
                    onRegistrar = viewModel::registrar,
                    onFinished = { nav.popBackStack() }
                )
            }

            composable("reportes") {
                ReportesScreen()
            }

            composable("empleados") {
                EmpleadosScreen(
                    onBack = {
                        nav.popBackStack()
                    }
                )
            }
        }
    }
}
