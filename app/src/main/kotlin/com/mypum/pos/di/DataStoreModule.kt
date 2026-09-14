package com.mypum.pos.di
import android.content.Context
import com.mypum.pos.data.datastore.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
@Module @InstallIn(SingletonComponent::class) object DataStoreModule {
 @Provides fun settings(@ApplicationContext c:Context)=SettingsDataStore(c)
 @Provides fun session(@ApplicationContext c:Context)=SessionDataStore(c)
}
