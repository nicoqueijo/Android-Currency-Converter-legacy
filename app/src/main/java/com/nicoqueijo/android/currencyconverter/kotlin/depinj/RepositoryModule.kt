package com.nicoqueijo.android.currencyconverter.kotlin.depinj

import android.content.Context
import android.content.SharedPreferences
import com.nicoqueijo.android.currencyconverter.kotlin.data.CurrencyDao
import com.nicoqueijo.android.currencyconverter.kotlin.data.ExchangeRateService
import com.nicoqueijo.android.currencyconverter.kotlin.data.DefaultRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
            @ApplicationContext context: Context,
            exchangeRateService: ExchangeRateService,
            currencyDao: CurrencyDao,
            sharedPreferences: SharedPreferences): DefaultRepository {
        return DefaultRepository(context, exchangeRateService, currencyDao, sharedPreferences)
    }
}