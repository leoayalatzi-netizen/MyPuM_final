package com.mypum.pos.di
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
@Module @InstallIn(SingletonComponent::class) object NetworkModule {
 @Provides fun json()=Json{ignoreUnknownKeys=true}
 @Provides fun client()=OkHttpClient.Builder().build()
 @Provides fun retrofit(json:Json,client:OkHttpClient)=Retrofit.Builder().baseUrl("https://example.invalid/").client(client).addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
}
