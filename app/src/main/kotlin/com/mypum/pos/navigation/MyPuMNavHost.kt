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

private data class MainDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val destinations = listOf(
    MainDestination(
        route = "venta",
        label = "Venta",
        icon = Icons.Default.PointOfSale
    ),
    MainDestination(
        route = "inventario",
        label = "Inventario",
        icon = Icons.Default.Inventory2
    ),
    MainDestination(
        route = "reportes",
        label = "Reportes",
        icon = Icons.Default.Assessment
    )
)

@Composable
fun MyPuMNavHost() {
    val nav = rememberNavController()

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showMainNavigation = currentRoute != "login"

    Scaffold(
        bottomBar = {
            if (showMainNavigation) {
                NavigationBar {
                    destinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                nav.navigate(destination.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
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
        }
    ) { paddingValues ->

        NavHost(
            navController = nav,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {

            composable("login") {
                LoginScreen {
                    nav.navigate("venta") {
                        popUpTo("login") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }

            composable("venta") {
                VentaScreen()
            }

            composable("inventario") {
                InventarioScreen()
            }

            composable("reportes") {
                ReportesScreen()
            }
        }
    }
}
