package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    val repository: Repository = Repository(application)

    private val _currencies = repository.getAllCurrencies()
    val currencies: LiveData<List<Currency>>
        get() = _currencies

    val _activeFragment: MutableLiveData<Int> = MutableLiveData(R.id.loadingCurrenciesFragment_kt)
    val activeFragment: LiveData<Int>
        get() = _activeFragment
    val fragmentBackstackEntries: MutableSet<Int> = mutableSetOf()

}