package com.mypum.pos.feature.subscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.data.subscription.GooglePlayBillingManager
import com.mypum.pos.domain.model.subscription.Plan
import com.mypum.pos.domain.repository.subscription.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ProViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository,
    private val billingManager: GooglePlayBillingManager
) : ViewModel() {

    val plan: StateFlow<Plan> =
        subscriptionRepository
            .observePlan()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Plan.FREE
            )

    val message = billingManager.message

    init {
        billingManager.refreshPurchases()
    }

    fun comprarPro(activity: Activity) {
        billingManager.launchProPurchase(activity)
    }
}
