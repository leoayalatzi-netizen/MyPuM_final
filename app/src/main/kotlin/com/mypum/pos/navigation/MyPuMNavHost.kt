package com.mypum.pos.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mypum.pos.feature.inventario.InventarioScreen
import com.mypum.pos.feature.login.LoginScreen
import com.mypum.pos.feature.reportes.ReportesScreen
import com.mypum.pos.feature.venta.VentaScreen

@Composable
fun MyPuMNavHost(
    nav: NavHostController
) {
    val backStackEntry = nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "login") {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "venta",
                        onClick = { nav.navigate("venta") },
                        icon = { Text("🛒") },
                        label = { Text("Venta") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "inventario",
                        onClick = { nav.navigate("inventario") },
                        icon = { Text("📦") },
                        label = { Text("Inventario") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "reportes",
                        onClick = { nav.navigate("reportes") },
                        icon = { Text("📊") },
                        label = { Text("Reportes") }
                    )
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
                LoginScreen(
                    onFinished = {
                        nav.navigate("venta") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
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
