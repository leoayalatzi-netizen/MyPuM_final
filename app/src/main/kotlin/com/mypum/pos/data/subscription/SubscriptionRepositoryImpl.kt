package com.mypum.pos.data.subscription

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mypum.pos.domain.model.subscription.Plan
import com.mypum.pos.domain.repository.subscription.SubscriptionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.subscriptionStore by preferencesDataStore(
    name = "subscription"
)

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SubscriptionRepository {

    private val planKey = stringPreferencesKey("plan")

    override fun observePlan(): Flow<Plan> =
        context.subscriptionStore.data.map { preferences ->
            preferences[planKey]
                ?.let { value ->
                    runCatching {
                        Plan.valueOf(value)
                    }.getOrNull()
                }
                ?: Plan.FREE
        }

    override suspend fun getPlan(): Plan =
        observePlan().first()

    override suspend fun setPlan(plan: Plan) {
        context.subscriptionStore.edit { preferences ->
            preferences[planKey] = plan.name
        }
    }
}
