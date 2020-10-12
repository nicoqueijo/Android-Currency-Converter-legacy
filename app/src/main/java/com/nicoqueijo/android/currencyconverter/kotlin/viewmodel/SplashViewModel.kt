package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped

@ActivityRetainedScoped
class SplashViewModel @ViewModelInject constructor(
        private val repository: Repository,
        application: Application) : AndroidViewModel(application) {

    suspend fun initCurrencies() {
        repository.initCurrencies()
    }
}