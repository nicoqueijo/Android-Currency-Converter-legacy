package com.nicoqueijo.android.currencyconverter.kotlin.depinj

import android.content.Context
import com.nicoqueijo.android.currencyconverter.kotlin.data.*
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
            appPrefs: AppPrefs): Repository {
        return DefaultRepository(context, exchangeRateService, currencyDao, appPrefs)
    }
}