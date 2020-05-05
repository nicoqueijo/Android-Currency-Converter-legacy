package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    suspend fun initCurrencies() {
        repository.initCurrencies()
    }
}