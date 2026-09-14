package com.mypum.pos.designsystem.component
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
@Composable fun MyPuMButton(text:String,onClick:()->Unit)=Button(onClick){Text(text)}