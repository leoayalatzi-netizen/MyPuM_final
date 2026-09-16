package com.mypum.pos.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MyPuMLightColorScheme = lightColorScheme(

    primary = MyPuMBlue,
    onPrimary = MyPuMWhite,

    primaryContainer = MyPuMBlueContainer,
    onPrimaryContainer = MyPuMBlueDark,

    secondary = MyPuMTeal,
    onSecondary = MyPuMWhite,

    secondaryContainer = MyPuMTealSoft,
    onSecondaryContainer = MyPuMDark,

    tertiary = MyPuMOrange,
    onTertiary = MyPuMWhite,

    tertiaryContainer = MyPuMOrangeSoft,
    onTertiaryContainer = MyPuMDark,

    background = MyPuMBackground,
    onBackground = MyPuMDark,

    surface = MyPuMSurface,
    onSurface = MyPuMDark,

    surfaceVariant = MyPuMSurfaceVariant,
    onSurfaceVariant = MyPuMTextSecondary,

    outline = MyPuMOutline,
    outlineVariant = MyPuMOutlineVariant,

    error = MyPuMRed,
    onError = MyPuMWhite,

    errorContainer = MyPuMRedSoft,
    onErrorContainer = MyPuMRed
)

private val MyPuMDarkColorScheme = darkColorScheme(

    primary = MyPuMBlueLight,
    onPrimary = MyPuMDarkBackground,

    primaryContainer = MyPuMBlueDark,
    onPrimaryContainer = MyPuMWhite,

    secondary = ColorTealDark,
    onSecondary = MyPuMWhite,

    secondaryContainer = MyPuMTeal,
    onSecondaryContainer = MyPuMWhite,

    tertiary = MyPuMOrange,
    onTertiary = MyPuMDarkBackground,

    tertiaryContainer = ColorOrangeDark,
    onTertiaryContainer = MyPuMWhite,

    background = MyPuMDarkBackground,
    onBackground = MyPuMTextDark,

    surface = MyPuMDarkSurface,
    onSurface = MyPuMTextDark,

    surfaceVariant = MyPuMDarkSurfaceVariant,
    onSurfaceVariant = MyPuMTextSecondaryDark,

    outline = MyPuMOutlineDark,
    outlineVariant = MyPuMOutlineVariantDark,

    error = MyPuMRed,
    onError = MyPuMWhite,

    errorContainer = MyPuMRedSoft,
    onErrorContainer = MyPuMWhite
)

private val ColorTealDark = MyPuMTeal
private val ColorOrangeDark = Color(0xFFB45309)

@Composable
fun MyPuMTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            MyPuMDarkColorScheme
        } else {
            MyPuMLightColorScheme
        },
        typography = MyPuMTypography,
        shapes = MyPuMShapes,
        content = content
    )
}
