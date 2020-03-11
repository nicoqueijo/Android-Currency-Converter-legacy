package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository

class LoadingCurrenciesViewModel_kt(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    val repository = Repository(application)
}