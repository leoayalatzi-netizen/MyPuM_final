package com.mypum.pos.designsystem.component
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
@Composable fun MyPuMTextField(value:String,onValueChange:(String)->Unit)=TextField(value,onValueChange)
