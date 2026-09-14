package com.mypum.pos.designsystem.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
@Composable fun MyPuMTheme(content:@Composable()->Unit){ MaterialTheme(typography=MyPuMTypography,shapes=MyPuMShapes,content=content) }
