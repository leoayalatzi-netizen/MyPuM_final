package com.mypum.pos.domain.repository.subscription

import com.mypum.pos.domain.model.subscription.Plan
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {

    fun observePlan(): Flow<Plan>

    suspend fun getPlan(): Plan

    suspend fun setPlan(plan: Plan)
}
