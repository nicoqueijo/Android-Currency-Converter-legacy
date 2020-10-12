package com.nicoqueijo.android.currencyconverter.kotlin.depinj

import com.nicoqueijo.android.currencyconverter.kotlin.data.ExchangeRateService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideExchangeRateService(retrofit: Retrofit): ExchangeRateService {
        return retrofit.create(ExchangeRateService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(baseUrl: String, moshiConverterFactory: MoshiConverterFactory): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(moshiConverterFactory)
                .build()
    }

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideBaseUrl(): String {
        return ExchangeRateService.BASE_URL
    }
}