package com.mypum.pos.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.mypum.pos.feature.login.LoginScreen
import com.mypum.pos.feature.venta.VentaScreen
import com.mypum.pos.feature.inventario.InventarioScreen
import com.mypum.pos.feature.reportes.ReportesScreen
@Composable fun MyPuMNavHost(){
 val nav=rememberNavController()
 NavHost(navController=nav,startDestination="login"){
  composable("login"){LoginScreen{nav.navigate("venta")}}
  composable("venta"){VentaScreen{nav.navigate("inventario")}}
  composable("inventario"){InventarioScreen()}
  composable("reportes"){ReportesScreen()}
 }
}
