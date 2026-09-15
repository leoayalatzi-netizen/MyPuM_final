package com.mypum.pos.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

val MyPuMTypography = Typography().run {
    copy(
        headlineLarge = headlineLarge.copy(fontWeight = FontWeight.Bold),
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold)
    )
}
