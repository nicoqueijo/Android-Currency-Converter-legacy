package com.nicoqueijo.android.currencyconverter.kotlin.depinj

import android.content.Context
import com.nicoqueijo.android.currencyconverter.kotlin.data.AppPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object SharedPreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): AppPrefs {
        return AppPrefs(context)
    }
}