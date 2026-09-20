package com.mypum.pos.di

import com.mypum.pos.data.subscription.SubscriptionRepositoryImpl
import com.mypum.pos.domain.repository.subscription.SubscriptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SubscriptionModule {

    @Binds
    abstract fun bindSubscriptionRepository(
        implementation: SubscriptionRepositoryImpl
    ): SubscriptionRepository
}
