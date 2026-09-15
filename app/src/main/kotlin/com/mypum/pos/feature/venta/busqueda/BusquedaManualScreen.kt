package com.mypum.pos.feature.venta.busqueda
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@Composable fun BusquedaManualScreen(onFinished:()->Unit={}) { Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center) { Column(horizontalAlignment=Alignment.CenterHorizontally) { Text("MyPuM • BusquedaManual"); Spacer(Modifier.height(12.dp)); Button(onClick=onFinished) { Text("Continuar") } } } }
