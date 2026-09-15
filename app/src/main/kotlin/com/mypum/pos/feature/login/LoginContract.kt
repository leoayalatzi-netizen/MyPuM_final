package com.mypum.pos.feature.login

import com.mypum.pos.core.UiEffect
import com.mypum.pos.core.UiEvent
import com.mypum.pos.core.UiState

 data class LoginContractState(
    val loading: Boolean = false,
    val userId: Long? = null,
    val userName: String = "",
    val message: String? = null
) : UiState

sealed interface LoginContractEvent : UiEvent
sealed interface LoginContractEffect : UiEffect
