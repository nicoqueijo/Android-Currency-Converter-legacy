package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nicoqueijo.android.currencyconverter.kotlin.app.MyApplication
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import javax.inject.Inject

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var repository: Repository

    init {
        (application.applicationContext as MyApplication).getAppComponent().inject(this)
    }

    suspend fun initCurrencies() {
        repository.initCurrencies()
    }
}