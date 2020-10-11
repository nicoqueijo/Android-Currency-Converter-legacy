package com.nicoqueijo.android.currencyconverter.kotlin.app

import android.app.Application
import com.nicoqueijo.android.currencyconverter.kotlin.depinj.ApplicationComponent
import com.nicoqueijo.android.currencyconverter.kotlin.depinj.ApplicationScope
import com.nicoqueijo.android.currencyconverter.kotlin.depinj.ContextModule
import com.nicoqueijo.android.currencyconverter.kotlin.depinj.DaggerApplicationComponent

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