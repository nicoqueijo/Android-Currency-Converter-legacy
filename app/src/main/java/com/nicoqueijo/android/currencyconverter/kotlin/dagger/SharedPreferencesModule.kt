package com.nicoqueijo.android.currencyconverter.kotlin.dagger

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
class SharedPreferencesModule {

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(provideName(context), provideMode())
    }

    @Provides
    fun provideName(context: Context): String {
        return context.packageName + ".properties"
    }

    @Provides
    fun provideMode(): Int {
        return Context.MODE_PRIVATE
    }
}