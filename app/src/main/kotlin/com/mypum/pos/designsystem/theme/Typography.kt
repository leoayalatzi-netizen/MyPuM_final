package com.mypum.pos.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MyPuMTypography = Typography().run {

    copy(

        displayLarge = displayLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            lineHeight = 46.sp,
            letterSpacing = (-0.8).sp
        ),

        displayMedium = displayMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.5).sp
        ),

        headlineLarge = headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            letterSpacing = (-0.4).sp
        ),

        headlineMedium = headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 32.sp
        ),

        headlineSmall = headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),

        titleLarge = titleLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 26.sp
        ),

        titleMedium = titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),

        titleSmall = titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),

        bodyLarge = bodyLarge.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),

        bodyMedium = bodyMedium.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),

        bodySmall = bodySmall.copy(
            fontSize = 12.sp,
            lineHeight = 18.sp
        ),

        labelLarge = labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        ),

        labelMedium = labelMedium.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        ),

        labelSmall = labelSmall.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        )
    )
}
