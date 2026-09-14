package com.mypum.pos.di
import com.mypum.pos.core.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class) object AppModule {
 @Provides @Singleton fun dispatcherProvider():DispatcherProvider=DefaultDispatcherProvider()
 @Provides @Singleton fun clockProvider():ClockProvider=SystemClockProvider()
}
