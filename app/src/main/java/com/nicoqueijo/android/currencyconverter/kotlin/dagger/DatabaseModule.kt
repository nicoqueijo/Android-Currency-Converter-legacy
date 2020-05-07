package com.nicoqueijo.android.currencyconverter.kotlin.dagger

import android.content.Context
import com.nicoqueijo.android.currencyconverter.kotlin.data.CurrencyDao
import com.nicoqueijo.android.currencyconverter.kotlin.data.CurrencyDatabase
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
class DatabaseModule {

    @Provides
    fun provideCurrencyDatabase(context: Context): CurrencyDatabase {
        return CurrencyDatabase.getInstance(context)
    }

    @Provides
    fun provideCurrencyDao(context: Context): CurrencyDao {
        return provideCurrencyDatabase(context).currencyDao()
    }
}