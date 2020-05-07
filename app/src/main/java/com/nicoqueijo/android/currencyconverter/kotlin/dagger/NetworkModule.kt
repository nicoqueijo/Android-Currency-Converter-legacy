package com.nicoqueijo.android.currencyconverter.kotlin.dagger

import com.nicoqueijo.android.currencyconverter.kotlin.data.ExchangeRateService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class NetworkModule {

    @Provides
    fun provideExchangeRateService(): ExchangeRateService {
        return provideRetrofit().create(ExchangeRateService::class.java)
    }

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(provideBaseUrl())
                .addConverterFactory(provideMoshiConverterFactory())
                .build()
    }

    @Provides
    fun provideMoshiConverterFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create()
    }

    @Provides
    fun provideBaseUrl(): String {
        return ExchangeRateService.BASE_URL
    }
}