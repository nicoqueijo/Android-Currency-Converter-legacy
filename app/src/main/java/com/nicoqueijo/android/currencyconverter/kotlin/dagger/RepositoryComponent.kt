package com.nicoqueijo.android.currencyconverter.kotlin.dagger

import android.content.SharedPreferences
import com.nicoqueijo.android.currencyconverter.kotlin.data.CurrencyDao
import com.nicoqueijo.android.currencyconverter.kotlin.data.ExchangeRateService
import dagger.Component

@Component(modules = [NetworkModule::class, DatabaseModule::class, SharedPreferencesModule::class, ContextModule::class])
interface RepositoryComponent {
    fun getExchangeRateService(): ExchangeRateService
    fun getCurrencyDao(): CurrencyDao
    fun getSharedPreferences(): SharedPreferences
}