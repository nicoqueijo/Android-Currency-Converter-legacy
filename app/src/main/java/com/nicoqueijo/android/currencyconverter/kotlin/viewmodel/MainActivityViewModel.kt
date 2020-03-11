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
    private val repository = Repository(application)

    private val _allCurrencies = repository.getAllCurrencies()
    val allCurrencies: LiveData<MutableList<Currency>>
        get() = _allCurrencies

    private val _activeCurrencies = repository.getActiveCurrencies()
    val activeCurrencies: LiveData<MutableList<Currency>>
        get() = _activeCurrencies

    fun upsertCurrency(currency: Currency) {
        repository.upsertCurrency(currency)
    }

    val _activeFragment = MutableLiveData(R.id.loadingCurrenciesFragment_kt)
    val activeFragment: LiveData<Int>
        get() = _activeFragment

    val fragmentBackstackEntries: MutableSet<Int> = mutableSetOf()

}