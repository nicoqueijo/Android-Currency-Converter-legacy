package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository

class ActiveCurrenciesViewModel_kt(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    val repository: Repository = Repository(application)
    var fabClicks: Int = 0
}