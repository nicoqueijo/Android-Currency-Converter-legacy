package com.nicoqueijo.android.currencyconverter.kotlin.app

import android.app.Application
import com.nicoqueijo.android.currencyconverter.kotlin.dagger.ApplicationComponent
import com.nicoqueijo.android.currencyconverter.kotlin.dagger.ApplicationScope
import com.nicoqueijo.android.currencyconverter.kotlin.dagger.ContextModule
import com.nicoqueijo.android.currencyconverter.kotlin.dagger.DaggerApplicationComponent

@ApplicationScope
class MyApplication : Application() {

    private lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent
                .builder()
                .contextModule(ContextModule(applicationContext))
                .build()
    }

    fun getAppComponent(): ApplicationComponent {
        return appComponent
    }
}