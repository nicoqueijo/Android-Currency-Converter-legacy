package com.nicoqueijo.android.currencyconverter.kotlin.depinj

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }
}
