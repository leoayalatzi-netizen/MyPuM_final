package com.mypum.pos.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1769FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE7FF),
    onPrimaryContainer = Color(0xFF001A41),
    secondary = Color(0xFF465D91),
    tertiary = Color(0xFF006A67),
    background = Color(0xFFF9F9FD),
    surface = Color(0xFFF9F9FD)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFADC6FF),
    onPrimary = Color(0xFF002F68),
    primaryContainer = Color(0xFF004594),
    onPrimaryContainer = Color(0xFFDCE7FF),
    secondary = Color(0xFFB8C6EA),
    tertiary = Color(0xFF7DD8D4)
)

@Composable
fun MyPuMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MyPuMTypography,
        shapes = MyPuMShapes,
        content = content
    )
}
