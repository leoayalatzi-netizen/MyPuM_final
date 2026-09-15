package com.mypum.pos.feature.historial_ventas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable fun DetalleVentaScreen(onFinished:()->Unit={}) { Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center) { Column(horizontalAlignment=Alignment.CenterHorizontally) { Text("MyPuM • DetalleVenta"); Spacer(Modifier.height(12.dp)); Button(onClick=onFinished) { Text("Continuar") } } } }
