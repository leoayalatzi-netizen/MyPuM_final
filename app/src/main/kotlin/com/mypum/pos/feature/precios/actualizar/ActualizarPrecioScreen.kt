package com.mypum.pos.feature.precios.actualizar
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@Composable fun ActualizarPrecioScreen(onFinished:()->Unit={}) { Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center) { Column(horizontalAlignment=Alignment.CenterHorizontally) { Text("MyPuM • ActualizarPrecio"); Spacer(Modifier.height(12.dp)); Button(onClick=onFinished) { Text("Continuar") } } } }
