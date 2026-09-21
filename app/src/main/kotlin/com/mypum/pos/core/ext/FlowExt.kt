package com.mypum.pos.core.ext
import kotlinx.coroutines.flow.Flow
fun <T> Flow<T>.asUiState()=this
