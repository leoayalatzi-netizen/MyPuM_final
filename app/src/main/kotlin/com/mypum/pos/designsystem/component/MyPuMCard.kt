package com.mypum.pos.designsystem.component

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable

@Composable
fun MyPuMCard(
    content: @Composable () -> Unit
) {
    Card {
        content()
    }
}
