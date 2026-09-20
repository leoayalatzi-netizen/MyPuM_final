package com.mypum.pos.feature.inventario
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@Composable fun EntradaMercanciaScreen(onFinished:()->Unit={}) { Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center) { Column(horizontalAlignment=Alignment.CenterHorizontally) { Text("MyPuM • EntradaMercancia"); Spacer(Modifier.height(12.dp)); Button(onClick=onFinished) { Text("Continuar") } } } }
