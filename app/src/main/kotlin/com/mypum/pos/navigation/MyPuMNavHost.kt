package com.mypum.pos.navigation

import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mypum.pos.feature.inventario.InventarioScreen
import com.mypum.pos.feature.login.LoginScreen
import com.mypum.pos.feature.reportes.ReportesScreen
import com.mypum.pos.feature.venta.VentaScreen

private data class Destination(val route: String, val label: String, val icon: ImageVector)

private val destinations = listOf(
    Destination("venta", "Venta", Icons.Default.PointOfSale),
    Destination("inventario", "Inventario", Icons.Default.Inventory2),
    Destination("reportes", "Reportes", Icons.Default.Assessment)
)

@Composable
fun MyPuMNavHost() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "login") {
                NavigationBar {
                    destinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                nav.navigate(destination.route) {
                                    popUpTo("venta") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "login",
            modifier = Modifier.padding(padding)
        ) {
            composable("login") {
                LoginScreen {
                    nav.navigate("venta") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            composable("venta") { VentaScreen() }
            composable("inventario") { InventarioScreen() }
            composable("reportes") { ReportesScreen() }
        }
    }
}
